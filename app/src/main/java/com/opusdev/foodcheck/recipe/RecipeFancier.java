package com.opusdev.foodcheck.recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * RecipeFancier - creates a Recipe object from JSON file
 *
 * @String JSON - JSON file is used on creating an object of the RecipeFancier
 * <p>
 * getRecipe() - returns an ArrayList of all the recipe objects in the provided JSON file.
 */
public class RecipeFancier {
	JSONObject Json;
	String JsonString;
	ArrayList<Recipe> recipes;

	public RecipeFancier(String Json) throws JSONException {
		this.Json = new JSONObject((Json.trim()));
		JsonString = Json;
	}

	public ArrayList<Recipe> getRecipe() throws JSONException {
		recipes = new ArrayList<>();
		JSONArray json = (JSONArray) Json.get("hits");
		for (int i = 0; i < json.length(); i++) {
			String name = json.getJSONObject(i).getJSONObject("recipe").getString("label");
			String image = json.getJSONObject(i).getJSONObject("recipe").getString("image");
			String health = json.getJSONObject(i).getJSONObject("recipe").getString("healthLabels");
			String address = json.getJSONObject(i).getJSONObject("recipe").getString("url");
			recipes.add(new Recipe(name, image, health, address));
		}
		return recipes;
	}


}
