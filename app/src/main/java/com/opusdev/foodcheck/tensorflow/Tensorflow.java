package com.opusdev.foodcheck.tensorflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;

public class Tensorflow {

	private static final String TAG = "TensorflowModel";

	private static final String MODEL_PATH = "model5.lite";
	private static final String LABEL_PATH = "labels5.txt";

	/**
	 * The quantized model does not require normalization, thus set mean as 0.0f, and std as 1.0f to
	 * bypass the normalization.
	 */
	private static final float IMAGE_MEAN = 0.0f;
	private static final float IMAGE_STD = 1.0f;

	/**
	 * Quantized MobileNet requires additional dequantization to the output probability.
	 */
	private static final float PROBABILITY_MEAN = 0.0f;
	private static final float PROBABILITY_STD = 255.0f;

	private static final int TOP_K_RESULTS = 2;
	private final int TENSOR_SIZE_X = 299;
	private final int TENSOR_SIZE_Y = 299;
	private final Interpreter interpreter;
	private final List<String> labels;
	private TensorImage inputImageBuffer;
	private final TensorBuffer outputProbabilityBuffer;
	private final TensorProcessor probabilityProcessor;
	private final int imageSizeY;
	private final int imageSizeX;

	public Tensorflow(Activity activity) throws IOException {
		MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(activity, MODEL_PATH);
		interpreter = new Interpreter(tfliteModel);
		labels = FileUtil.loadLabels(activity, LABEL_PATH);

		// Reads type and shape of input and output tensors, respectively.
		int imageTensorIndex = 0;
		int[] imageShape = interpreter.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
		imageSizeY = imageShape[1];
		imageSizeX = imageShape[2];
		DataType imageDataType = interpreter.getInputTensor(imageTensorIndex).dataType();
		int probabilityTensorIndex = 0;
		int[] probabilityShape = interpreter.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
		DataType probabilityDataType = interpreter.getOutputTensor(probabilityTensorIndex).dataType();

		// Creates the input tensor.
		inputImageBuffer = new TensorImage(imageDataType);

		// Creates the output tensor and its processor.
		outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

		// Creates the post processor for the output probability.
		probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessingNormalizeOp()).build();
	}

	private static List<Results> getTopKProbability(Map<String, Float> labelProb, int k) {

		ArrayList<Results> results = new ArrayList<>();

		for (Map.Entry<String, Float> entry : labelProb.entrySet()) {
			String label = entry.getKey();
			float value = entry.getValue();
			results.add(new Results(label, value));
		}

		Collections.sort(results, (a, b) -> (int) (a.value - b.value));
		return results.subList(0, k + 1);
	}

	/**
	 * Classifies a frame from the preview stream.
	 */
	public List<Results> classifyFrame(TextureView textureView, Context context,
								  int sensorOrientation) {
		Log.d(TAG, "Started classify");
		Bitmap bitmap = textureView.getBitmap(TENSOR_SIZE_X, TENSOR_SIZE_Y);
		inputImageBuffer = loadImage(bitmap, sensorOrientation);

		// Runs the inference call.
		interpreter.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

		// Gets the map of label and probability.
		Map<String, Float> labeledProbability = new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).getMapWithFloatValue();

		return getTopKProbability(labeledProbability, TOP_K_RESULTS);
	}

	private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
		// Loads bitmap into a TensorImage.
		inputImageBuffer.load(bitmap);

		// Creates processor for the TensorImage.
		int cropSize = min(bitmap.getWidth(), bitmap.getHeight());
		int numRotation = sensorOrientation / 90;
		ImageProcessor imageProcessor = new ImageProcessor.Builder().add(new ResizeWithCropOrPadOp(cropSize, cropSize)).add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)).add(new Rot90Op(numRotation)).add(getPreprocessingNormalizeOp()).build();
		return imageProcessor.process(inputImageBuffer);
	}

	protected TensorOperator getPreprocessingNormalizeOp() {
		return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
	}

	protected TensorOperator getPostprocessingNormalizeOp() {
		return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
	}

	public static class Results {

		String label;
		float value;

		public Results(String label, float value) {
			this.label = label;
			this.value = value;
		}
	}
}

