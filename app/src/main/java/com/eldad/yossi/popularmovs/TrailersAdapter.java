package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Tamar on 09/11/2015.
 */
public class TrailersAdapter extends ArrayAdapter<String> {

    public TrailersAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item,parent,false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.trailer_image_item);
        TextView textView = (TextView)convertView.findViewById(R.id.trailer_text_item);
        img.setImageResource(R.drawable.play_button);
        textView.setText("Watch trailer #" + (position+1));

        return convertView;
        //return super.getView(position, convertView, parent);
    }
}
