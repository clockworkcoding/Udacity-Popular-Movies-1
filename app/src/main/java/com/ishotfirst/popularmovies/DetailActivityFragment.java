package com.ishotfirst.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent!=null &&intent.hasExtra("Movie")){
            Movie movie = (Movie)intent.getParcelableExtra("Movie");
            TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title_textview);
            TextView ratingTextView = (TextView) rootView.findViewById(R.id.detail_rating_textview);
            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.detail_synopsis_textview);
            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.detail_release_date_textview);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.detail_poster_image_view);

            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(movie.getRating());
            synopsisTextView.setText(movie.getOverview());
            releaseDateTextView.setText(movie.getReleaseDate());

            Picasso.with(getContext()).load(movie.getImageUri("w500")).into(posterImageView);
        }


        return rootView;
    }
}
