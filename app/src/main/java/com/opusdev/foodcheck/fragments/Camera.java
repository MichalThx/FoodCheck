package com.opusdev.foodcheck.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.*;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.opusdev.foodcheck.AutoFitTextureView;
import com.opusdev.foodcheck.MainActivity;
import com.opusdev.foodcheck.R;
import com.opusdev.foodcheck.viewmodels.SharedViewModel;
import com.opusdev.foodcheck.tensorflow.Tensorflow;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Basic fragments for the Camera.
 * Based on the code from
 * https://github.com/tensorflow/examples/tree/master/lite/examples/image_classification/android/lib_support/src/main/java/org/tensorflow/lite/examples/classification/tflite
 */
public class Camera extends Fragment {

	/**
	 * Dialog Popup
	 */
	public static final int NUM_DISPLAYED_AFTER_CLASSIFIER = 5;
	/**
	 * Tag for the {@link Log}.
	 */
	private static final String TAG = "Camera_Fragment";
	private static final String HANDLE_THREAD_NAME = "CameraBackground";
	/**
	 * Max preview width that is guaranteed by Camera2 API
	 */
	private static final int MAX_PREVIEW_WIDTH = 1920;
	/**
	 * Max preview height that is guaranteed by Camera2 API
	 */
	private static final int MAX_PREVIEW_HEIGHT = 1080;
	private final Object lock = new Object();
	/**
	 * A {@link Semaphore} to prevent the app from exiting before closing the camera.
	 */
	private final Semaphore cameraOpenCloseLock = new Semaphore(1);
	/**
	 * A {@link CameraCaptureSession.CaptureCallback} that handles events related to capture.
	 */
	private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

