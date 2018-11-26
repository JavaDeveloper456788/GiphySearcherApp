package com.giphy.android.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import com.giphy.android.data.DatabaseHelper;
import com.giphy.android.giphysearcher.R;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.regex.Pattern;
import static com.giphy.android.data.DatabaseHelper.DB_TABLE;
import static com.giphy.android.utils.Utils.getUniqueUrl;

/**
 *Main activity loading 2 fragments: Trending/Search Images Fragment and Favourite Images Fragment
 * Contains an Async task to execute API calls and return JSON data
 */
public class ImagesActivity extends FragmentActivity {
    private Context context;

    //List of trending image urls used by trending/search view adapter
    private ArrayList<String> imageAdapterUrls;

    //List of favourite imageviews used by favourite image view adapter
    private ArrayList<ImageView> favouriteImagesAdapterData;



    private MyPagerAdapter adapterViewPager;
    private DatabaseHelper databaseHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        imageAdapterUrls = new ArrayList<String>();
        favouriteImagesAdapterData = new ArrayList<ImageView>();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context);
        pager.setAdapter(adapterViewPager);
        databaseHelper = new DatabaseHelper(context);


    }


    /**
     * List of trending image urls used by trending/search adapter
     */
    public ArrayList<String> getImageAdapterUrls() {
        return imageAdapterUrls;
    }


    /**
     * Default API call to load trending images, without argument (Search keyword)
     */
    public void loadTrendingImages() {
        new LoadImages(this).execute();
    }


    /**
     * API call to load seacrh images, uses a search keyword as argument
     */
    public void searchImages(String searchKeyWord) {
        new LoadImages(this).execute(searchKeyWord);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;


        private ArrayList<Fragment> registeredFragments;
        private Context context;

        public MyPagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            this.context = context;
            registeredFragments = new ArrayList<Fragment>();
        }

        /**
         * Number of pages
         */
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
        /**
         * get Fragment
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SearchImagesFragment searchImagesFragment = new SearchImagesFragment();
                    registeredFragments.add(searchImagesFragment);
                    return searchImagesFragment;
                case 1:
                    FavouriteImagesFragment favouriteImagesFragment = new FavouriteImagesFragment();
                    registeredFragments.add(favouriteImagesFragment);
                    return favouriteImagesFragment;

                default:
                    return null;
            }
        }

        /**
         * Tab title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getResources().getString(R.string.search);
                case 1:
                    return context.getResources().getString(R.string.favourites);
                default:
                    return null;
            }
        }


        public ArrayList<Fragment> getRegisteredFragments() {
            return registeredFragments;
        }

    }

    /**
     * API calls to load trending images and search images
     * Async task called without arguments ( search keyword) will execute the API call to return trending images
     * Async task called with argument ( search keyword) will execute the API call to search images
     * The API call will return a list of images URLS, which will be passed to view adapter
     */
    class LoadImages extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        public LoadImages(ImagesActivity activity) {

            dialog = new ProgressDialog(activity);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.loading_images));
            dialog.show();
        }

        /**
         * getting All images from HTTP request
         */
        protected String doInBackground(String... args) {
            boolean trendingRequest = false;
            String searchKeyWord = null;
            // if no search keyword argument, then load trending images
            if (args.length == 0) trendingRequest = true;
            else
                searchKeyWord = args[0];
            // if no search key word, then load trending images
            if ((searchKeyWord == null) || (searchKeyWord.isEmpty())) {
                trendingRequest = true;
            }
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https");
            //split url into base url and paths
            String[] paths = getString(R.string.api_base_url).split(Pattern.quote("/"));
            builder.authority(paths[0]);
            for (int i = 1, l = paths.length; i < l; i++)
                builder.appendPath(paths[i]);
            //url for trending images API call
            if (trendingRequest)
                builder.appendPath(getString(R.string.api_trending));
            //url for search API call
            else {
                builder.appendPath(getString(R.string.api_search));
                builder.appendQueryParameter("q", searchKeyWord);

            }
            builder.appendQueryParameter("api_key", getString(R.string.api_key));
            builder.build();

            //HTTP call
            HttpGet httpGet = new HttpGet(builder.toString());
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();
                //if successful get JSON data
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(data);
                    JSONArray images = json.getJSONArray("data");
                    if (imageAdapterUrls!=null) imageAdapterUrls.clear();
                    for (int i = 0; i < images.length(); i++) {
                        JSONObject image = images.getJSONObject(i);
                        String url = image.getJSONObject("images").getJSONObject("original").getString("url");
                        //add URL to te list used by view adapter
                        imageAdapterUrls.add(url);

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
           ((SearchImagesFragment) adapterViewPager.getRegisteredFragments().get(0)).notifyDataChanged();
        }

    }

    /**
     * Load favourite Images from SQLite database and add them to the view adapter as ImageViews
     *
     */
    public ArrayList<ImageView> loadFavouriteImages() {
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();


            String selectQuery = "SELECT  * FROM " + DB_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String url =cursor.getString(0);
                    byte[] imageBytes = cursor.getBlob(1);
                    ImageView imageView = new ImageView(context);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                    imageView.setTag(R.id.url_tag,url);
                    favouriteImagesAdapterData.add(imageView);
                } while (cursor.moveToNext()); //move to next row in the query result

            }
            cursor.close();
            database.close();


        } catch (SQLiteException e) {
            e.printStackTrace();

        }
        return favouriteImagesAdapterData;

    }

    /**
     * Add Favourite Image to view adapter
     *
     */
    public void addFavouriteImage(String url, ImageView imageView) {
        imageView.setTag(R.id.url_tag,getUniqueUrl(url));
        favouriteImagesAdapterData.add(imageView);
        ((FavouriteImagesFragment) adapterViewPager.getRegisteredFragments().get(1)).notifyDataChanged();

    }
    /**
     * Delete Favourite Image from view adapter
     * Loop through Favourite Image adapter to find the corresponding image tag to URL

     */
    public void deleteFavouriteImage(String url) {
        Iterator<ImageView> iterator = favouriteImagesAdapterData.iterator();
        while (iterator.hasNext()) {
            ImageView image =iterator.next();
            if ((image.getTag(R.id.url_tag)).equals(url)) {
                favouriteImagesAdapterData.remove(image);
                break;
            }
        }
            ((FavouriteImagesFragment) adapterViewPager.getRegisteredFragments().get(1)).notifyDataChanged();
        }

    }



