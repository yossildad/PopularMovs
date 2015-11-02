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
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.MOVIES_PATH + "/#",MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.FAVORITS_PATH, FAVORITES_LIST);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.FAVORITS_PATH + "/#",FAVORITE);
        return matcher;
    }

    static final int MOVIES_LIST = 100;
    static final int MOVIE = 101;
    static final int FAVORITES_LIST = 200;
    static final int FAVORITE = 201;


    @Override
    public String getType(Uri uri) {

        Integer match = matcher.match(uri);

        switch (match){
            case MOVIES_LIST:
                return MovieContract.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.CONTENT_ITEM_TYPE;
            case FAVORITES_LIST:
                return MovieContract.CONTENT_TYPE;
            case FAVORITE:
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

        //matching the uri
        switch (matcher.match(uri)) {
            case MOVIES_LIST: {
                //delete all entries in movie table (clear cache
                rowsNum = db.delete(MovieContract.MOVIE_TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITES_LIST:{
                rowsNum = db.delete(MovieContract.FAVORIT_TABLE_NAME, selection, selectionArgs);
                break;
            }
            //not sure if this case will be used
            case FAVORITE:{
                rowsNum = db.delete(MovieContract.FAVORIT_TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
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
        Cursor c = null;
        if (uri != null){


         switch (matcher.match(uri))
         {
            //for the detailed screen (a single movie), the selection will contain the id field name and the args the id number
             case MOVIE:{
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 String id = uri.getLastPathSegment();
                 c = db.query(MovieContract.MOVIE_TABLE_NAME, projection, MovieContract._ID + " = ? ", new String[]{id}, null, null, sortOrder);
                 Log.v("POP2", "Query MOVIE");
                 break;
             }
             //for the home screen in which
             case MOVIES_LIST:{
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 c = db.query(MovieContract.MOVIE_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                 Log.v("POP2", "Query MOVIE LIST");
                 break;
             }

             case FAVORITE: {
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 String id = uri.getLastPathSegment();
                 c = db.query(MovieContract.FAVORIT_TABLE_NAME, projection, MovieContract._ID + " = ? ", new String[]{id}, null, null, sortOrder);
                 Log.v("POP2", "Query Favorite");
                 break;
             }
             case FAVORITES_LIST:{
                 MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getContext());
                 SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
                 c = db.query(MovieContract.FAVORIT_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                 Log.v("POP2", "Query FAVORITE LIST. C.length is: " + c.getCount());
                 break;
             }

             default:
             {
                 throw new UnsupportedOperationException("Unknown uri: " + uri);
             }
         }
        if (c != null)
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
        }
        else return null;
    }

    //will be used mainly for inserting rows to favorite movie table
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        Uri uriInserted = null;
        switch (matcher.match(uri)){
            case MOVIES_LIST:{
                SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
                id = db.insert(MovieContract.MOVIE_TABLE_NAME, null, values);
                uriInserted = ContentUris.withAppendedId(MovieContract.MOVIE_CONTENT_URI, id);
                break;
            }
            case FAVORITES_LIST:
                SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
                id = db.insert(MovieContract.FAVORIT_TABLE_NAME, null, values);
                uriInserted = ContentUris.withAppendedId(MovieContract.FAVORIT_CONTENT_URI, id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return uriInserted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        String tableName;
        //matching the uri and deciding where to insert the data
        if (matcher.match(uri) == MOVIES_LIST)
        {
            tableName = MovieContract.MOVIE_TABLE_NAME;
        }
        else if (matcher.match(uri) == FAVORITES_LIST)
        {
            tableName = MovieContract.FAVORIT_TABLE_NAME;
        }
        else
            tableName = null;

        if (tableName != null && values != null) {
            SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
            db.beginTransaction();

            try {
                for (ContentValues cv : values) {
                    long newId = db.insertOrThrow(tableName, null, cv);

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
        String tableName;
        Uri destUri;
        if (matcher.match(uri) == MOVIE){
           tableName = MovieContract.MOVIE_TABLE_NAME;
            destUri = MovieContract.MOVIE_CONTENT_URI;
        }
        else if( matcher.match(uri) == FAVORITE){
            tableName = MovieContract.FAVORIT_TABLE_NAME;
            destUri = MovieContract.FAVORIT_CONTENT_URI;
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        id = db.update(tableName,values,selection,selectionArgs);
        uriInserted = ContentUris.withAppendedId(destUri, id);
        db.close();

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
