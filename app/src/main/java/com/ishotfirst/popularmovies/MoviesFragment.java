package com.ishotfirst.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {


    private MovieAdapter mMoviesAdapter;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        Movie[] movieArray = {
                new Movie("Loading")
        };

        ArrayList<Movie> movies = new ArrayList<Movie>(Arrays.asList(movieArray));

        mMoviesAdapter = new MovieAdapter(getActivity(),R.layout.grid_item_movie, movies);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getActivity();
                String title = mMoviesAdapter.getItem(i).getTitle();

                Intent detailIntent = new Intent(context,DetailActivity.class)
                        .putExtra("Movie", mMoviesAdapter.getItem(i));

                startActivity(detailIntent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), mMoviesAdapter.getItem(position).getTitle(), duration);
                toast.show();

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void updateMovies()    {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void,Void,List<Movie>>
    {

        private static final String SORT_BY_RATING = "vote_average.desc";
        private static final String QUERY_KEY_MIN_VOTE_COUNT = "vote_count.gte";
        private static final String MINIMUM_VOTE_COUNT = "85"; //matches top rated page on themoviedb.org

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private static final String QUERY_KEY_SORT_BY = "sort_by";
        private static final String QUERY_KEY_API_KEY = "api_key";
        private static final String QUERY_KEY_MAX_DATE = "primary_release_date.lte";
        private static final String API_KEY = "PUT REAL KEY HERE";

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);
            if(!movieList.isEmpty()){
                mMoviesAdapter.clear();
                mMoviesAdapter.addAll(movieList);
            }
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());




            try
            {
                String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
                boolean includeUnlreleasedBoolean = prefs.getBoolean(getString(R.string.pref_include_unreleased_key), Boolean.parseBoolean(getString(R.string.pref_include_unreleased_default)));
                Uri.Builder tmdbUri = new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter(QUERY_KEY_SORT_BY, sortBy)
                        .appendQueryParameter(QUERY_KEY_API_KEY, API_KEY);

                if(!includeUnlreleasedBoolean){
                    Date today = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    tmdbUri.appendQueryParameter(QUERY_KEY_MAX_DATE, df.format(today) );
                }
                if(sortBy.equals(SORT_BY_RATING)){
                    tmdbUri.appendQueryParameter(QUERY_KEY_MIN_VOTE_COUNT, MINIMUM_VOTE_COUNT);
                }
                Log.v(LOG_TAG,"API URL: "+tmdbUri.build().toString());

                URL url = new URL(tmdbUri.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                try {
                    return getMovieDataFromJson(moviesJsonStr);
                } catch (JSONException e){
                    Log.e(LOG_TAG,"Error",e);
                }

            }catch (IOException e) {
                Log.e(LOG_TAG,"Error", e);
                return null;
            }finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            return null;
        }

        private List<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_IMAGE = "poster_path";
            final String TMDB_RATING = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            List<Movie> resultList = new ArrayList<Movie>();

            for(int i = 0;i<moviesArray.length();i++){
                JSONObject movieJson = moviesArray.getJSONObject(i);
                Movie movie = new Movie(getStringFromJson(TMDB_TITLE, movieJson));
                movie.setImage(getStringFromJson(TMDB_IMAGE, movieJson));
                movie.setOverview(getStringFromJson(TMDB_OVERVIEW, movieJson));
                movie.setRating(getStringFromJson(TMDB_RATING, movieJson));
                movie.setReleaseDate(getStringFromJson(TMDB_RELEASE_DATE, movieJson));

                resultList.add(movie);


            }

            return resultList;
        }
        private String getStringFromJson(String key, JSONObject jsonObject){
            String parsed = "null";
            try{
                parsed = jsonObject.getString(key);
            }catch (JSONException e)
            {
                Log.e(LOG_TAG,"Error ",e);
            }
            return parsed;
        }
    }



}
