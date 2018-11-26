package com.giphy.android.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giphy.android.giphysearcher.R;

/**
 * .Fragment to display Favourite images stored on device
 * Includes an adapter to display a grid of images
 */

public class FavouriteImagesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImagesActivity mImagesActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourite_fragment, container, false);

    }
    /**
     * Initialize the views and adapter
     * calls the method to load favourite images when fragment starts
     *
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImagesActivity = (ImagesActivity) getActivity();


        mRecyclerView = mImagesActivity.findViewById(R.id.favourite_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FavouriteImageAdapter(mImagesActivity.loadFavouriteImages());
        mRecyclerView.setAdapter(mAdapter);
        notifyDataChanged();


    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }
}
