package com.opusdev.foodcheck;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.opusdev.foodcheck.fragments.Camera;
import com.opusdev.foodcheck.fragments.Landing;
import com.opusdev.foodcheck.fragments.Search;
import com.opusdev.foodcheck.fragments.Settings;
import com.opusdev.foodcheck.recipe.Recipe;
import com.opusdev.foodcheck.recipe.RecipesRecyclerAdapter;

import java.util.ArrayList;

/**
 * This is the main class of the application.
 * It handles the fragment switching.
 * This piece of code is inspired by https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
 * Author: Roger Hu; Accessed: 10 December 2018
 */

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

	public static final int MainActivity_CODE = 0;
	private static final int NUM_ITEMS = 4;
	private static final String TAG = "MainActivity";
	ArrayList<String> recentSearches;
	FragmentStateAdapter adapterViewPager;
	Context context;
	private ViewPager2 vpPager;
	private RecipesRecyclerAdapter recipesRecyclerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getBaseContext();
		permissionHandler(context, this);
		setContentView(R.layout.activity_main);
		vpPager = findViewById(R.id.vpPager);
		adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle(), context);
		vpPager.setAdapter(adapterViewPager);
		vpPager.setCurrentItem(1);
		ArrayList<Recipe> recipes = new ArrayList<>();
		recipes.add(new Recipe("Search for an item", "https://upload.wikimedia" + ".org/wikipedia/commons/6/6d/Good_Food_Display_-_NCI_Visuals_Online.jpg", "Use the box at the top", "https://en.wikipedia.org/wiki/Food"));
		recipesRecyclerAdapter = new RecipesRecyclerAdapter(recipes, this);

		recentSearches = new ArrayList<>();

	}

	private void permissionHandler(Context context, Activity activity) {
		if (PackageManager.PERMISSION_GRANTED != context.checkSelfPermission(Manifest.permission.CAMERA)) {
			activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity_CODE);
		} else {
			// No need to check, already granted
			Log.i(TAG, "Camera permissions already granted");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == MainActivity_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.i(TAG, "Camera permissions granted");
			}
		}
	}

	public ViewPager2 getVpPager() {
		return vpPager;
	}

	public RecipesRecyclerAdapter getRecipesRecyclerAdapter() {
		return recipesRecyclerAdapter;
	}

	public ArrayList<String> getRecentSearches() {
		return recentSearches;
	}

	public void setRecentSearches(ArrayList<String> recentSearches) {
		this.recentSearches = recentSearches;
	}

	public static class MyPagerAdapter extends FragmentStateAdapter{
		Context context;

		public MyPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context) {
			super(fragmentManager, lifecycle);
			this.context = context;
		}

		@NonNull
		@Override
		public Fragment createFragment(int position) {
			switch (position) {
				case 0:
					return Settings.newInstance();
				case 2:
					return Camera.newInstance();
				case 3:
					return Search.newInstance();
				case 1:
				default:
					return Landing.newInstance();
			}
		}

		@Override
		public int getItemCount() {
			return NUM_ITEMS;
		}
	}
}