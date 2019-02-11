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
 * Use the {@link LandingPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingPageFragment extends Fragment{

    public static LandingPageFragment newInstance() {
        LandingPageFragment fragmentLandingPage = new LandingPageFragment();
        return fragmentLandingPage;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        return view;
    }
}


