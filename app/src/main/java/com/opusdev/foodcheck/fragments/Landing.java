package com.opusdev.foodcheck.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.opusdev.foodcheck.MainActivity;
import com.opusdev.foodcheck.R;


public class Landing extends Fragment {

	public static Landing newInstance() {
		return new Landing();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_landing, container, false);
		RecyclerView recyclerView = view.findViewById(R.id.recentSearches);
		Button settingsButton = view.findViewById(R.id.settingsBtn);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(((MainActivity) getActivity()).getRecipesRecyclerAdapter());

		settingsButton.setOnClickListener(v -> {
			((MainActivity) getActivity()).getVpPager().setCurrentItem(0, true);
		});

		return view;
	}
}