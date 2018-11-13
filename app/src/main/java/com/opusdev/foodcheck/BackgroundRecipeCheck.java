package com.opusdev.foodcheck;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Michal on 28.10.2018.
 * Method finds recipes online based on the query
 *
 */

public class BackgroundRecipeCheck extends AsyncTask<URL, Integer, Boolean> {
    TextView tv;
    String result;
    public BackgroundRecipeCheck(TextView tv){
        this.tv = tv;
    }
    @Override
    protected Boolean doInBackground(URL... urls) {
        URL url = urls[0];
        result = getRecipe(url);

        return false;
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        //sets the text to result taken from website(JSON)
        tv.setText(result);
    }

    /**
     * Method checks if there is a connection to the server
     * @param url = url of a website with modified query
     * @return it returns String taken from website
     */
    public String getRecipe(URL url){
        String result = "";
        HttpURLConnection urlConnection = null;


        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }
}