		@Override
		public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
		}

		@Override
		public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
		}
	};
	List<Tensorflow.Results> list;
	RelativeLayout layout;
	Activity activity;
	Tensorflow tensorflow;
	boolean isUsingCamera2API;
	ArrayList<String> searchValues;
	private Integer sensorOrientation;
	private CameraManager manager;
	private WindowManager windowManager;
	private SharedViewModel model;
	/**
	 * ID of the current {@link CameraDevice}.
	 */
	private String cameraId;
	/**
	 * An {@link AutoFitTextureView} for camera preview.
	 */
	private AutoFitTextureView textureView;
	/**
	 * A {@link CameraCaptureSession } for camera preview.
	 */
	private CameraCaptureSession captureSession;
	/**
	 * A reference to the opened {@link CameraDevice}.
	 */
	private CameraDevice cameraDevice;
	/**
	 * The {@link android.util.Size} of camera preview.
	 */
	private Size previewSize;
	/**
	 * An {@link ImageReader} that handles image capture.
	 */
	private ImageReader imageReader;
	/**
	 * {@link CaptureRequest.Builder} for the camera preview
	 */
	private CaptureRequest.Builder previewRequestBuilder;
	/**
	 * {@link CaptureRequest} generated by {@link #previewRequestBuilder}
	 */
	private CaptureRequest previewRequest;
	/**
	 * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
	 */
	private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

		@Override
		public void onOpened(@NonNull CameraDevice currentCameraDevice) {
			cameraOpenCloseLock.release();
			cameraDevice = currentCameraDevice;
			createCameraPreviewSession();
		}

		@Override
		public void onDisconnected(@NonNull CameraDevice currentCameraDevice) {
			cameraOpenCloseLock.release();
			currentCameraDevice.close();
			cameraDevice = null;
		}

		@Override
		public void onError(@NonNull CameraDevice currentCameraDevice, int error) {
			cameraOpenCloseLock.release();
			currentCameraDevice.close();
			cameraDevice = null;
			Activity activity = getActivity();
			if (null != activity) {
				activity.finish();
			}
		}
	};
	/**
	 * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
	 * TextureView}.
	 */
	private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
			openCamera(width, height);
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
			configureTransform(width, height);
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			return true;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
		}
	};

	public static Fragment newInstance() {
		return new Camera();
	}

	private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

		// Collect the supported resolutions that are at least as big as the preview Surface
		List<Size> bigEnough = new ArrayList<>();
		// Collect the supported resolutions that are smaller than the preview Surface
		List<Size> notBigEnough = new ArrayList<>();
		int w = aspectRatio.getWidth();
		int h = aspectRatio.getHeight();
		Log.i(TAG, "chooseOptimalSize: w = " + w + "  " + h);
		for (Size option : choices) {
			if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && option.getHeight() == option.getWidth() * h / w) {
				if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
					bigEnough.add(option);
				} else {
					notBigEnough.add(option);
				}
			}
		}

		// Pick the smallest of those big enough. If there is no one big enough, pick the
		// largest of those not big enough.
		if (bigEnough.size() > 0) {
			return Collections.min(bigEnough, new CompareSizesByArea());
		} else if (notBigEnough.size() > 0) {
			return Collections.max(notBigEnough, new CompareSizesByArea());
		} else {
			Log.e(TAG, "Couldn't find any suitable preview size");
			return choices[0];
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		searchValues = new ArrayList<>();
		model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try {
			tensorflow = new Tensorflow(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inflater.inflate(R.layout.fragement_camera, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {

		Log.i(TAG, "Created TextureView");
		textureView = view.findViewById(R.id.texture);
		list = new ArrayList<>();
		layout = view.findViewById(R.id.camera_fragment);
		setupCamera(view);
		layout.setOnClickListener(view1 -> {
			if (list != null) list.clear();
			long start = System.currentTimeMillis();
			list = tensorflow.classifyFrame(textureView, sensorOrientation);
			long end = System.currentTimeMillis();
			String timeDiff = (end - start) / 1000f + " s";
			Snackbar.make(view1, "Classification has been done in " + timeDiff, Snackbar.LENGTH_LONG).show();
			Dialog dialog = onCreateDialog(list);
			dialog.show();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (textureView.isAvailable()) {
			openCamera(textureView.getWidth(), textureView.getHeight());
		} else {
			textureView.setSurfaceTextureListener(surfaceTextureListener);
		}
	}

	@Override
	public void onPause() {
		closeCamera();
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onDestroy();
	}

	public Dialog onCreateDialog(final List<Tensorflow.Results> list) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		int size = Math.min(NUM_DISPLAYED_AFTER_CLASSIFIER, list.size());
		String[] choicesArray = new String[size];
		ArrayList<String> searched = new ArrayList<>();
		int i = 0;
		while (i < size) {
			Tensorflow.Results r = list.get(i);
			choicesArray[i] = r.getLabel();
			i++;
		}
		boolean[] checkedItems = new boolean[size];
		builder.setTitle("I Spy With My Little Eye:").setMultiChoiceItems(choicesArray, checkedItems, (dialog, which, isChecked) -> {
			if (isChecked) {
				searched.add(list.get(which).getLabel());
			} else {
				searched.remove(list.get(which).getLabel());
			}
		}).setNegativeButton("Add more", (dialog, id) -> {
			updateSearchValue(searched);
			model.change(getSearchString());
		}).setPositiveButton("Search", (dialog, id) -> {
			updateSearchValue(searched);
			model.change(getSearchString());
			((MainActivity) activity).getVpPager().setCurrentItem(3, true);
		}).setNeutralButton("Retake Picture", null);
		return builder.create();
	}

	public void updateSearchValue(ArrayList<String> selected) {
		HashSet<String> set = new HashSet<>(searchValues);
		set.addAll(selected);
		searchValues.clear();
		searchValues.addAll(set);
	}

	public String getSearchString() {
		StringBuilder sb = new StringBuilder();
		String delimiter = "";
		for (String s : searchValues) {
			sb.append(delimiter);
			sb.append(s);
			delimiter = ", ";
		}
		return sb.toString();
	}

	/**
	 * Sets up member variables related to camera.
	 *
	 * @param width  The width of available size for camera preview
	 * @param height The height of available size for camera preview
	 */
	private void setUpCameraOutputs(int width, int height) {
		try {
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

			StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			if (map == null) return;

			// For still image captures, we use the largest available size.
			Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
			imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/ 2);

			// Find out if we need to swap dimension to get the preview size relative to sensor
			// coordinate.
			int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
			Log.i("ORIENTATION", String.valueOf(displayRotation));

			boolean swappedDimensions = false;
			switch (displayRotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_180:
					if (sensorOrientation == 90 || sensorOrientation == 270) {
						swappedDimensions = false;
					}
					break;
				case Surface.ROTATION_90:
				case Surface.ROTATION_270:
					if (sensorOrientation == 0 || sensorOrientation == 180) {
						swappedDimensions = true;
					}
					break;
				default:
					Log.e(TAG, "Display rotation is invalid: " + displayRotation);
			}

			Point displaySize = new Point();

			int rotatedPreviewWidth = width;
			int rotatedPreviewHeight = height;
			int maxPreviewWidth = displaySize.x;
			int maxPreviewHeight = displaySize.y;

			if (swappedDimensions) {
				rotatedPreviewWidth = height;
				rotatedPreviewHeight = width;
				maxPreviewWidth = displaySize.y;
				maxPreviewHeight = displaySize.x;
			}

			if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
				maxPreviewWidth = MAX_PREVIEW_WIDTH;
			}

			if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
				maxPreviewHeight = MAX_PREVIEW_HEIGHT;
			}

			previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);

			// We fit the aspect ratio of TextureView to the size of preview we picked.
			int orientation = getResources().getConfiguration().orientation;
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
			} else {
				textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
			}

			textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());


		} catch (CameraAccessException | NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configures the necessary {@link android.graphics.Matrix} transformation to `textureView`. This
	 * method should be called after the camera preview size is determined in setUpCameraOutputs and
	 * also the size of `textureView` is fixed.
	 *
	 * @param viewWidth  The width of `textureView`
	 * @param viewHeight The height of `textureView`
	 */
	private void configureTransform(int viewWidth, int viewHeight) {

		if (null == textureView || null == previewSize || null == activity) {
			return;
		}
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		Matrix matrix = new Matrix();
		RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
		RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
		float centerX = viewRect.centerX();
		float centerY = viewRect.centerY();
		if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
			bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
			matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
			float scale = Math.max((float) viewHeight / previewSize.getHeight(), (float) viewWidth / previewSize.getWidth());
			matrix.postScale(scale, scale, centerX, centerY);
			matrix.postRotate(90 * (rotation - 2), centerX, centerY);
		} else if (Surface.ROTATION_180 == rotation) {
			matrix.postRotate(180, centerX, centerY);
		}
		textureView.setTransform(matrix);
	}

	public void setupCamera(View view) {

		manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		sensorOrientation = getScreenOrientation();
		cameraId = chooseCamera();
		if (cameraId == null) {
			Snackbar.make(view, "Sorry, this application will not work with.", Snackbar.LENGTH_LONG).show();
			Log.i(TAG, "Not Camera2 API compatible device");
		}
	}

	private boolean isHardwareLevelSupported(CameraCharacteristics characteristics) {
		int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
		if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
			return CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL == deviceLevel;
		}
		// deviceLevel is not LEGACY, can use numerical sort
		return CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL <= deviceLevel;
	}

	private String chooseCamera() {

		try {
			for (final String cameraId : manager.getCameraIdList()) {
				final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);


				final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

				if (facing == null) continue;
				if (characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) == null) continue;


				isUsingCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) || isHardwareLevelSupported(characteristics);

				return isUsingCamera2API ? cameraId : null;
			}
		} catch (CameraAccessException e) {
			Log.i(TAG, "Error checking Camera Access");
		}

		return null;
	}

	/**
	 * Opens the camera specified by {@link Camera#cameraId}.
	 */
	@SuppressLint("MissingPermission")
	private void openCamera(int width, int height) {

		Log.d(TAG, "Open Camera has been launched");

		setUpCameraOutputs(width, height);
		configureTransform(width, height);

		try {
			if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
				throw new RuntimeException("Time out waiting to lock camera opening.");
			}
			if (cameraId == null) return;
			manager.openCamera(cameraId, stateCallback, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
		}
	}

	protected int getScreenOrientation() {
		switch (windowManager.getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_270:
				return 270;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_90:
				return 90;
			default:
				return 0;
		}
	}

	/**
	 * Creates a new {@link CameraCaptureSession} for camera preview.
	 */
	private void createCameraPreviewSession() {
		try {
			SurfaceTexture texture = textureView.getSurfaceTexture();
			assert texture != null;

			// We configure the size of default buffer to be the size of camera preview we want.
			texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

			// This is the output Surface we need to start preview.
			Surface surface = new Surface(texture);

			// We set up a CaptureRequest.Builder with the output Surface.
			previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			previewRequestBuilder.addTarget(surface);

			// Here, we create a CameraCaptureSession for camera preview.
			cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

				@Override
				public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
					// The camera is already closed
					if (null == cameraDevice) {
						return;
					}

					// When the session is ready, we start displaying the preview.
					captureSession = cameraCaptureSession;
					try {
						// Autofocus should be continuous for camera preview.
						previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

						// Finally, we start displaying the camera preview.
						previewRequest = previewRequestBuilder.build();
						captureSession.setRepeatingRequest(previewRequest, captureCallback, null);
					} catch (CameraAccessException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
					Log.i(TAG, "Failed creating Camera Session");
				}
			}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current {@link CameraDevice}.
	 */
	private void closeCamera() {
		try {
			cameraOpenCloseLock.acquire();
			if (null != captureSession) {
				captureSession.close();
				captureSession = null;
			}
			if (null != cameraDevice) {
				cameraDevice.close();
				cameraDevice = null;
			}
			if (null != imageReader) {
				imageReader.close();
				imageReader = null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
		} finally {
			cameraOpenCloseLock.release();
		}
	}

	/**
	 * Compares two {@code Size}s based on their areas.
	 */
	private static class CompareSizesByArea implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			// We cast here to ensure the multiplications won't overflow
			return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
		}
	}
}