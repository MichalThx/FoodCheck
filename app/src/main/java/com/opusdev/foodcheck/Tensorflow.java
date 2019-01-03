package com.opusdev.foodcheck;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.tensorflow.lite.Interpreter;

public class Tensorflow {
    private static final String NAME = "tensorflow.lite";
    private static final String LabelPath = "labels.txt";
    // Display preferences
    Interpreter tflite;
    Context context;
    float probabilitiesArray [][] = null;
    List<String> labelList;
    float[] probArray;

    public Tensorflow(Context context, Bitmap bitmap) throws IOException {
        this.context = context;
        labelList = loadLabelledList(this.context);
        probArray = new float[labelList.size()];
        try{
            tflite = new Interpreter(loadModuleFile());
            doInference(bitmap);
            showResults();
            close();
        }catch (Exception e){

        }
    }
    /**This Method is used to manipulae Interpreter from other classes
     */
    protected Interpreter getInterpreter(){
        return  tflite;
    }
    /**This Method does the inference of the model
     * */
    protected void doInference(Bitmap bitmap){
        tflite.run( convertBitmapToByteBuffer(bitmap), probArray);
    };

    /**after labelling closes the Interpreter
     */
    public void close(){
        tflite.close();
        tflite = null;
    }


    /** Below are methods used for loading files: tflite and labels from asset
     * And converting images to streams
     */
    protected MappedByteBuffer loadModuleFile() throws IOException{
        //Getting tensorfile from assets folder
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    /** This method load list of labels to which the objects have to be assigned
    */
    protected List<String> loadLabelledList(Context context) throws IOException{
        List<String> labelledList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(LabelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelledList.add(line);
        }
        reader.close();
        return labelledList;
    }
    //protected
    /*TODO: 1. Finish bitmap to bytebuffer. 2.Finish connecting them all 3.Start tensorflow??
    * */
    protected ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap){
       int width = bitmap.getWidth();
       int height = bitmap.getHeight();

       int size = bitmap.getRowBytes()*height;
       ByteBuffer byteBuffer = ByteBuffer.allocate(size);
       bitmap.copyPixelsToBuffer(byteBuffer);
       return byteBuffer;
    }


    /** Temporary method created for showing results of inference in Log files
     * */
    protected void showResults(){
        //Adding results; this part has 10% thrershold
        ArrayList<String[]> results = new ArrayList<String[]>();
        for(int j = 0; j < labelList.size(); j++){
            //here might be an error comparing float with double?
            if(probArray[j] > 0.1){
                results.add(new String[]{labelList.get(j),String.valueOf(probArray[j])});
            }
        }
        Log.i("The results coming in hot", "-------RIGHT BELOW--------");
        for(int i = 0; i < 10; i++){
            Log.i(results.get(i)[0],results.get(i)[1]);
        }
    }

}
