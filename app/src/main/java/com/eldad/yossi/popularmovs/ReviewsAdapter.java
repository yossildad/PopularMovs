package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

/**
 * Created by Tamar on 09/11/2015.
 */
public class ReviewsAdapter extends ArrayAdapter<String> {


    public ReviewsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        Log.v("rev", "adapter constructor ");
    }


        @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String reviewText =  getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item,parent,false);
        }

        TextView review = (TextView) convertView.findViewById(R.id.review_text);
        review.setText(reviewText);
        Log.v("rev", "adapter getView. reviewText is: " + reviewText );
        return convertView;
    }

    @Override
    public void addAll(Collection<? extends String> collection) {
        super.addAll(collection);
        Log.v("rev", "adapter addAll");
    }
}
