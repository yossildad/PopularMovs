package com.eldad.yossi.popularmovs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
        fetchMovieTask.execute(getResources().getString(R.string.sort_rated),"1");

        return inflater.inflate(R.layout.fragment_main, container, false);

    }
}
