package com.giphy.android.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.giphy.android.giphysearcher.R;

/**
 * Fragment for Trending and search images
 * Includes an adapter to display a grid of images and a search menu ( edit text and search button)
 */

public class SearchImagesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImagesActivity mImagesActivity;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);

    }

    /**
     * Initialize the list of views and the adapter.
     * calls the method to load trending images when fragment starts
     *
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImagesActivity = (ImagesActivity) getActivity();
        mImagesActivity.loadTrendingImages();

        mRecyclerView = mImagesActivity.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ImageAdapter(mImagesActivity.getImageAdapterUrls());
        mRecyclerView.setAdapter(mAdapter);
        //Search edit text
        final EditText mSearchEditText = mImagesActivity.findViewById(R.id.search_edit_text);

        //Search button
        Button mSearchButton = mImagesActivity.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        mImagesActivity.searchImages(mSearchEditText.getText().toString());
                    }
                });
        //Progress Dialog
        if (dialog == null) {
            dialog = new ProgressDialog(mImagesActivity);
            dialog.setMessage(getString(R.string.loading_images));
        }

        if (!dialog.isShowing()) dialog.show();
        //When Recycler view is done loading images, dismiss progress dialog
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if ((dialog.isShowing()))
                    dialog.dismiss();
            }
        });
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

}

