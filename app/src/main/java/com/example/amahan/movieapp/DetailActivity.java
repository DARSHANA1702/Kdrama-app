package com.example.amahan.movieapp;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by amahan on 11/3/2015.
 */
public class DetailActivity extends FragmentActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();

        }
    }

    public static class DetailFragment extends Fragment {

        private DBHelper mydb;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mydb = new DBHelper(getActivity());

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            int imageID;
            String imageURL;
            String dramaName;
            String synopsis;
            int dramadate;

            // get intent data
            Intent i = getActivity().getIntent();
            // Selected image id
            imageID = i.getExtras().getInt("Id");
            imageURL = mydb.getImage(imageID);
            dramaName = mydb.getName(imageID);
            dramadate = mydb.getDate(imageID);
            synopsis = mydb.getSynopsis(imageID);


            ImageView imageView;
            imageView = (ImageView) rootView.findViewById(R.id.detail_view);

            TextView titleTextView;
            titleTextView = (TextView) rootView.findViewById(R.id.dramaName);
            titleTextView.setText(dramaName);

            TextView dateTextView;
            dateTextView = (TextView) rootView.findViewById(R.id.dramadate);
            dateTextView.setText("Date aired: " + Integer.toString(dramadate));

            TextView synopsisTextView;
            synopsisTextView = (TextView) rootView.findViewById(R.id.synopsis);
            synopsisTextView.setText(synopsis);

            Picasso.with(getActivity()).load(imageURL).into(imageView);

            return rootView;
        }

    }

}
