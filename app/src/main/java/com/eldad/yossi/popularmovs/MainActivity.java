package com.eldad.yossi.popularmovs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public String mSort = null;

    @Override
    protected void onResume() {
        Log.v("POPMOVS","onResume start");
        super.onResume();
        MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSort.equals(sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular))))
        Log.v("POPMOVS","onResume before");
            fragment.LoadPage();
            fragment.mPage = 1;
        Log.v("POPMOVS", "onResume after");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("POPMOVS","onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        //clearing the cache when the app is destroyed
        getContentResolver().delete(MovieContract.CONTENT_URI, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSort = sp.getString(getResources().getString(R.string.preference_file_key),getResources().getString(R.string.sotr_popular));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
