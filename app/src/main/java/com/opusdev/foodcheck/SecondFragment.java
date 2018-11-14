package com.opusdev.foodcheck;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Second Fragment
 * This screen has:
 * - search bar
 * -ingredients list
 *
 *
 */
public class SecondFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static SecondFragment newInstance(int page, String title) {
        SecondFragment fragmentSecond = new SecondFragment();


        return fragmentSecond;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);
        return view;
    }


    //Creates menu bar: search bar. Provides search query methods.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the options menu from XML
         inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                URL url = null;
                try {
                    /* TODO: Move the key to separate folder */
                    url = new URL("https://api.edamam.com/search?q="+query+"&app_id=1242155c&app_key=223b7a8dcccd7c01adc0e89f0e13ad7a");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //new BackgroundRecipeCheck((TextView) getActivity().findViewById(R.id.SndFraTV)).execute(url);

                return false;
            }

            /*Creating visual cue of progress being made*/
            @Override
            public boolean onQueryTextChange(String newText) {
                //TextView tv = (TextView) getActivity().findViewById(R.id.SndFraTV);
                //tv.setText("Looking for "+ newText);
                return false;
            }
        });
    }
}