package com.eldad.yossi.popularmovs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Yossi on 26/10/2015.
 */
public class MovieDetailsActivity extends FragmentActivity {

    public MovieDetailsActivity(){}

    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

        //getting the imdb id in order to fetch the per movie data from TMDB
        String imdbId = getIntent().getStringExtra(getResources().getString(R.string.imdbid_key));

        //sending the uri to the fragment
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.detail_uri_arg), getIntent().getData().toString());

            MovieDetailsFragment fragment = new MovieDetailsFragment();

            FetchTrailersTask ft = new FetchTrailersTask(this,(FetchTrailersTask.TrailersCallback)fragment);
            ft.execute(imdbId);


            FetchReviewsTask fr = new FetchReviewsTask(this,(FetchReviewsTask.ReviewsCallback)fragment);
            fr.execute(imdbId);
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().add(R.id.movies_detail_container,fragment,DETAILS_FRAGMENT_TAG).commit();
        }



    }


}
