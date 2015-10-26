package com.eldad.yossi.popularmovs;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

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
        int rowsNum = -1;
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
        Log.v("POPMOVS", "deleted. rowsNum: "+ rowsNum);
        return rowsNum;


    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;

         switch (matcher.match(uri))
         {
            //for the detailed screen (a single movie), the selection will contain the id field name and the args the id number
             case MOVIE:{
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 c = db.query(MovieContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                 Log.v("POPMOVS", "query Movie. c length is: " + c.getCount());
                 break;
             }
             //for the home screen in which
             case MOVIES_LIST:{
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 c = db.query(MovieContract.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                 Log.v("POPMOVS", "query Movie List. c length is: " + c.getCount());
                 break;
             }
             default:
             {
                 throw new UnsupportedOperationException("Unknown uri: " + uri);
             }
         }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        Uri uriInserted = null;
        if (matcher.match(uri) == MOVIES_LIST){
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            id = db.insert(MovieContract.TABLE_NAME, null, values);
            uriInserted = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI, id);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        Log.v("POPMOVS", "Insert NotifyChange. uri is: "+ uri.toString()+ " Inserted uri is: " + uriInserted.toString());
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
     //   super.bulkInsert(uri, values);
        if (matcher.match(uri) == MOVIES_LIST && values != null) {
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            db.beginTransaction();
            Log.v("POPMOVS", "BulkInsert");
            try {
                for (ContentValues cv : values) {
                    long newId = db.insertOrThrow(MovieContract.TABLE_NAME, null, cv);
                    Log.v("POPMOVS", "BulkInsert for loop. newId is: " + newId);
                    if (newId == -1)
                        throw new SQLException("Failed to insert row into " + uri);
                }

            }
            catch (SQLException e)
            {
                Log.e("MovieProvider","failed to insert row in bulk insert. the uri is: " + uri.toString());
            } finally {
                db.setTransactionSuccessful();
                db.endTransaction();
                getContext().getContentResolver().notifyChange(uri, null);
                Log.v("POPMOVS", "bulkinsert finaly. value of uri: " + uri.toString());
            }
        }
            else
            {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        return values.length;

    }

    //not used in this project
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int id;
        Uri uriInserted = null;
        if (matcher.match(uri) == MOVIE){
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            id = db.update(MovieContract.TABLE_NAME,values,selection,selectionArgs);
            uriInserted = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI, id);
            db.close();
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
