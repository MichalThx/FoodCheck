package com.opusdev.foodcheck;

import android.os.AsyncTask;
import org.json.JSONException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Michal on 28.10.2018.
 * This class handels searching for a recipe in an API.
 * It extends Android's AsynTask to do it in the background.
 */

public class BackgroundRecipeCheck extends AsyncTask<URL, Integer, Boolean> {

    RecipesRecyclerAdapter recipesRecyclerAdapter;

    private SecondFragment.OnListFragmentInteractionListener mListener;
    String result;

    /**
     * Constructor of this class
     */
    public BackgroundRecipeCheck(RecipesRecyclerAdapter recipesRecyclerAdapter){
        this.recipesRecyclerAdapter = recipesRecyclerAdapter;
    }

    /** First part of the AsynTask, searches in the background for a recipe, utilizes vargas arguments.
    * It calls method Recipe with an Url
    * Url... == it means, this method can take few urls
    * Example:
    * name(url1); name(url1,url2,url3);
    *
    */
    @Override
    protected Boolean doInBackground(URL... urls) {
        URL url = urls[0];
        result = getRecipe(url);
        return false;
    }
    /**
     *  This method does all the things after the recipe has been found.
     *  It catches if the JSON file is corrupted or anything similiar.
     *  It creates Recipe Fancier object with just received file.
     */
    @Override
    protected void onPostExecute(Boolean bool) {
        try {
            RecipeFancier rp = new RecipeFancier(result);
            //TODO: Here we should inflate the card view with results
            recipesRecyclerAdapter.update(rp.getRecipe());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if there is a connection to the server
     * @param url = url of a website with modified query
     * @return it returns String taken from website in JSON format
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
