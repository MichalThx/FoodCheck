package com.opusdev.foodcheck.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.opusdev.foodcheck.R;


public class FragmentLanding extends Fragment {

	public static FragmentLanding newInstance() {
		return new FragmentLanding();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_landing, container, false);
		return view;
	}
}