package com.eldad.yossi.popularmovs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tamar on 09/11/2015.
 */
public class ReviewsFragment extends Fragment implements FetchReviewsTask.ReviewsCallback {
    private ArrayList<String> mReviewsList = null;
    private ReviewsAdapter mReviewsAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reviews_fragment, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
        mReviewsList = new ArrayList<String>();
        mReviewsAdapter = new ReviewsAdapter(getContext(),R.layout.review_item,mReviewsList);
        ListView ls = (ListView) rootView.findViewById(R.id.reviews_listview);
        ls.setAdapter(mReviewsAdapter);
        Log.v("rev","fragment on create view");
        return rootView;
    }

    @Override
    public void OnReviewsLoadFinished(String[] reviews) {

       if (reviews != null) {
           mReviewsList = new ArrayList<String>(Arrays.asList(reviews));
           mReviewsAdapter.clear();
           mReviewsAdapter.addAll(mReviewsList);
           mReviewsAdapter.notifyDataSetChanged();
           Log.v("rev", "fragment onreview load finished inside if. arraylist length is: " + mReviewsList.size());
           Log.v("rev", "fragment onreview load finished inside if. adapter count is: " + mReviewsAdapter.getCount());
       }
        Log.v("rev", "fragment onreview load finished outsude if");
    }


}
