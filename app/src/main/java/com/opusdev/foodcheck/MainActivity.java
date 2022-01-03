package com.opusdev.foodcheck;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.opusdev.foodcheck.fragments.CameraFragment;
import com.opusdev.foodcheck.fragments.FragmentLanding;
import com.opusdev.foodcheck.fragments.SearchFragment;
import com.opusdev.foodcheck.fragments.SettingsFragment;

import java.util.ArrayList;

/**
 * This is the main class of the application.
 * It handles the fragment switching.
 * This piece of code is inspired by https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
 * Author: Roger Hu; Accessed: 10 December 2018
 */
public class MainActivity extends AppCompatActivity {

	private static final int NUM_ITEMS = 4;

	ViewPager vpPager;
	String searchValue;
	ArrayList recentSearches;
	FragmentPagerAdapter adapterViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		vpPager = findViewById(R.id.vpPager);
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

	public void setRecentSearches(ArrayList recentSearches) {
		this.recentSearches = recentSearches;
	}

	public ArrayList getRecentSearches() {
		return recentSearches;
	}


	public static class MyPagerAdapter extends FragmentPagerAdapter implements OnListFragmentInteractionListener {

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return SettingsFragment.newInstance();
				case 1:
					return FragmentLanding.newInstance();
				case 2:
					return CameraFragment.newInstance();
				case 3:
					return SearchFragment.newInstance();
				default:
					return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Page number " + position;
		}

	}

//    public void checkStoredData(){
//
//    }
}