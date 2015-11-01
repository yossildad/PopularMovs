package com.eldad.yossi.popularmovs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {
    //saving the sort type in order to know when to reload the data on onResume
    public String mSort = null;

    @Override
    protected void onResume() {
        super.onResume();

        MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //if the sorting has changed then the main screen parameters should be reset
        if (!mSort.equals(sp.getString(getResources().getString(R.string.preference_file_key),""))) {

            mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));

            //going back to the first page and loading the data
            fragment.LoadPage(mSort,"1");

            //setting the scroll back to the start of the grid
            fragment.mScrollPosition = GridView.INVALID_POSITION;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting the sort type
        if (mSort == null)
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

       if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
