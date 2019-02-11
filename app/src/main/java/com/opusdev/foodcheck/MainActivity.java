package com.opusdev.foodcheck;

import android.content.ClipData;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * This is main class of the application.
 * The handling of different fragents happens here.
 * This piece of code is inspired by https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
 * Author: Roger Hu; Accessed: 10 Decembert, 2018
* */
public class MainActivity extends AppCompatActivity  {
    ViewPager vpPager;
    String searchValue;
    ArrayList recentSearches;



    public static class MyPagerAdapter extends FragmentPagerAdapter implements OnListFragmentInteractionListener  {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SettingsPageFragment.newInstance();
                case 1:
                    return LandingPageFragment.newInstance();
                case 2:
                    return Camera2Fragment.newInstance();
                case 3:
                    return SecondFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(1);
        searchValue = "";

            recentSearches = new ArrayList<String>();


    }

    public ViewPager getVpPager() {
        return vpPager;
    }
    public String getSearchValue() {
        return searchValue;
    }
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
//    public void checkStoredData(){
//
//    }
    public ArrayList getRecentSearches() {
        return recentSearches;
    }

    public void setRecentSearches(ArrayList recentSearches) {
        this.recentSearches = recentSearches;
    }
}