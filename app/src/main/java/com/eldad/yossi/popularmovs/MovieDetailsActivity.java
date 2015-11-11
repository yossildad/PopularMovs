package com.eldad.yossi.popularmovs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Tamar on 26/10/2015.
 */
public class MovieDetailsActivity extends FragmentActivity {

    public MovieDetailsActivity(){}

    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";
    private static final String TRAILERS_FRAGMENT_TAG = "TFTAG";
    private static final String REVIEWS_FRAGMENT_TAG = "RFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
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

            //start the async tasks for reviews and trailers and add replace the placeholder with the fragments !!!!
        }



    }


}
