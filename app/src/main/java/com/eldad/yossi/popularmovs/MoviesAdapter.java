package com.eldad.yossi.popularmovs;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Yossi on 18/10/2015.
 */
public class MoviesAdapter extends CursorAdapter {

    public  MoviesAdapter(Context context, Cursor c, int flags) {super(context, c, flags);}

    //defining the cols order so the cursor can be accessed correctly
    static final int COL_ID = MovieContract.COL_ID;
    static final int COL_TITLE = MovieContract.COL_TITLE;
    static final int COL_OVERVIEW = MovieContract.COL_OVERVIEW;
    static final int COL_POSTER = MovieContract.COL_POSTER;
    static final int COL_RATING = MovieContract.COL_RATING;
    static final int COL_RELEASE = MovieContract.COL_RELEASE;
    static final int COL_IMDBID = MovieContract.COL_IMDBID;


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_mov,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = new ViewHolder(view);
        String decodeUrl = null;
        TextView textView = viewHolder.textView;
        textView.setText(cursor.getString(MovieContract.COL_TITLE));

        ImageView imageView = viewHolder.imageView;
        imageView.setAdjustViewBounds(true);

        try {
            decodeUrl = URLDecoder.decode(cursor.getString(MovieContract.COL_POSTER), "UTF-8");
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
        public final TextView textView;
        public final ImageView imageView;

        public ViewHolder(View view){
          textView = (TextView)view.findViewById(R.id.item_mov_title);
          imageView= (ImageView)view.findViewById(R.id.item_mov_image);
        }
    }
}
