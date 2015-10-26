package com.eldad.yossi.popularmovs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesAdapter mMovieAdapter = null;

    //the last page that was fetched from TMDB and inserted to the DB
    public int mPage = 1;

    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract._ID,
            MovieContract.COLUMN_TITLE,
            MovieContract.COLUMN_OVERVIEW,
            MovieContract.COLUMN_POSTER,
            MovieContract.COLUMN_RATING,
            MovieContract.COLUMN_RELEASE_DATE,
            MovieContract.COLUMN_IMDB_ID,
                };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MoviesAdapter(getContext(),null,0);
        //inflating the fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_main);
        listView.setAdapter(mMovieAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisItem= 0;
            int totaVislItems = 0;
            int totalItems=0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE && totalItems >0){
                    if ((firstVisItem + totaVislItems)/totalItems > 0.7){
                        LoadPage();
                        mPage = mPage +1;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //loading the next page if the user scrolled more the 75% of the list
                firstVisItem = firstVisibleItem;
                totaVislItems = visibleItemCount;
                totalItems = totalItemCount;
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),MovieDetailsActivity.class);
                intent.setData(MovieContract.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build());
                startActivity(intent);
            }
        });

        //getting the page

        if (savedInstanceState != null){
            mPage = savedInstanceState.getInt(getResources().getString(R.string.saved_page));
        }

        return rootView;

    }

    public void refreshData(){
        getLoaderManager().restartLoader(0,null,this);
    }

    public void LoadPage(){
        //getting the prefered sort order. popular is the default is the values was not changed/set by the user
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getContext());

      //  String sortOrder = shp.getString(getResources().getString(R.string.preference_file_key), getResources().getString(R.string.sotr_popular));
        String sortOrder = shp.getString(getResources().getString(R.string.preference_file_key), getResources().getString(R.string.sotr_popular));

        Log.v("POPMOVS", "LoadNextData. sortOrder is: " + sortOrder);
        //initiating AsyncTask to Fetch the data from TMDB and inserting it to the DB
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(),mMovieAdapter);
        //using the sort from the shared preferences and the page from saved instance
        fetchMovieTask.execute(sortOrder,Integer.toString(mPage));
        getLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving the current page in the save instance
        outState.putInt(getResources().getString(R.string.saved_page),mPage);
    }

    @Override
    public void onLoaderReset(Loader loader) {
       mMovieAdapter.swapCursor(null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract._ID + " ASC";
        return new CursorLoader(getActivity(),MovieContract.CONTENT_URI,MOVIE_COLUMNS,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        //!!!! לעדכען את המיקום של ה SCROLL
    }

}
