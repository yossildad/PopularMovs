package com.eldad.yossi.popularmovs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Tamar on 26/10/2015.
 */
public class MovieDetailsActivity extends FragmentActivity implements FetchTrailersTask.TrailersCallback, FetchReviewsTask.ReviewsCallback{

    public MovieDetailsActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

        //sending the uri to the fragment
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.detail_uri_arg), getIntent().getData().toString());

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().add(R.id.movies_detail_container,fragment).commit();

            //start the async tasks for reviews and trailers and add replace the placeholder with the fragments !!!!
        }
    }

    @Override
    public void OntrailersLoadFinished(String[] keys) {

    }

    @Override
    public void OnReviewsLoadFinished(String[] reviews) {

    }
}
