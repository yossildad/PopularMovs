package com.eldad.yossi.popularmovs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Tamar on 26/10/2015.
 */
//public class MovieDetailsFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FetchTrailersTask.TrailersCallback, FetchReviewsTask.ReviewsCallback{

//    private ArrayList<String> mReviewsList = null;
//    private ReviewsAdapter mReviewsAdapter = null;
//
//    private TrailersAdapter mAdapter = null;
    private ViewGroup mReviewsContainer;
    private ViewGroup mTrailersContainer;
    private String[] mKeys = null;
    private String[] mReviews = null;
    private static final int TRAILER_KEYS_TAG_POS = 1;

    private boolean mIsFavorites;
    private MovieDetailHolder holder;
    //the projection for the Loaders
    private static final String[] MOVIE_DETAILS_COLUMNS = {
            MovieContract._ID,
            MovieContract.COLUMN_TITLE,
            MovieContract.COLUMN_OVERVIEW,
            MovieContract.COLUMN_POSTER,
            MovieContract.COLUMN_RATING,
            MovieContract.COLUMN_RELEASE_DATE,
            MovieContract.COLUMN_IMDB_ID,
            MovieContract.COLUMN_VOTERS,
    };

    private Uri mUri = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

                if (args != null) {
            mUri = Uri.parse(args.getString(getResources().getString(R.string.detail_uri_arg)));
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        if (savedInstanceState != null){
            mKeys = savedInstanceState.getStringArray(getActivity().getResources().getString(R.string.trailers_instance_key));
            mReviews = savedInstanceState.getStringArray(getActivity().getResources().getString(R.string.reviews_instance_key));
            mReviewsContainer = (ViewGroup)rootView.findViewById(R.id.reviews_container);
            mTrailersContainer = (ViewGroup)rootView.findViewById(R.id.trailers_container);
            InitTrailerViews();
            InitReviewViews();
        }
        holder = new MovieDetailHolder(rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(getActivity().getResources().getString(R.string.trailers_instance_key),mKeys);
        outState.putStringArray(getActivity().getResources().getString(R.string.reviews_instance_key),mReviews);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0,null,this);
        mReviewsContainer = (ViewGroup)getActivity().findViewById(R.id.reviews_container);
        mTrailersContainer = (ViewGroup)getActivity().findViewById(R.id.trailers_container);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, MOVIE_DETAILS_COLUMNS, null, null, null);
        }
        else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {



        if (data != null && data.moveToFirst()) {

            //loading the data into the views.
            //there is no need in adapter since there is only one item with 5 views
            String releaseDate = Utility.fromJulian(Double.valueOf(data.getString(MovieContract.COL_RELEASE)));
            holder.textTitle.setText(data.getString(MovieContract.COL_TITLE) + " (" + releaseDate + ")");
            holder.textOverView.setText(data.getString(MovieContract.COL_OVERVIEW));
            holder.textRating.setText(data.getString(MovieContract.COL_RATING) + "/10");
            holder.textVoters.setText(data.getString(MovieContract.COL_VOTERS));

            //checking whether the movie is in the favorites
            Uri favoritesUri = MovieContract.FAVORIT_CONTENT_URI;
            Cursor favoriteCursor = null;
            favoriteCursor =getActivity().getContentResolver().query(favoritesUri,
                                                     MOVIE_DETAILS_COLUMNS,
                                                     MovieContract.COLUMN_IMDB_ID + " = ? ",
                                                     new String[]{data.getString(MovieContract.COL_IMDBID)},
                                                     null);
            if (favoriteCursor == null || !favoriteCursor.moveToFirst()){
                mIsFavorites = false;
            }
            else {
                mIsFavorites = true;
            }
            Log.v("POPS2","Detail Fragment onLoadFinish. voters is: " + data.getString(MovieContract.COL_VOTERS));
            Log.v("POPS2","Detail Fragment onLoadFinish. mIsFavorite is: " + mIsFavorites);

            //making sure that the star and the headline are hidden since the are hidden in the master detail mode app start
            holder.textTitle.setBackgroundColor(getResources().getColor(R.color.popmov_headlines));
            holder.star.setVisibility(View.VISIBLE);

            //loading the data to the view
            Picasso.with(getContext()).load(data.getString(MovieContract.COL_POSTER)).into(holder.imagePoster);
            holder.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsFavorites == false) {
                        ContentValues cv = new ContentValues();
                        cv.put(MovieContract.COLUMN_TITLE, data.getString(MovieContract.COL_TITLE));
                        cv.put(MovieContract.COLUMN_VOTERS, data.getString(MovieContract.COL_VOTERS));
                        cv.put(MovieContract.COLUMN_RELEASE_DATE, data.getString(MovieContract.COL_RELEASE));
                        cv.put(MovieContract.COLUMN_OVERVIEW, data.getString(MovieContract.COL_OVERVIEW));
                        cv.put(MovieContract.COLUMN_POSTER, data.getString(MovieContract.COL_POSTER));
                        cv.put(MovieContract.COLUMN_RATING, data.getString(MovieContract.COL_RATING));
                        cv.put(MovieContract.COLUMN_IMDB_ID, data.getString(MovieContract.COL_IMDBID));
                        Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.FAVORIT_CONTENT_URI, cv);
                        if (insertedUri != null) {
                            mIsFavorites = true;
                            Toast toast = Toast.makeText(getContext(), "added to favorites", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        String whereClause = MovieContract.COLUMN_IMDB_ID + " = ? ";
                        int deleted = getActivity().getContentResolver().delete(MovieContract.FAVORIT_CONTENT_URI, whereClause, new String[]{data.getString(MovieContract.COL_IMDBID)});
                        if (deleted > 0){
                            mIsFavorites = false;
                        }
                        Toast toast = Toast.makeText(getContext(), Integer.toString(deleted) + " movies removed from Favorites", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

        }
        //if there is no data that mean that we are in master detail mode and the star and the headline should be hidden
        else {
            holder.textTitle.setBackgroundColor(Color.parseColor("#000000"));
            holder.star.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void OnReviewsLoadFinished(String[] reviews) {
        mReviews = reviews;
        if (reviews != null){
            InitReviewViews();
        }
    }


    @Override
    public void OntrailersLoadFinished(String[] keys) {
        mKeys = keys;
        if (keys != null){
            InitTrailerViews();
        }
    }
    private void InitReviewViews (){
        //init the reviews views
        for (int i = 0;i<mReviews.length;i++){
            View reviewItem = LayoutInflater.from(getActivity()).inflate(R.layout.review_item,null);
            TextView reviewText = (TextView)reviewItem.findViewById(R.id.review_text);
            reviewText.setText(mReviews[i]);
            mReviewsContainer.addView(reviewItem);
        }
    }

    public void InitTrailerViews(){
        //init the trailers views
        for (int i=0;i<mKeys.length;i++){
            View trailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_list_item,null);
            TextView trailerText = (TextView) trailerItem.findViewById(R.id.trailer_text_item);
            ImageView trailerImage = (ImageView) trailerItem.findViewById(R.id.trailer_image_item);
            trailerText.setText("Watch trailer #"+ (i+1));
            trailerImage.setImageResource(R.drawable.play_button);
            trailerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //launch the youtube content provider with the key from the member
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mKeys[(int)v.getTag()]));
                    startActivity(intent);
                }
            });
            trailerItem.setTag(i);
            mTrailersContainer.addView(trailerItem);
        }
    }

    public class MovieDetailHolder{
        public final ImageView imagePoster;
        public final TextView textTitle;
        public final TextView textOverView;
        public final TextView textRating;
        public final TextView textVoters;
        public final ImageView star;
        public  MovieDetailHolder(View view){
            imagePoster = (ImageView)view.findViewById(R.id.movie_details_poster);
            textTitle = (TextView)view.findViewById(R.id.movie_details_title);
            textOverView = (TextView)view.findViewById(R.id.movie_details_overview);
            textRating = (TextView)view.findViewById(R.id.movie_details_rating);
            textVoters = (TextView)view.findViewById(R.id.movie_details_voters);
            star = (ImageView)view.findViewById(R.id.star);
        }
    }


}
