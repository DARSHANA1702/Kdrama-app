package com.example.amahan.movieapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> movieAdapter;

    public MainActivityFragment() {
    }

    ArrayList<String> urls = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        urls.add("http://i.imgur.com/Uxptyb8.png");
        urls.add("http://i.imgur.com/DvpvklR.png");

        ArrayAdapter<String> mForecastAdapter;
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_movie, // The name of the layout ID.
                        R.id.list_item_movie_textview, // The ID of the textview to populate.
                        urls);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_Movie);
        gridView.setAdapter(new movieAdapter(getActivity()));
        //gridView.setAdapter(mForecastAdapter);
        return rootView;
    }


    public class movieAdapter extends BaseAdapter
    {
        private Context context;

        public movieAdapter(Context c)
        {
            context = c;
        }

        public int getCount() {
            return urls.size();
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return urls.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
       @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
            }else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(context).load(urls.get(position)).into(imageView);
            return imageView;
        }



    }
}
