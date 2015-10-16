package com.ishotfirst.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Max on 10/16/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private static final String TMDB_IMAGE_NORMAL = "w185";
    private static final String TMDB_IMAGE_SMALL = "w92";
    private static final String TMDB_IMAGE_LARGE = "w342";
    private static final String TMDB_IMAGE_XLARGE = "w500";
    private final Context context;
    private final ArrayList<Movie> data;
    private final int layoutResourceId;
    private final String imageSize;

    public MovieAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.layoutResourceId = resource;

        switch (((Activity)context).getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            default:
                Log.d(LOG_TAG, "Unexpected screen size");
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                imageSize = TMDB_IMAGE_NORMAL;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                imageSize = TMDB_IMAGE_SMALL;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                imageSize = TMDB_IMAGE_LARGE;
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                imageSize = TMDB_IMAGE_XLARGE;
                break;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem = convertView;
        ViewHolder holder = null;

        if(gridItem == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            gridItem = inflater.inflate(layoutResourceId,parent,false);

            holder = new ViewHolder();

            holder.posterImageView = (ImageView)gridItem.findViewById(R.id.grid_item_movie_poster_imageview);
            holder.titleTextView = (TextView)gridItem.findViewById(R.id.grid_item_movie_title_textview);

            gridItem.setTag(holder);
        }
        else {
            holder = (ViewHolder)gridItem.getTag();
        }
        Movie movie = data.get(position);
        holder.titleTextView.setText(movie.getTitle());

        Picasso.with(context).load(movie.getImageUri(imageSize)).into(holder.posterImageView);
        holder.posterImageView.setContentDescription((CharSequence) movie.getTitle());

        if(movie.getImage() == "null"){
            holder.posterImageView.setVisibility(View.GONE);
            holder.titleTextView.setVisibility(View.VISIBLE);
        }
        else {
            holder.posterImageView.setVisibility(View.VISIBLE);
            holder.titleTextView.setVisibility(View.GONE);

        }
        return gridItem;
    }
    static class ViewHolder
    {
        ImageView posterImageView;
        TextView titleTextView;
    }
}