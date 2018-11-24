package com.opusdev.foodcheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This Class is responsible for creating a recipe box from JSON file
 * Created by Michal on 13.11.2018.
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
        for(int i = 0; i < json.length(); i++){
            String name = json.getJSONObject(i).getJSONObject("recipe").getString("label");
            String image = json.getJSONObject(i).getJSONObject("recipe").getString("image");
            String health = json.getJSONObject(i).getJSONObject("recipe").getString("healthLabels");
            String address = json.getJSONObject(i).getJSONObject("recipe").getString("url");
            recipes.add(new Recipe(name, image, health, address));
        }
        return recipes;
    }
//    public ArrayList<String> getLabeles() throws JSONException {
//        ArrayList<String> labels = new ArrayList<>();
//        JSONArray json = (JSONArray) Json.get("hits");
//        for(int i = 0; i < json.length(); i++){
//            labels.add(json.getJSONObject(i).getJSONObject("recipe").getString("label"));
//        }
//        return labels;
//    }
//    public ArrayList<String> getImages() throws JSONException {
//        ArrayList<String> images = new ArrayList<>();
//        JSONArray json = (JSONArray) Json.get("hits");
//        for(int i = 0; i < json.length(); i++){
//            images.add(json.getJSONObject(i).getJSONObject("recipe").getString("image"));
//        }
//        return images;
//    }

}
