package com.eldad.yossi.popularmovs;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface callback{
        public void onMovieClicked(Uri movieUri, String imdbId);
    }

    private GridView mMoviesGrid = null;

    private MoviesAdapter mMovieAdapter = null;


    private Cursor mMoviesCursorData = null;

    public int mScrollPosition = GridView.INVALID_POSITION;

    String mSortOrder;

    //the last page that was fetched from TMDB and inserted to the DB
    public int mPage = 1;

    //the projection of the main screen
    private static final String[] MOVIE_COLUMNS = {
            MovieContract._ID,
            MovieContract.COLUMN_POSTER,
            //needed in order to correlate between the movie table and the favorite table since the _id is sequence and not meaningful
            MovieContract.COLUMN_IMDB_ID,
                            };

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mMoviesCursorData.close();
    }

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

        //getting the prefered sort order. popular is the default is the values was not changed/set by the user
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSortOrder = shp.getString(getResources().getString(R.string.preference_file_key), getResources().getString(R.string.sotr_popular));

        mMovieAdapter = new MoviesAdapter(getContext(),null,0);
        //inflating the fragment and setting the adapter
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);
        mMoviesGrid = (GridView) rootView.findViewById(R.id.movies_list);
        mMoviesGrid.setAdapter(mMovieAdapter);
        mMoviesGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisItem= 0;
            int totaVislItems = 0;
            int totalItems=0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //loading the next page if the user is reaching the end of the list
                    if (totalItems > 0) {
                        if ((firstVisItem + totaVislItems) / totalItems > Double.parseDouble(getActivity().getResources().getString(R.string.movies_list_page_percent))) {
                            mPage = mPage + 1;
                            LoadPage(mSortOrder, Integer.toString(mPage));
                            }
                    }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                 firstVisItem = firstVisibleItem;
                totaVislItems = visibleItemCount;
                totalItems = totalItemCount;

                if (firstVisibleItem != 0){
                    mScrollPosition = firstVisibleItem;
                }
            }

        });
        mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mMoviesCursorData.moveToPosition(position);
                //sending the uri to the main activity so it will launch the detail fragment (whether in a new Activity or inside the main activity
                ((callback)getActivity()).onMovieClicked(MovieContract.MOVIE_CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build(),mMoviesCursorData.getString(2));

            }
        });

        //reading the page and scroll position from the saveinstancestate
        if (savedInstanceState != null){
            mPage = savedInstanceState.getInt(getResources().getString(R.string.saved_page));
            mScrollPosition = savedInstanceState.getInt(getResources().getString(R.string.movies_grid_scroll_pos));
        }
        if (mScrollPosition == GridView.INVALID_POSITION){
            mMoviesGrid.smoothScrollToPosition(0);

        }
        return rootView;
    }

    public void refreshData(){
        getLoaderManager().restartLoader(0,null,this);
    }

    public void LoadPage(String sort, String page){

        //if the sorting is by favorites there is no reason to start the AsyncTask.
         if (sort != getActivity().getResources().getString(R.string.sort_favorites)){
                        //initiating AsyncTask to Fetch the data from TMDB and inserting it to the DB
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
            //using the sort from the shared preferences and the page from saved instance
            fetchMovieTask.execute(sort, page);

        }
        mSortOrder = sort;
        getLoaderManager().restartLoader(0, null, this);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving the current page in the save instance
        outState.putInt(getResources().getString(R.string.saved_page), mPage);
        outState.putInt(getResources().getString(R.string.movies_grid_scroll_pos),mScrollPosition);
    }

    @Override
    public void onLoaderReset(Loader loader) {
       mMovieAdapter.swapCursor(null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract._ID + " ASC";

        //if the sotring is by favorites the cursor should take the data from favorites table since it is not delete every time
        if (mSortOrder.equals(getActivity().getResources().getString(R.string.sort_favorites))) {
            return new CursorLoader(getActivity(), MovieContract.FAVORIT_CONTENT_URI, MOVIE_COLUMNS, null, null, sortOrder);
        }
        else {
            return new CursorLoader(getActivity(), MovieContract.MOVIE_CONTENT_URI, MOVIE_COLUMNS, null, null, sortOrder);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       //keaping the scroll position
        mMoviesCursorData = data;
        mMovieAdapter.swapCursor(data);
        if (mScrollPosition != GridView.INVALID_POSITION){
            mMoviesGrid.smoothScrollToPosition(mScrollPosition);
        }
    }

}
