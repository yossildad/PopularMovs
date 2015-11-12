package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.callback{

    //saving the sort type in order to know when to reload the data on onResume
    public String mSort = null;
    private boolean mIsMasterDetail;
    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";



    @Override
    protected void onStart() {
        super.onStart();

       //checking internet conectivity state and notifying the user if the device is disconnected since only favorite data will be shown
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null)
        {
            Toast toast = Toast.makeText(this,"Internet connection is required for this app. please check your internet connection",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //if the sorting has changed then the main screen parameters should be reset
        if (!mSort.equals(sp.getString(getResources().getString(R.string.preference_file_key),""))) {
            mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));

            //going back to the first page and loading the data
            fragment.LoadPage(mSort,"1");

            //setting the scroll back to the start of the grid
            fragment.mScrollPosition = GridView.INVALID_POSITION;

            //"hiding the details fragment when coming back to the main fragment in master detail mode after settings changes
            if (mIsMasterDetail){
                findViewById(R.id.movies_detail_container).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            //fetching trailers data
            FetchTrailersTask ft = new FetchTrailersTask(this,(FetchTrailersTask.TrailersCallback)detailsFragment);
            ft.execute(imdb);

            //fetching reviews data
            FetchReviewsTask fr = new FetchReviewsTask(this,(FetchReviewsTask.ReviewsCallback)detailsFragment);
            fr.execute(imdb);

            getSupportFragmentManager().beginTransaction().replace(R.id.movies_detail_container, detailsFragment).commit();
            findViewById(R.id.movies_detail_container).setVisibility(View.VISIBLE);

        }
        else {
            //stating the movie details activity
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.setData(uri);
            intent.putExtra(getString(R.string.imdbid_key), imdb);
            startActivity(intent);
        }
    }
}
