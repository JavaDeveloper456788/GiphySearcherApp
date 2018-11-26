package com.giphy.android.ui;
import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
;
import android.widget.ImageView;

import com.giphy.android.data.DatabaseHelper;
import com.giphy.android.giphysearcher.R;
import java.util.ArrayList;


/**
 * View adapter for favourite images
 * The favourite images are saved in SQLite database as bitmaps and loaded into adapter using a list of images views
 *
 */
public class FavouriteImageAdapter extends RecyclerView.Adapter<FavouriteImageAdapter.MyFavouriteViewHolder> {
    private ArrayList<ImageView> images;
    private Context context;
    private DatabaseHelper databaseHelper;


    public FavouriteImageAdapter(ArrayList<ImageView> images) {
        this.images= images;

    }

    /**
     * Initialize the views. A view item includes an imageView
     *
     */

    @Override
    public FavouriteImageAdapter.MyFavouriteViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        if (context == null) context = parent.getContext();
        if (databaseHelper == null) databaseHelper = new DatabaseHelper(context);
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_image_view, parent, false);
        FavouriteImageAdapter.MyFavouriteViewHolder vh = new FavouriteImageAdapter.MyFavouriteViewHolder(imageView );
        return vh;

    }

    /**
     * Set the image view content of adapter view item
     */

    @Override
    public void onBindViewHolder(final FavouriteImageAdapter.MyFavouriteViewHolder holder, final int position) {;
        holder.imageView.setImageDrawable(images.get(position).getDrawable());

    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public static class MyFavouriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyFavouriteViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }

}
