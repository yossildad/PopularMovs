package com.eldad.yossi.popularmovs;

import android.test.AndroidTestCase;

/**
 * Created by Tamar on 18/10/2015.
 */
public class FetchMovieTaskTest extends AndroidTestCase {


    public void testCreateImageURL()
    {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(mContext);
        String url = fetchMovieTask.CreatePosterUrl("a.jpg");

        assertEquals("No match","http://image.tmdb.org/t/p/w185/a.jpg",url );

    }
}
