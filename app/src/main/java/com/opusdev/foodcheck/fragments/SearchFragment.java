package com.opusdev.foodcheck.fragments;

import android.app.SearchManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.opusdev.foodcheck.*;
import com.opusdev.foodcheck.api.BackgroundRecipeCheck;
import com.opusdev.foodcheck.recipe.RecipesRecyclerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Second Fragment
 * This screen has:
 * - search bar
 * -ingredients list
 *
 *
 */
public class SearchFragment extends Fragment {
    //private static String searchValue;
    // Store instance variables
    private OnListFragmentInteractionListener mListener;
    //TODO: is this one needed if the app uses it only once?
    private RecyclerView recyclerView;
    private RecipesRecyclerAdapter recipesRecyclerAdapter;
    private ArrayList values;



    // newInstance constructor for creating fragment with arguments
    public static SearchFragment newInstance() {
        SearchFragment fragmentSecond = new SearchFragment();
        //searchValue = SearchValue;
        return fragmentSecond;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    // Inflate the view for the fragment based on the XML layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        values = ((MainActivity) getActivity()).getRecentSearches();
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        recyclerView = view.findViewById(R.id.rycycleBox);
//        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            // recentSearches = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recipesRecyclerAdapter = new RecipesRecyclerAdapter(
                    new ArrayList<String>(Arrays.asList( "Search for an item")),
                    new ArrayList<String>(Arrays.asList("https://upload.wikimedia.org/wikipedia/commons/6/6d/Good_Food_Display_-_NCI_Visuals_Online.jpg")),
                    new ArrayList<String>(Arrays.asList( "Use the box at the top")),
                    new ArrayList<String>(Arrays.asList("https://en.wikipedia.org/wiki/Food")),
                    this.getContext(),
                    mListener);
            //TODO: first its empty/ it should show past results, atm it uses SEARCH FOR ITEMS string
            recyclerView.setAdapter(recipesRecyclerAdapter);
        final ImageButton searchButton = view.findViewById(R.id.searchBoxButton);
        final RecentAutoCompleteText textView = view.findViewById(R.id.autoCompleteTextView);

        final ArrayAdapter<String> test = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, values);
        //textView.setThreshold(0);
        textView.setAdapter(test);
        //final EditText searchBoxText = view.findViewById(R.id.);
        String searchValue = ((MainActivity)getActivity()).getSearchValue();
        SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        //final RecentViewModel history = ViewModelProviders.of(getActivity()).get(RecentViewModel.class);
        model.getSelected().observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String item) {
                        textView.setText(item);
                    }
                }
        );

        try{
            FileInputStream fis = new FileInputStream(new File(getContext().getCacheDir(), "recentSearch"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            values = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            Log.i("READING", String.valueOf(values.size()));
        }catch(Exception e) {

        }
        Log.i("READING", "onCreateView: "+values.size());
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                textView.showDropDown();
//            }
//        });
        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) textView.showDropDown();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = textView.getText().toString();
                //if(query.)
                textView.setText("");
                URL url = null;
                try {
                    /* TODO: Move the key to separate folder */
                    url = new URL("https://api.edamam.com/search?q="+query+"&app_id=1242155c&app_key=223b7a8dcccd7c01adc0e89f0e13ad7a");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //TODO: Move set text to different thread so it is seamless
                //if(!values.equals(""))values.add(query);


                new BackgroundRecipeCheck(recipesRecyclerAdapter).execute(url);
                textView.setText("");

                if(!values.contains(query) && !query.equals(" "))values.add(query);
                ((MainActivity)getActivity()).setSearchValue("");
                if(values.size()>10){
                    values.remove(0);
                   // test.remove((String) values.get(0));

                }
                test.clear();
                for(int i = 0; i < values.size(); i++){
                    test.insert((String) values.get(i),i);
                }
                ((MainActivity) getActivity()).setRecentSearches(values);
                test.notifyDataSetChanged();
                textView.clearFocus();
                searchButton.requestFocus();

                        try{
                            //File file =
//                            FileOutputStream fos = new FileOutputStream(new File("recentSearch", getContext().getCacheDir()));
                            FileOutputStream fos = new FileOutputStream("recentSearch");
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(values);
                            oos.close();
                            fos.close();
                            Log.i("WRITING", String.valueOf(values.size()));
                        }catch (Exception e){

                        }


            }
        });

//        }
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
                new BackgroundRecipeCheck(recipesRecyclerAdapter).execute(url);
                return false;
            }

            /*TODO: Creating visual cue of progress being made*/
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String label);
    }
}