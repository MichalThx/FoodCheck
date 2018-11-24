package com.opusdev.foodcheck;

import java.util.ArrayList;

/**
 * Class combines creates a list view from an array list of items
 * Created by Michal on 14.11.2018.
 */
//TODO: not used class

public class CardViewAdapter {

    ArrayList<String> label;
    ArrayList<String> img;
    ArrayList<String> ingredientsTotal;

    public CardViewAdapter(ArrayList<String> label, ArrayList<String> img, ArrayList<String> ingredientsTotal) {
            this.label = label;
            this.img = img;
            this.ingredientsTotal = ingredientsTotal;
    }

}
