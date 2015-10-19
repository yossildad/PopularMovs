package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tamar on 18/10/2015.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //the movie table is only for cache so in case of upgrade it should be droped and recreated and repopulated
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //The Create Table statement for the movie table
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.TABLE_NAME + " (" + MovieContract.COLUMN_ID + "INTEGER PRIMARY KEY, " +
                MovieContract.COLUMN_TITLE + "TEXT NOT NULL, " +
                MovieContract.COLUMN_OVERVIEW + "TEXT NOT NULL, " +
                MovieContract.COLUMN_POSTER + "TEXT NOT NULL, " +
                MovieContract.COLUMN_RATING + "REAL NOT NULL, " +
                MovieContract.COLUMN_RELEASE_DATE + "INTEGER, NOT NULL, " +
                //in case I'll need it for stage 2
                MovieContract.COLUMN_IMDB_ID + "INTEGER";
    }
}
