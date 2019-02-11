package com.opusdev.foodcheck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsPageFragment extends Fragment{

    public static SettingsPageFragment newInstance() {
        SettingsPageFragment fragmentSettingsPage = new SettingsPageFragment();
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


