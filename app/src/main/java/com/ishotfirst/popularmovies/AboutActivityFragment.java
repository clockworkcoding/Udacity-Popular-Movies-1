package com.ishotfirst.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutActivityFragment extends Fragment {

    public AboutActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ImageView tmdbImageView = (ImageView) rootView.findViewById(R.id.about_tmdb_imageview);
        Picasso.with(getContext()).load("https://assets.tmdb.org/images/logos/var_1_1_PoweredByTMDB_Blk_Logo_Antitled.png").into(tmdbImageView);


        return rootView;
    }
}
