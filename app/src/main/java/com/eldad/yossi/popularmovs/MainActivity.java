package com.eldad.yossi.popularmovs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.callback, FetchTrailersTask.TrailersCallback, FetchReviewsTask.ReviewsCallback{
    //saving the sort type in order to know when to reload the data on onResume
    public String mSort = null;
    private boolean mIsMasterDetail;
    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";
    private static final String TRAILERS_FRAGMENT_TAG = "TFTAG";
    private static final String REVIEWS_FRAGMENT_TAG = "RFTAG";


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("PMS", "Activity Onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("PMS", "Main Activity OnResume");
        MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        MainActivityFragment mf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (mf != null)
        {
            //if ()
        }
        //if the sorting has changed then the main screen parameters should be reset
        if (!mSort.equals(sp.getString(getResources().getString(R.string.preference_file_key),""))) {
            Log.v("PMS", "on resume inside if (sort has changed");
            mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));

            //going back to the first page and loading the data
            fragment.LoadPage(mSort,"1");

            //setting the scroll back to the start of the grid
            fragment.mScrollPosition = GridView.INVALID_POSITION;
        }
        Log.v("PMS", "Main Activity OnResume End");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("PMS", "Main Activity OnCreate");
        setContentView(R.layout.activity_main);
        //setting the sort type
        if (mSort == null)
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));
        }
        //finding out whether this is a master detail layout and replacing the container with the details fragment if so.
        if (findViewById(R.id.movies_detail_container) != null){
            mIsMasterDetail = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.movies_detail_container, new MovieDetailsFragment(), DETAILS_FRAGMENT_TAG).commit();
            }

        }
        else {
            mIsMasterDetail = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

       if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClicked(Uri movieUri, String imdbId) {
        //the uri that is relevant for popular and rated sorting
        Uri uri = movieUri;

        String id = movieUri.getLastPathSegment();
        String imdb = imdbId;

        //build a uri for the details fragment based on the favorites prefix so the it should no the get the data from favorites table
        if (mSort.equals(getResources().getString(R.string.sort_favorites))){

             uri= MovieContract.FAVORIT_CONTENT_URI.buildUpon().appendPath(id).build();
        }


        if (mIsMasterDetail) {
            //creating the details fragment
            Bundle args = new Bundle();
            args.putString(getResources().getString(R.string.detail_uri_arg), uri.toString());
            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(args);


            FetchTrailersTask ft = new FetchTrailersTask(this,(FetchTrailersTask.TrailersCallback)detailsFragment);
            ft.execute(imdb);

            FetchReviewsTask fr = new FetchReviewsTask(this,(FetchReviewsTask.ReviewsCallback)detailsFragment);
            fr.execute(imdb);

            getSupportFragmentManager().beginTransaction().replace(R.id.movies_detail_container, detailsFragment).commit();

        }
        else {
            //stating the movie details activity
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.setData(uri);
            intent.putExtra(getString(R.string.imdbid_key), imdb);
            startActivity(intent);
        }
    }

    @Override
    public void OntrailersLoadFinished(String[] keys) {
       //create the trailers fragment + send the keys + replace the placeholder
        //getSupportFragmentManager().beginTransaction().replace(R.id.trailers_container, ).commit();
    }

    @Override
    public void OnReviewsLoadFinished(String[] reviews) {
        //create the reviews fragment + send the text + replace the placeholder
        //getSupportFragmentManager().beginTransaction().replace(R.id.trailers_container, ).commit();
    }
}
