package com.eldad.yossi.popularmovs;

import android.content.ContentResolver;
import android.content.ContentValues;
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
 * Created by Yossi on 14/10/2015.
 */
public class FetchMovieTask extends AsyncTask<String,Integer,Integer> {



    private Context mContext;
    private MoviesAdapter mAdapter;
    private int mTotalPages;
    private int mTotalResults;


public FetchMovieTask(Context context, MoviesAdapter adapter){

    mContext = context;
    mAdapter = adapter;
    }

    @Override
    protected void onPostExecute(Integer o) {
        super.onPostExecute(o);
//        if (o > 0){
//          mFragment.onProcessFinish();
//        }
//        Log.v("POPMOVS", "OnPostExec. o is: "+o.toString());

    }

    @Override
    protected Integer doInBackground(String... params) {


        // base url for getting movies sorted by user rating
        final String RATED_URL = "https://api.themoviedb.org/3/movie/top_rated";

        // base url for getting movies sorted by popularity
        final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular";

        //a key required for TMDB authentication
        final String API_KEY = "a26ea2689e48792c72d2cd6dc77bb996";


        //if there are no params the sorting order and page cannot be decided
        if (params.length == 0) {
            Log.v("POPMOVS", "doinbck. param length is 0");
            return -1;
        }
        //delete previous data since the stop and destroy methods are controled by the OS
        String page = params[1].toString();
        if (page == "1"){
            mContext.getContentResolver().delete(MovieContract.CONTENT_URI,null,null);
        }


                //defined outside the try in order to close them a the finally
        HttpsURLConnection httpsURLConnection = null;
        BufferedReader reader = null;

        //the first param is the sorting order
        String sortType = params[0].toString();

        try {
            Uri uriData = null;
            Log.v("POPMOVS", "doinback. sortType is: " + sortType + "and resource is: "+ mContext.getResources().getString(R.string.sotr_popular));
            if (mContext.getResources().getString(R.string.sort_rated).equals(sortType)) {

                //building the uri in case the requested order is by user rating

                    uriData = Uri.parse(RATED_URL).buildUpon().appendQueryParameter("page", page).appendQueryParameter("api_key", API_KEY).build();
                }

                //building the uri in case the requested order is by popularity
                else if (mContext.getResources().getString(R.string.sotr_popular).equals(sortType)) {
                    uriData = Uri.parse(POPULAR_URL).buildUpon().appendQueryParameter("page", page).appendQueryParameter("api_key", API_KEY).build();
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
                    return 0;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line+"\n");
                }

                if (buffer.length() != 0)
                {
                   //if everything was ok the new lines will be replace to old ones and the adapter will be notified by the content resolver
                      //mContext.getContentResolver().bulkInsert(MovieContract.CONTENT_URI,getMovieDetailFromJson(buffer.toString()));
                    return UpdateSqliteCache(getMovieDetailFromJson(buffer.toString()), params[1].toString());
                }
                else
                    return 0;
            }
            else
                Log.v("POPMOVS","doinbck. uri data is null");
                return -1;
        }
        catch (IOException e){
            return -1;
        }

    }

