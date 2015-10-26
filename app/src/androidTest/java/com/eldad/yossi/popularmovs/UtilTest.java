package com.eldad.yossi.popularmovs;

import android.test.AndroidTestCase;

/**
 * Created by Tamar on 22/10/2015.
 */
public class UtilTest  extends AndroidTestCase{
    public void testJulianDate(){
        int julDate = Utility.toJulian("2015-10-22");
        assertEquals(2457318,julDate);
    }

}
