package com.opusdev.foodcheck.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.opusdev.foodcheck.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { BlankFragment.OnFragmentInteractionListener } interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment{

    public static SettingsFragment newInstance() {
        SettingsFragment fragmentSettingsPage = new SettingsFragment();
        return fragmentSettingsPage;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }
}


