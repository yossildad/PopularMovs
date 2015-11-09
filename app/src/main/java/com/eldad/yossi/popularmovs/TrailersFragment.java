package com.eldad.yossi.popularmovs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Yossi on 09/11/2015.
 */
public class TrailersFragment extends Fragment implements FetchTrailersTask.TrailersCallback{

    private TrailersAdapter mAdapter = null;
    private String[] mKeys = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trailers_fragment, container, false);
        mAdapter = new TrailersAdapter(getContext(),R.layout.trailer_list_item);
        ListView lv = (ListView)view.findViewById(R.id.trailers_listview);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //launch the youtube content provider with the key from the member
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v="+mKeys[position]));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void OntrailersLoadFinished(String[] keys) {

        mKeys = keys;
       if (mKeys != null) {
           mAdapter.clear();
           mAdapter.addAll(mKeys);
           mAdapter.notifyDataSetChanged();
       }
    }
}


//private ArrayList<String> mReviewsList = null;
//private ReviewsAdapter mReviewsAdapter = null;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.reviews_fragment, container, false);
//        //return super.onCreateView(inflater, container, savedInstanceState);
//        mReviewsList = new ArrayList<String>();
//        mReviewsList = new ArrayList<String>(Arrays.asList(new String[]{"sdfsgtdfbdf", "sdfsfdsfd"}));
//        mReviewsAdapter = new ReviewsAdapter(getContext(),R.layout.review_item,mReviewsList);
//        ListView ls = (ListView) rootView.findViewById(R.id.reviews_listview);
//        ls.setAdapter(mReviewsAdapter);
//        Log.v("rev", "fragment on create view");
//        return rootView;
//    }
//
//    @Override
//    public void OnReviewsLoadFinished(String[] reviews) {
//
//        if (reviews != null) {
//            mReviewsList = new ArrayList<String>(Arrays.asList(reviews));
//            mReviewsAdapter.clear();
//            mReviewsAdapter.addAll(mReviewsList);
//            mReviewsAdapter.notifyDataSetChanged();
//            Log.v("rev", "fragment onreview load finished inside if. arraylist length is: " + mReviewsList.size());
//            Log.v("rev", "fragment onreview load finished inside if. adapter count is: " + mReviewsAdapter.getCount());
//        }
//        Log.v("rev", "fragment onreview load finished outsude if");
//    }