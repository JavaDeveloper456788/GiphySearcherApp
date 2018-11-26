package com.giphy.android.ui;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.giphy.android.data.DatabaseHelper;
import com.giphy.android.giphysearcher.R;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.giphy.android.data.DatabaseHelper.DB_TABLE;
import static com.giphy.android.data.DatabaseHelper.KEY_IMAGE;
import static com.giphy.android.data.DatabaseHelper.KEY_URL;
import static com.giphy.android.utils.Utils.getUniqueUrl;


/**
 * View adapter for trending and search images using a list of images URLS to load the images
 *
 */


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    // list of images URLS
    private ArrayList<String> urls;
    private Context context;
    private DatabaseHelper databaseHelper;


    public ImageAdapter(ArrayList<String> urls) {
        this.urls = urls;

    }
    /**
     * Initialize the views. A view item includes an imageView and  button to add/delete favourite
     *
     */

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (context == null) context = parent.getContext();
        if (databaseHelper == null) databaseHelper = new DatabaseHelper(context);

        LinearLayout imageContainer = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_view_container, parent, false);
        MyViewHolder vh = new MyViewHolder(imageContainer);

        vh.setImageView((ImageView) imageContainer.findViewById(R.id.image_view));
        vh.setButton((Button) imageContainer.findViewById(R.id.button_add_delete_from_favourites));


        return vh;

    }




    /**
     * Load/build an imageView from URL
     * Add a click listener to add/delete favourite image button
     *
     */

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {



        RequestOptions options = new RequestOptions().
                skipMemoryCache(true).
                disallowHardwareConfig();
        //Load/build an imageView from URL
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(urls.get(position))
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            holder.getButton().setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.getImageView());
            final String url = getImageUrl(position);
            Button favouriteButton = holder.getButton();

            if (!isFavourite(url))
                favouriteButton.setText(context.getResources().getString(R.string.add_to_favourites));
            else
                favouriteButton.setText(context.getResources().getString(R.string.delete_from_favourites));

           // Add a click listener to add/delete favourite image button
            favouriteButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            if (!isFavourite(url)) {
                                ((Button) v).setText(context.getResources().getString(R.string.delete_from_favourites));
                                //Save Favourite image to SQLite database
                                saveImage(url, holder.getImageView());
                                //Add Favourite Image to view adapter
                                ((ImagesActivity) context).addFavouriteImage(url, holder.getImageView());
                            } else {
                                ((Button) v).setText(context.getResources().getString(R.string.add_to_favourites));
                                deleteImageFromFavourite(url);
                                ((ImagesActivity) context).deleteFavouriteImage(getUniqueUrl(url));
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return urls.size();
    }

    private String getImageUrl(int position) {
        return urls.get(position);

    }

    /**
     * Save Favourite image to SQLite database as a Bitmap and URL
     */
    public void saveImage(String url, ImageView imageView) {
        try {
            BitmapDrawable bitmapDrawable= (BitmapDrawable) imageView.getDrawable();
            if (bitmapDrawable!=null) {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(KEY_URL, getUniqueUrl(url));
                cv.put(KEY_IMAGE, imageBytes);
                database.insert(DB_TABLE, null, cv);
                database.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();

        }

    }
    /**
     * Database Query to check if the image is stored in database as favourite
     */
    private boolean isFavourite(String url) {
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            String selectQuery = "SELECT  * FROM " + DB_TABLE + " WHERE " + KEY_URL + " = " + "\"" + getUniqueUrl(url) + "\"";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                return true;

            }
            cursor.close();
            database.close();
        } catch (
                SQLiteException e)

        {
            e.printStackTrace();

        }
        return false;
    }
    /**
     * Database Query to delete favourite image from SQLite database
     */
    private void deleteImageFromFavourite(String url) {
        try {
            SQLiteDatabase database = databaseHelper.getWritableDatabase();
            database.delete(DB_TABLE, KEY_URL + " = " + "\"" + getUniqueUrl(url) + "\"", null);
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();

        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView imageView;
        private Button button;

        public MyViewHolder(LinearLayout v) {
            super(v);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }
    }




}