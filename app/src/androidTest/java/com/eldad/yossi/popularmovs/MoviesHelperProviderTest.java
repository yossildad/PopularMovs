package com.eldad.yossi.popularmovs;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Tamar on 19/10/2015.
 */
public class MoviesHelperProviderTest extends AndroidTestCase {
    public void testCreateDB()
    {

//        final HashSet<String> tableNameHashSet = new HashSet<String>();
//        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
//        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        boolean isTableExists = false;
        do{
            if (c.getString(0).equals(MovieContract.TABLE_NAME))
                isTableExists = true;
        }
        while (c.moveToNext());

        assertTrue("Error: the table name is not correct", isTableExists);

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.COLUMN_ID);
        locationColumnHashSet.add(MovieContract.COLUMN_IMDB_ID);
        locationColumnHashSet.add(MovieContract.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MovieContract.COLUMN_POSTER);
        locationColumnHashSet.add(MovieContract.COLUMN_RATING);
        locationColumnHashSet.add(MovieContract.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(MovieContract.COLUMN_TITLE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType()
    {
        String type = mContext.getContentResolver().getType(MovieContract.CONTENT_URI);
        assertEquals("Error: the BASE_CONTENT_URI type is not correct",
                MovieContract.CONTENT_TYPE, type);
    }
    public void testInsertAndQuery()
    {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.COLUMN_TITLE, "Title");
        cv.put(MovieContract.COLUMN_OVERVIEW, "Very Big Overview");
        cv.put(MovieContract.COLUMN_RATING,8.3);
        cv.put(MovieContract.COLUMN_POSTER,"http://a.com/b.jpg");
        cv.put(MovieContract.COLUMN_RELEASE_DATE, 2009218);

        Uri inserted = mContext.getContentResolver().insert(MovieContract.CONTENT_URI,cv);

        long rowId = ContentUris.parseId(inserted);
        assertTrue(rowId != -1);
        Cursor c = mContext.getContentResolver().query(MovieContract.CONTENT_URI,null,null,null,null);

        assertTrue(c.moveToFirst());
        do
        {
            if (c.getLong(0) == rowId){
                assertEquals("Title didn't match", cv.get(MovieContract.COLUMN_TITLE), c.getString(1));
                assertEquals("OverView didn't match", cv.get(MovieContract.COLUMN_OVERVIEW), c.getString(2));
                assertEquals("Poster didn't match", cv.get(MovieContract.COLUMN_POSTER), c.getString(3));
                assertEquals("Rating didn't match", cv.get(MovieContract.COLUMN_RATING), c.getDouble(4));
                assertEquals("Release didn't match", cv.get(MovieContract.COLUMN_RELEASE_DATE), c.getInt(5));
            }
        }
        while(c.moveToNext());
    }
    public void testDelete()
    {
    //test deleting all records
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.COLUMN_TITLE, "Title");
        cv.put(MovieContract.COLUMN_OVERVIEW, "Very Big Overview");
        cv.put(MovieContract.COLUMN_RATING,8.3);
        cv.put(MovieContract.COLUMN_POSTER,"http://a.com/b.jpg");
        cv.put(MovieContract.COLUMN_RELEASE_DATE, 2009218);

        Uri inserted = mContext.getContentResolver().insert(MovieContract.CONTENT_URI,cv);

        long rowId = ContentUris.parseId(inserted);
        assertTrue(rowId != -1);


        int deletedRows = mContext.getContentResolver().delete(MovieContract.CONTENT_URI,null,null);
        assertTrue("All rows delete failed", deletedRows > 0);

        //adding a new row in order to test deleting a specific row
        cv.clear();
        cv.put(MovieContract.COLUMN_TITLE, "Title");
        cv.put(MovieContract.COLUMN_OVERVIEW, "Very Big Overview");
        cv.put(MovieContract.COLUMN_RATING,8.3);
        cv.put(MovieContract.COLUMN_POSTER,"http://a.com/b.jpg");
        cv.put(MovieContract.COLUMN_RELEASE_DATE, 2009218);

        inserted = mContext.getContentResolver().insert(MovieContract.CONTENT_URI, cv);
        rowId = ContentUris.parseId(inserted);
        assertTrue(rowId != -1);
        deletedRows = mContext.getContentResolver().delete(MovieContract.CONTENT_URI,MovieContract.COLUMN_ID + " = ?",new String[]{Long.toString(rowId)});
        assertTrue("Deleting one row failed",deletedRows == 1);


    }
    public void testBulkInsert()
    {
        //delete all rows for a clean start
        int deletedRows = mContext.getContentResolver().delete(MovieContract.CONTENT_URI,null,null);
        assertTrue("Deletion failed", deletedRows != -1);

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.COLUMN_TITLE, "Title");
        cv.put(MovieContract.COLUMN_OVERVIEW, "Very Big Overview");
        cv.put(MovieContract.COLUMN_RATING,8.3);
        cv.put(MovieContract.COLUMN_POSTER,"http://a.com/b.jpg");
        cv.put(MovieContract.COLUMN_RELEASE_DATE, 2009218);

        ContentValues cv2 = new ContentValues();
        cv2.put(MovieContract.COLUMN_TITLE, "Title2");
        cv2.put(MovieContract.COLUMN_OVERVIEW, "Very Big Overview2");
        cv2.put(MovieContract.COLUMN_RATING,9.0);
        cv2.put(MovieContract.COLUMN_POSTER,"http://a.org/b.jpg");
        cv2.put(MovieContract.COLUMN_RELEASE_DATE, 2009219);

        ContentValues[] cvArr = {cv2,cv};

        int rowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.CONTENT_URI,cvArr);
        assertEquals("The number of rows inserted was wrong",2,rowsInserted);

    }
}
