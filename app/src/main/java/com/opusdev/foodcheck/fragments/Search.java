package com.opusdev.foodcheck.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.opusdev.foodcheck.MainActivity;
import com.opusdev.foodcheck.R;
import com.opusdev.foodcheck.RecentAutoCompleteText;
import com.opusdev.foodcheck.api.BackgroundRecipeCheck;
import com.opusdev.foodcheck.recipe.RecipesRecyclerAdapter;
import com.opusdev.foodcheck.viewmodels.SharedViewModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Search extends Fragment {
	private RecyclerView recyclerView;
	private RecipesRecyclerAdapter recipesRecyclerAdapter;
	private ArrayList values;

	public static Search newInstance() {
		Search fragmentSecond = new Search();
		return fragmentSecond;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		values = ((MainActivity) getActivity()).getRecentSearches();
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		recyclerView = view.findViewById(R.id.recycleBox);
		Context context = view.getContext();
		recipesRecyclerAdapter = ((MainActivity) getActivity()).getRecipesRecyclerAdapter();
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setAdapter(recipesRecyclerAdapter);

		final ImageButton searchButton = view.findViewById(R.id.searchBoxButton);
		final RecentAutoCompleteText textView = view.findViewById(R.id.autoCompleteTextView);

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, values);
		textView.setAdapter(arrayAdapter);
		SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
		model.getSelected().observe(this, textView::setText);

		textView.setOnClickListener(view13 -> textView.showDropDown());
		textView.setOnFocusChangeListener((view12, b) -> {
			if (b) textView.showDropDown();
		});

		searchButton.setOnClickListener(view1 -> {
			String query = textView.getText().toString();
			textView.setText("");
			URL url = null;
			try {
				url = new URL("https://api.edamam.com/search?q=" + query + "&app_id=1242155c&app_key=223b7a8dcccd7c01adc0e89f0e13ad7a");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			new BackgroundRecipeCheck(recipesRecyclerAdapter).execute(url);
			textView.setText("");

			if (!values.contains(query) && !query.equals(" ")) values.add(query);
			if (values.size() > 10) values.remove(0);
			arrayAdapter.clear();
			for (int i = 0; i < values.size(); i++) {
				arrayAdapter.insert((String) values.get(i), i);
			}
			((MainActivity) getActivity()).setRecentSearches(values);
			arrayAdapter.notifyDataSetChanged();
			textView.clearFocus();
			searchButton.requestFocus();

		});
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setQueryHint("Search");
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				URL url = null;
				try {
					url = new URL("https://api.edamam.com/search?q=" + query + "&app_id=1242155c&app_key=223b7a8dcccd7c01adc0e89f0e13ad7a");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				new BackgroundRecipeCheck(recipesRecyclerAdapter).execute(url);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	public void onDetach() {
		super.onDetach();
	}
}