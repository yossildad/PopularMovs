package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Yossi on 18/10/2015.
 */
public class MoviesAdapter extends CursorAdapter {

    public  MoviesAdapter(Context context, Cursor c, int flags) {super(context, c, flags);}

    //defining the cols order so the cursor can be accessed correctly
    //it is localy defined since the projection for the movie list is different the the movie details
        static final int COL_ID = 0;
        static final int COL_POSTER = 1;



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_mov,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = new ViewHolder(view);
        String decodeUrl = null;
        ImageView imageView = viewHolder.imageView;
        imageView.setAdjustViewBounds(true);

        try {
            decodeUrl = URLDecoder.decode(cursor.getString(COL_POSTER), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("bind view", "unable to decode poster url");
        }
        Picasso.with(context).load(decodeUrl)
                .into(imageView);
        //imageView.(cursor.getString(COL_POSTER));
    }

    public class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view){
          imageView= (ImageView)view.findViewById(R.id.item_mov_image);
        }
    }
}
