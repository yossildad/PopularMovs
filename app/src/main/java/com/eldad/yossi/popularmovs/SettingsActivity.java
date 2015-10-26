package com.eldad.yossi.popularmovs;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Created by Tamar on 25/10/2015.
 */
public class SettingsActivity extends PreferenceActivity implements  Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences_general);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.v("POPMOVS", "Prefchange value is: " + newValue.toString());
        return true;
    }
}
