package com.example.samhattangady.mymdb;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by samhattangady on 1/3/16.
 */
public class DisplayStructAdapter extends ArrayAdapter<DisplayStruct> {

    private ArrayList<DisplayStruct> objects;
    private int mResource;
    private int screenWidth;


    public DisplayStructAdapter(Context context, @LayoutRes int resource, int textViewResourceId, ArrayList<DisplayStruct> objects){
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
        this.mResource = resource;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        if (convertView == null) {
            view = inflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        DisplayStruct current = objects.get(position);

        int width = screenWidth/((int)(screenWidth/185.0));
        int height = (int)Math.round(width/.675);

        if (current != null) {

            ImageView image = (ImageView) view.findViewById(R.id.grid_display_image);

            if (image != null) {
                Picasso.with(getContext())
                        .load("http://image.tmdb.org/t/p/w185"+current.getPoster())
                        .resize(width, height)
                        .centerCrop()
                        .placeholder(R.mipmap.place_holder)
                        .resize(width, height)
                        .into(image);
            }
        }
        return view;
    }

    @Override
    public DisplayStruct getItem(int position) {
        return objects.get(position);
    }
}
