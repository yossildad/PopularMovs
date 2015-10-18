package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Yossi on 14/10/2015.
 */
public class FetchMovieTask extends AsyncTask<String,Integer,MyMovie[]> {

    private Context mContext;

public FetchMovieTask(Context context){
    mContext = context;
}

    @Override
    protected void onPostExecute(MyMovie[] o) {
        super.onPostExecute(o);

    }

    @Override
    protected MyMovie[] doInBackground(String... params) {


        // base url for getting movies sorted by user rating
        final String RATED_URL = "https://api.themoviedb.org/3/movie/top_rated";

        // base url for getting movies sorted by popularity
        final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular";

        //a key required for TMDB authentication
        final String API_KEY = "a26ea2689e48792c72d2cd6dc77bb996";

        //if there are no params the sorting order and page cannot be decided
        if (params.length == 0)
        return null;

        //defined outside the try in order to close them a the finally
        HttpsURLConnection httpsURLConnection = null;
        BufferedReader reader = null;

        //the first param is the sorting order
        String sortType = params[0].toString();

        try {
            Uri uriData = null;

            if (sortType == mContext.getResources().getString(R.string.sort_rated)) {

                //building the uri in case the requested order is by user rating

                    uriData = Uri.parse(RATED_URL).buildUpon().appendQueryParameter("page", params[1].toString()).appendQueryParameter("api_key", API_KEY).build();
                }

                //building the uri in case the requested order is by popularity
                else if (mContext.getResources().getString(R.string.sotr_popular) == sortType) {
                    uriData = Uri.parse(POPULAR_URL).buildUpon().appendQueryParameter("page", params[1].toString()).appendQueryParameter("api_key", API_KEY).build();
                }

                //if the sort order was not recognized
                else {
                    uriData = null;
                }

            if (uriData != null)
            {
                //opening the connection to TMDB
                URL url = new URL(uriData.toString());
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();
                InputStream inputStream = httpsURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                //if the response was empty
                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line+"\n");
                }

                if (buffer.length() != 0)
                {
                   return getMovieDetailFromJson(buffer.toString());
                }


            }
            return null;
        }
        catch (IOException e){
            return null;
        }

    }

// parse the json and return an array of movies
    public MyMovie[] getMovieDetailFromJson(String jsonString){

        //JSON Objects
        String JO_ARR = "results";
        String JO_TITLE = "original_title";
        String JO_OVERVIEW = "overview";
        String JO_POSTER = "poster_path";
        String JO_RATING = "vote_average";
        String JO_RELEASE = "release_date";
        MyMovie[] movArr = null;
        
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //get the json array of movies in the page
            JSONArray movJsonArray = jsonObject.getJSONArray(JO_ARR);
            
            if (movJsonArray.length()> 0){
                movArr = new MyMovie[movJsonArray.length()];
            }
            //until the end of the page
            for(int i = 0; i < movJsonArray.length(); i++){
               
                //get the data of the relevant element into the  
                JSONObject movJson = movJsonArray.getJSONObject(i);
                MyMovie myMovie = new MyMovie();
                
                myMovie.setmTitle(movJson.getString(JO_TITLE));
                myMovie.setmOverview(movJson.getString(JO_OVERVIEW));
                myMovie.setmPosterPath(CreatePosterUrl(movJson.getString(JO_POSTER)));
                myMovie.setmRating(movJson.getDouble(JO_RATING));
                String releaseString = movJson.getString(JO_RELEASE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date convertedDate = new Date();
                try {
                    myMovie.setmReleaseDate(convertedDate = dateFormat.parse(releaseString));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                movArr[i] = myMovie;
            }
            return movArr;
        }
        catch (JSONException e) {

            return null;
        }
    }
    public String CreatePosterUrl (String eofURL){
        Uri imageUri = Uri.parse( mContext.getResources().getString(R.string.image_base_url)).buildUpon().appendPath(mContext.getResources().getString(R.string.image_format)).appendPath(eofURL).build();
        return imageUri.toString();
    }
}
