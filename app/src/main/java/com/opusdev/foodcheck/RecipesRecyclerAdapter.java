package com.opusdev.foodcheck;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.opusdev.foodcheck.SecondFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * RecipesRecyclerAdapter is the class responsible for creating a way to show the recipe data in a lsit.
 * It follows the tutorial from here: https://github.com/java-lang-programming/Android-Material-Design-Demo/blob/master/app/src/main/java/com/java_lang_programming/android_material_design_demo/ui/MyItemRecyclerViewAdapter.java
 *
 */
public class RecipesRecyclerAdapter extends RecyclerView.Adapter<RecipesRecyclerAdapter.ViewHolder> {

    private final List<String> labels;
    private final List<String> images;
    private final List<String> desc;
    private final List<String> addresses;
    private Context context;
    private final OnListFragmentInteractionListener mListener;


    public RecipesRecyclerAdapter(List<String> labels,List<String> images, List<String> desc, List<String> addresses,Context context, OnListFragmentInteractionListener listener) {
        this.labels = labels;
        this.images = images;
        this.desc = desc;
        this.addresses = addresses;
        this.context = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recipes_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mIdView.setText(labels.get(position));
        Picasso.get().load(images.get(position))
                .resize(400, 400)
                .centerCrop().into(holder.mImageView);
        holder.mContentView.setText(desc.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addresses.get(position)));
                    context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public String label;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.itemLabel);
            mImageView = (ImageView) view.findViewById(R.id.itemImage);
            mContentView = (TextView) view.findViewById(R.id.itemDesc);
        }
    }

    /**The method updates data.
    * It upates the values in the recycler viewer
    *
    * */
    public void update(List<Recipe> data){

        labels.clear();
        images.clear();
        desc.clear();
        addresses.clear();
        for(Recipe recipe: data){
            labels.add(recipe.getName());
            images.add(recipe.getImage());
            desc.add(descWrapper(recipe.getHealth()));
            addresses.add(recipe.getAddress());
        }
        this.notifyDataSetChanged();
    }

    /** This method cleans the output of JSON
    *  Changes ["asd","asdd"] into asd, asdd
    *  TODO: Make an efficient version of this method.
    * */
    public String descWrapper(String s){
            s = s.replace("[","");
            s = s.replace("\"","");
            s = s.replace("]","");
        return s;
    }

}
