package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Yossi on 06/11/2015.
 * this task is fetching the review details of a specific movie from TMDB
 */
public class FetchReviewsTask extends AsyncTask<String,String,String[]>{

    //used in getting the api_key from the strings file since it is common with the other AsyncTasks
    private Context mContext;

    private ReviewsCallback mCallerActivity;

   //used for updating the detail fragment when review data is fetched
    public interface ReviewsCallback{
        public void OnReviewsLoadFinished(String[] reviews);
    }

    public FetchReviewsTask(Context context, ReviewsCallback rc){
        mContext = context;
        mCallerActivity = rc;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        mCallerActivity.OnReviewsLoadFinished(strings);
    }

    @Override
    protected String[] doInBackground(String... params) {
        //defined outside the try in order to close them a the finally
        HttpsURLConnection httpsURLConnection = null;
        BufferedReader reader = null;

        //the api_key
        String apiKey = mContext.getResources().getString(R.string.api_key);

        //the TMDB base url for trailers
        String trailerBaseUrl = "https://api.themoviedb.org/3/movie/";
        String trailerUrlSuffix = "reviews?api_key=";
        String[] reviews = null;

        // creating the URL by adding the movie id and the suffix (videos) to the base url
        Uri uriData = Uri.parse(trailerBaseUrl+params[0].toString()+"/"+trailerUrlSuffix+mContext.getResources().getString(R.string.api_key));
        try{
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
                    reviews= getReviewsFromJson(buffer.toString()) ;
                }
                else{
                    return null;}
            }
            else {
                return null;}
        }
        catch (IOException e){
           return null;
        }
        return reviews;
    }

    private String[] getReviewsFromJson(String json){
        //JSON Objects
        String JO_ARR = "results";
        String JO_KEY = "content";

        JSONObject movJson = null;

        String[] reviews = null;

        try {
            JSONObject jsonObject = new JSONObject(json);

            //get the json array of movies in the page
            JSONArray movJsonArray = jsonObject.getJSONArray(JO_ARR);
            if (movJsonArray.length() > 0) {
                reviews = new String[movJsonArray.length()+1];

                for (int i = 0; i < movJsonArray.length(); i++) {
                    movJson = movJsonArray.getJSONObject(i);
                    reviews[i] = movJson.getString(JO_KEY);
                }
            }
            //if the array was empty
            else {
                return null;
            }
        }

        catch (JSONException e) {
            return null;
        }
        return reviews;
    }
}
