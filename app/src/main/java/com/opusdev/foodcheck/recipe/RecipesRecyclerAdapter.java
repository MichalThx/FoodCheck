package com.opusdev.foodcheck.recipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.opusdev.foodcheck.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * RecipesRecyclerAdapter is the class responsible for creating a way to show the recipe data in a list.
 * It follows the tutorial from here: https://github.com/java-lang-programming/Android-Material-Design-Demo/blob/master/app/src/main/java/com/java_lang_programming/android_material_design_demo/ui/MyItemRecyclerViewAdapter.java
 */
public class RecipesRecyclerAdapter extends RecyclerView.Adapter<RecipesRecyclerAdapter.ViewHolder> {
	private final Context context;
	List<Recipe> recipes;


	public RecipesRecyclerAdapter(List<Recipe> recipes, Context context) {
		this.recipes = recipes;
		this.context = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item_recycler, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
		holder.mIdView.setText(recipes.get(position).getName());
		Picasso.get().load(recipes.get(position).getImage()).resize(400, 400).centerCrop().into(holder.mImageView);
		String description = descWrapper(recipes.get(position).getHealth());
		if (description.length() > 100) {
			description = description.substring(0, 100) + "...";
		}
		holder.mContentView.setText(description);
		holder.mView.setOnClickListener(v -> {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(recipes.get(position).getAddress()));
			context.startActivity(i);
		});
	}

	@Override
	public int getItemCount() {
		return this.recipes.size();
	}

	/**
	 * The method updates data.
	 * It updates the values in the recycler viewer
	 */
	@SuppressLint("NotifyDataSetChanged")
	public void update(List<Recipe> data) {
		this.recipes.clear();
		this.recipes.addAll(data);
		this.notifyDataSetChanged();
	}

	/**
	 * This method cleans the output of JSON
	 * Changes ["abc","def"] into "abc, def"
	 */
	public String descWrapper(String s) {
		s = s.replace("[", "");
		s = s.replace("\"", "");
		s = s.replace("]", "");
		return s;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final ImageView mImageView;
		public final TextView mContentView;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = view.findViewById(R.id.itemLabel);
			mImageView = view.findViewById(R.id.itemImage);
			mContentView = view.findViewById(R.id.itemDesc);
		}
	}

}
