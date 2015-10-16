package com.ishotfirst.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Max on 10/16/2015.
 */
public class Movie implements Parcelable {

    private static final String LOG_TAG = Movie.class.getSimpleName();
    private String overview;
    private String rating;
    private String releaseDate;
    private String image;
    private String title;

    public Movie(String title) {
        this.title = title;
    }

    public Movie(String overview, String rating, String releaseDate, String image, String title) {
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.image = image;
        this.title = title;
    }

    protected Movie(Parcel in) {
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        image = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        dest.writeString(image);
        dest.writeString(title);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        if(releaseDate=="null")
            return "Unknown";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date rDate =format.parse(releaseDate);
            format = DateFormat.getDateInstance();
            return format.format(rDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error", e);
        }
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image.replace("/","");
    }

    public Uri getImageUri(String imageSizePath){
        Uri posterUri = Uri.parse("http://image.tmdb.org/t/p/").buildUpon()
                .appendPath(imageSizePath)
                .appendPath(image)
                .build();
        return posterUri;
    }


}
