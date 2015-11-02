package com.eldad.yossi.popularmovs;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Tamar on 26/10/2015.
 */
//public class MovieDetailsFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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
   // private MoviesAdapter mMovieAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        Log.v("POPS2", "Detail Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        Log.v("POPS2", "Detail Fragment onCreateView");
        if (args != null) {
            mUri = Uri.parse(args.getString(getResources().getString(R.string.detail_uri_arg)));
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        holder = new MovieDetailHolder(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),mUri,MOVIE_DETAILS_COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("POPMOVS", "OnLoadFinish. data.getCount() is: " + data.getCount());

        //mMovieAdapter.swapCursor(data);

        //loading the data into the views.
        //there is no need in adapter since there is only one item with 5 views
        if (data != null && data.moveToFirst()) {
            Picasso.with(getContext()).load(data.getString(MovieContract.COL_POSTER)).into(holder.imagePoster);
            String releaseDate = Utility.fromJulian(Double.valueOf(data.getString(MovieContract.COL_RELEASE)));
            holder.textTitle.setText(data.getString(MovieContract.COL_TITLE) + " (" + releaseDate + ")");
            holder.textOverView.setText(data.getString(MovieContract.COL_OVERVIEW));
            holder.textRating.setText(data.getString(MovieContract.COL_RATING)+"/10");
            holder.textVoters.setText(data.getString(MovieContract.COL_VOTERS));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    //    mMovieAdapter.swapCursor(null);
    }

    public class MovieDetailHolder{
        public final ImageView imagePoster;
        public final TextView textTitle;
        public final TextView textOverView;
        public final TextView textRating;
        public final TextView textVoters;
        public  MovieDetailHolder(View view){
            imagePoster = (ImageView)view.findViewById(R.id.movie_details_poster);
            textTitle = (TextView)view.findViewById(R.id.movie_details_title);
            textOverView = (TextView)view.findViewById(R.id.movie_details_overview);
            textRating = (TextView)view.findViewById(R.id.movie_details_rating);
            textVoters = (TextView)view.findViewById(R.id.movie_details_voters);
        }
    }


}
