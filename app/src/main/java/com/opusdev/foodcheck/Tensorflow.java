//package com.opusdev.foodcheck;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.AssetFileDescriptor;
//import android.graphics.Bitmap;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.tensorflow.lite.Interpreter;
//
//import javax.security.auth.login.LoginException;
//
//public class Tensorflow {
//    private static final String NAME = "model5.lite";
//    private static final String LabelPath = "labels5.txt";
//    // Display preferences
//    Interpreter tflite;
//    Context context;
//    float probabilitiesArray [][] = null;
//    List<String> labelList;
//    float[] probArray;
//    ByteBuffer buffer;
//    boolean isFinished;
//
//    public Tensorflow(Context context, Bitmap bitmap) throws IOException {
//        this.context = context;
//        labelList = loadLabelledList(this.context);
//        probArray = new float[labelList.size()];
//        probabilitiesArray = new float[1][labelList.size()];
//        tflite = new Interpreter(loadModuleFile());
//        buffer = ByteBuffer.allocateDirect(4 * 299 * 299 *1 * 3);
//        isFinished = false;
//        //buffer.order(ByteOrder.nativeOrder());
//        if(tflite == null){
////            Log.i("Ten","WHAT?");
//        }else{
////            Log.i("Ten","Or we gucci");
//        }
//
//        Log.i("i", String.valueOf(probabilitiesArray[0][1]));
//
//        doInference(bitmap);
//        Log.i("i", String.valueOf(probabilitiesArray[0][1]));
//
////            Log.d("Tensorflow","cant start2");
//            showResults();
////            Log.d("Tensorflow","cant start3");
//            close();
//            isFinished = true;
//
//
//
//    }
//
//    public Tensorflow(Context context) throws IOException {
//        this.context = context;
//        labelList = loadLabelledList(this.context);
//        probArray = new float[labelList.size()];
//        probabilitiesArray = new float[1][labelList.size()];
//        tflite = new Interpreter(loadModuleFile());
//        buffer = ByteBuffer.allocateDirect(4 * 299 * 299 *1 * 3);
//    }
//    public float[][] inference(Bitmap bmp){
//        doInference(bmp);
//        return probabilitiesArray;
//    }
//
//    /**This Method is used to manipulae Interpreter from other classes
//     */
//    protected Interpreter getInterpreter(){
//        return  tflite;
//    }
//    /**This Method does the inference of the model
//     * */
//    protected void doInference(Bitmap bitmap){
//        convertBitmapToByteBuffer(bitmap);
//        tflite.run(buffer, probabilitiesArray);
//        buffer.clear();
//        //buffer.clear();
//    };
//
//    /**after labelling closes the Interpreter
//     */
//    public void close(){
//        tflite.close();
//        tflite = null;
//    }
//
//
//    /** Below are methods used for loading files: tflite and labels from asset
//     * And converting images to streams
//     */
//    protected MappedByteBuffer loadModuleFile() throws IOException{
//        //Getting tensorfile from assets folder
//        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(NAME);
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//       // Log.i("Tensorflow",String.valueOf(declaredLength));
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//    }
//    /** This method load list of labels to which the objects have to be assigned
//    */
//    protected List<String> loadLabelledList(Context context) throws IOException{
//        List<String> labelledList = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(LabelPath)));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            labelledList.add(line);
//        }
//        reader.close();
//        return labelledList;
//    }
//    //protected
//    /*TODO: 1. Finish bitmap to bytebuffer. 2.Finish connecting them all 3.Start tensorflow??
//    * */
//    protected void convertBitmapToByteBuffer(Bitmap bitmap) {
////        int width = bitmap.getWidth();
////        int height = bitmap.getHeight();
////        int size = bitmap.getRowBytes() * height;
////        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
////        bitmap.copyPixelsToBuffer(byteBuffer);
////
////        return byteBuffer;
//        //buffer.rewind();
////        Log.i("Tensorflow",String.valueOf(bitmap.getHeight()));
////        Log.i("Tensorflow",String.valueOf(bitmap.getWidth()));
//        int[] intValues = new int[bitmap.getWidth() * bitmap.getHeight()];
////        Log.i("BMP1", String.valueOf(bitmap.getPixel(1,1)));
////        Log.i("BMP1", String.valueOf(bitmap.getPixel(100,100)));
////        Log.i("BMP1", String.valueOf(bitmap.getPixel(200,200)));
//        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//        int pixel = 0;
//        for (int i = 0; i < 299; ++i) {
//            for (int j = 0; j < 299; ++j) {
//                final int val = intValues[pixel++];
//                buffer.putFloat((((val >> 16) & 0xFF) - 128) / 128.0f);
//                buffer.putFloat((((val >> 8) & 0xFF) - 128) / 128.0f);
//                buffer.putFloat((((val) & 0xFF) - 128) / 128.0f);
//            }
//        }
////        return buffer;
//    }
//
//    public boolean isFinished(){
//        return isFinished;
//    }
//    /** Temporary method created for showing results of inference in Log files
//     * */
//    protected void showResults(){
//        //Adding results; this part has 10% thrershold
//        ArrayList<String[]> results = new ArrayList<String[]>();
//       // Log.d("Tensorflow",String.valueOf(probabilitiesArray[0].length));
//
//        float max = 0f;
//        for(int j = 0; j < probabilitiesArray[0].length; j++){
//            //here might be an error comparing float with double?
//            //if(probabilitiesArray[0][j] > 0.1f){
//                if(probabilitiesArray[0][j] > max){
//                    max = probabilitiesArray[0][j];
//                    results.add(new String[]{labelList.get(j),String.valueOf(max)});
//                }
//                //results.add(new String[]{labelList.get(j),String.valueOf(probabilitiesArray[0][j])});
//            //}
//        }
////        Log.d("Tensorflow", "-------RIGHT BELOW--------");
//        String result = "";
//        for(int i = results.size()-1; i > 0; i--){
//            result += " "+results.get(i)[0]+ " : "+results.get(i)[1] + "\n";
//        }
//        for(int i = 0; i < results.size()-1; i++){
//            Log.i("Results" , results.get(i)[0]+" /--/ "+results.get(i)[1]);
//        }
////        for(int i = 0; i < probabilitiesArray[0].length; i++){
////            Log.i("probalitiesArray", String.valueOf(i)+"  "+probabilitiesArray[0][i]);
////        }
//        probabilitiesArray = null;
//
//        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
//
//    }
//
//
//
//}
