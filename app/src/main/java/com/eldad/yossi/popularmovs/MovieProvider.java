package com.eldad.yossi.popularmovs;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Yossi on 18/10/2015.
 */
public class MovieProvider extends ContentProvider {

    //a db member that will be used to update and read the

    //initiating the matcher with no match
    private UriMatcher matcher = buildUriMatcher();

    private MoviesDbHelper moviesDbHelper;

    private UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(0);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.MOVIES_PATH,MOVIES_LIST);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.MOVIES_PATH + "/*/#",MOVIE);
        return matcher;
    }

    static final int MOVIES_LIST = 100;
    static final int MOVIE = 101;

    @Override
    public String getType(Uri uri) {

        Integer match = matcher.match(uri);

        switch (match){
            case MOVIES_LIST:
                return MovieContract.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.CONTENT_ITEM_TYPE;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsNum = 0;
        //in order to delete all the table when selection is null
        if (selection == null) selection = "1";

        moviesDbHelper = new MoviesDbHelper(getContext());
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        //making sure that the uri is ok
        if (matcher.match(uri) == MOVIES_LIST) {
            rowsNum = db.delete(MovieContract.TABLE_NAME, selection, selectionArgs);
        }
        else{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsNum != 0) {
            getContext().getContentResolver().notifyChange(uri, null);}
        return rowsNum;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        Uri uriInserted = null;
        if (matcher.match(uri) == MOVIE){
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            id = db.insert(MovieContract.TABLE_NAME,null,values);
            uriInserted = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI,id);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uriInserted,null);
        return uriInserted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        super.bulkInsert(uri, values);
        int rowsNum = 0;
        if (matcher.match(uri) == MOVIE) {
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            db.beginTransaction();
            try{
                for (rowsNum = 0;rowsNum > values.length;rowsNum++){
                    db.insertOrThrow(MovieContract.TABLE_NAME,null,values[rowsNum]);
                }
            }

            finally {
                db.endTransaction();
            }
            }
        return rowsNum + 1;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int id;
        Uri uriInserted = null;
        if (matcher.match(uri) == MOVIE){
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            id = db.update(MovieContract.TABLE_NAME,values,selection,selectionArgs);
            uriInserted = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI, id);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uriInserted,null);
        return id;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        moviesDbHelper.close();
        super.shutdown();
    }
}
