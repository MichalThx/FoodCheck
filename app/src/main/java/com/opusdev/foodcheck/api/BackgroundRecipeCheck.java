package com.opusdev.foodcheck.api;

import android.os.AsyncTask;
import com.opusdev.foodcheck.recipe.RecipeFancier;
import com.opusdev.foodcheck.recipe.RecipesRecyclerAdapter;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Michal on 28.10.2018.
 * This class handles searching for a recipe in an API.
 * It extends Android's AsyncTask to do it in the background.
 */

public class BackgroundRecipeCheck extends AsyncTask<URL, Integer, Boolean> {

	RecipesRecyclerAdapter recipesRecyclerAdapter;
	String result;

	public BackgroundRecipeCheck(RecipesRecyclerAdapter recipesRecyclerAdapter) {
		this.recipesRecyclerAdapter = recipesRecyclerAdapter;
	}

	/**
	 * First part of the AsyncTask, searches in the background for a recipe, utilizes vargas arguments.
	 * It calls method Recipe with an Url
	 * Url...  indicates the method can take few urls as an array
	 * Example:
	 * name(url1); name(url1,url2,url3);
	 */
	@Override
	protected Boolean doInBackground(URL... urls) {
		URL url = urls[0];
		result = getRecipe(url);
		return false;
	}

	/**
	 * This method does all the things after the recipe has been found.
	 * It catches if the JSON file is corrupted or anything similar.
	 * It creates Recipe Fancier object with just received file.
	 */
	@Override
	protected void onPostExecute(Boolean bool) {
		try {
			RecipeFancier rp = new RecipeFancier(result);
			recipesRecyclerAdapter.update(rp.getRecipe());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method checks if there is a connection to the server
	 *
	 * @param url = url of a website with modified query
	 * @return it returns String taken from website in JSON format
	 */
	public String getRecipe(URL url) {
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