// parse the json and return an array of movies
    public ContentValues[] getMovieDetailFromJson(String jsonString){

        //JSON Objects
        String JO_ARR = "results";
        String JO_TITLE = "original_title";
        String JO_OVERVIEW = "overview";
        String JO_POSTER = "poster_path";
        String JO_RATING = "vote_average";
        String JO_RELEASE = "release_date";
        String JO_TOTAL_PAGES = "total_pages";
        String JO_TOTAL_RESULTS = "total_results";

        ContentValues[] cvArr = null;
        JSONObject movJson = null;
        
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //get the json array of movies in the page
            JSONArray movJsonArray = jsonObject.getJSONArray(JO_ARR);

            mTotalPages = jsonObject.getInt(JO_TOTAL_PAGES);
            mTotalResults = jsonObject.getInt(JO_TOTAL_RESULTS);
            
            if (movJsonArray.length()> 0){

                cvArr = new ContentValues[movJsonArray.length()];
            }
            //until the end of the page
            for(int i = 0; i < movJsonArray.length(); i++){
               ContentValues cv = new ContentValues();
                //get the data of the relevant element into the  
                movJson = movJsonArray.getJSONObject(i);


                cv.put(MovieContract.COLUMN_TITLE, movJson.getString(JO_TITLE));
                cv.put(MovieContract.COLUMN_OVERVIEW, movJson.getString(JO_OVERVIEW));
                //I removed the first character so I wont need to decode the /
                cv.put(MovieContract.COLUMN_POSTER, CreatePosterUrl(movJson.getString(JO_POSTER).substring(1)));
                cv.put(MovieContract.COLUMN_RATING, movJson.getDouble(JO_RATING));

                //saving the date in julian since there is no date in Sqlite
                int julDate = Utility.toJulian(movJson.getString(JO_RELEASE));
                cv.put(MovieContract.COLUMN_RELEASE_DATE, Integer.toString(julDate));

                cvArr[i] = cv;
                }
        }
            catch (JSONException e) {

                //!!!!! change the TAG to a more elegant value
                Log.e("FetchTask","could not parse TMDB JSON: " + movJson.toString());
                Log.e("FetchTask","the exception is: "+ e.toString());
                }
        return cvArr;

    }
    public String CreatePosterUrl (String eofURL){
        Uri imageUri = Uri.parse( mContext.getResources().getString(R.string.image_base_url)).buildUpon().appendPath(mContext.getResources().getString(R.string.image_format)).appendEncodedPath(eofURL).build();
       // return mContext.getResources().getString(R.string.image_base_url) + "/" + mContext.getResources().getString(R.string.image_format)+"/"+eofURL;
        return imageUri.toString();
    }

    //adds movies to the db if they are not already there.
    //using the db as cache means that it will be replaced every time the app launches
    public Integer UpdateSqliteCache(ContentValues[] cv, String page)
    {
        int numInserted = -1;
        int lcPage = Integer.parseInt(page);
        ContentResolver cr = mContext.getContentResolver();

        //if there is no data from TMDB
        if (cv == null){
            return  numInserted;
        }

        if (lcPage == 1){
            cr.delete(MovieContract.CONTENT_URI,null,null);
                numInserted = cr.bulkInsert(MovieContract.CONTENT_URI,cv);
        }
        else
        {
            numInserted = cr.bulkInsert(MovieContract.CONTENT_URI,cv);
        }
        cr.notifyChange(MovieContract.CONTENT_URI,null);
        return numInserted;
//
//        //get the current data from the db of the relevant page
//        Cursor c = cr.query(MovieContract.CONTENT_URI, null, MovieContract._ID + " BETWEEN ? AND ? " ,
//                new String[]{Integer.toString(getLowerRownumOfPage(lcPage)), Integer.toString(getUpperRownumOfPage(lcPage))}, null);
//
//
//
//        //if the db is empty or it is a new page insert the new data from TMDB
//        if (!c.moveToFirst()){
//            numInserted = cr.bulkInsert(MovieContract.CONTENT_URI,cv);
//        }
//
//        else if (c.getCount() !=cv.length){
//            cr.delete(MovieContract.CONTENT_URI,null,null);
//            numInserted = cr.bulkInsert(MovieContract.CONTENT_URI,cv);
//        }
//        //if the DB is not empty compare to the data from TMDB
//        else
//        {
//            int i = 0;
//            boolean isDiff = false;
//            do {
//                //making sure that there will be no out of array index exception
//                if (i<cv.length) {
//                    //checking all the fields and not just IMDBID in case that other field need to be update
//                    if (!c.getString(MovieContract.COL_TITLE).equals(cv[i].get(MovieContract.COLUMN_TITLE)) ||
//                            !c.getString(MovieContract.COL_OVERVIEW).equals(cv[i].get(MovieContract.COLUMN_OVERVIEW)) ||
//                            !c.getString(MovieContract.COL_POSTER).equals(cv[i].get(MovieContract.COLUMN_POSTER)) ||
//                            c.getDouble(MovieContract.COL_RATING) != (Double) (cv[i].get(MovieContract.COLUMN_RATING)) ||
//                            !c.getString(MovieContract.COL_RELEASE).equals(cv[i].get(MovieContract.COLUMN_RELEASE_DATE))) {
//                        isDiff = true;
//                    }
//                    i += 1;
//                }
//                //if i >= cv.length it means that there is more rows in the DB and the data should
//                else if (i>cv.length)
//                    isDiff = true;
//            }
//            while (c.moveToNext());
//            // if the data is not identical the DB data will be deleted and the TMDB data will be inserted
//            //it can be optimized by using update if only one row was updated (rating changed for example)
//            if (isDiff == true)
//            {
//                cr.delete(MovieContract.CONTENT_URI,null,null);
//                numInserted = cr.bulkInsert(MovieContract.CONTENT_URI,cv);
//            }
//        }
//
//        if (numInserted > 0){
//            cr.notifyChange(MovieContract.CONTENT_URI,null);
//
//        }
    }
    private int getUpperRownumOfPage(Integer page){
        return page * (mTotalResults / mTotalPages) + 1;
    }

    private int getLowerRownumOfPage(Integer page){
        return ((page -1) * (mTotalResults / mTotalPages));
    }
}
