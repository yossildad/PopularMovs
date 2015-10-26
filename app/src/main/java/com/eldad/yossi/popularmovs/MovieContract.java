package com.eldad.yossi.popularmovs;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by yossi on 08/10/2015.
 */
public class MovieContract {

    //the column order in the table for using with cursors
    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_OVERVIEW = 2;
    public static final int COL_POSTER = 3;
    public static final int COL_RATING = 4;
    public static final int COL_RELEASE = 5;
    public static final int COL_IMDBID = 6;


    //The authority of this application and no of the TMDB service
    public static final String CONTENT_AUTHORITY ="com.eldad.yossi.popularmovs";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIES_PATH = "movies";
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();



    //the content type for the movies list
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

    //the content type for a single movie
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";


    //Table data
    public static String _ID = "_id";
    public static String TABLE_NAME = "movie";

    public static String COLUMN_TITLE = "title";
    public static String COLUMN_POSTER = "poster";
    public static String COLUMN_RATING = "rating";
    public static String COLUMN_OVERVIEW = "overview";
    public static String COLUMN_RELEASE_DATE = "release";
    public static String COLUMN_GENRE = "genere";
    public static String COLUMN_IMDB_ID = "imdb_id";



}
