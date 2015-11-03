package com.example.amahan.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> movieAdapter;
    private ArrayAdapter<String> mForecastAdapter;
    GridView gridView;

    public MainActivityFragment() {
    }

    ArrayList<String> urls = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchDramaTask dramaTask  = new FetchDramaTask();
            dramaTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FetchDramaTask dramaTask  = new FetchDramaTask();
        dramaTask.execute();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_Movie);
        gridView.setAdapter(new movieAdapter(getActivity(),new ArrayList<String>()));
        //gridView.setAdapter(mForecastAdapter);
        return rootView;
    }


    public class movieAdapter extends BaseAdapter
    {
        private Context context;
        private  ArrayList<String> dramaData;

        public movieAdapter(Context c, ArrayList<String> data)
        {
            context = c;
            dramaData = data;
        }

        public int getCount() {
            return dramaData.size();
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return dramaData.get(position);
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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            Picasso.with(context).load(dramaData.get(position)).into(imageView);
            return imageView;
        }
    }

    public class FetchDramaTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchDramaTask.class.getSimpleName();

        private String[] getDramaDataFromJson(String dramaJsonStr) throws JSONException {

            final String D_DATA = "data";
            final String D_GENRE = "genre";
            final String D_CAST = "cast";
            final String D_SYNOPSIS = "synopsis";
            final String D_IMAGE = "image";
            final String D_DATE = "date";

            dramaJsonStr = dramaJsonStr + "}";
            dramaJsonStr = "{data: " + dramaJsonStr;
            JSONObject dramaJson = new JSONObject(dramaJsonStr);
            JSONArray dramaArray = dramaJson.getJSONArray(D_DATA);
            String[] resultStrs = new String[dramaArray.length()];

            try{
                for(int i = 0; i < dramaArray.length(); i++) {

                    int date;
                    String synopsis;
                    String image;

                    JSONObject dramaEntry = dramaArray.getJSONObject(i);

                    date = dramaEntry.getInt(D_DATE);
                    synopsis = dramaEntry.getString(D_SYNOPSIS);
                    image = dramaEntry.getString(D_IMAGE);

                    //add cast and genre later after I reformat JSON on the backend

                    //temp
                    resultStrs[i] = image;
                }
            }
            catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return resultStrs;
        }


        @Override
        protected  String[] doInBackground(String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String dramaJsonStr = null;

            try {
                final String dramaBaseURL = "http://rvlvrocelot.pythonanywhere.com/drama/20150601";
                URL dramaURL = new URL(dramaBaseURL);
                urlConnection = (HttpURLConnection) dramaURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dramaJsonStr = buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getDramaDataFromJson(dramaJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                Log.v(LOG_TAG, result[0]);
                ArrayList<String> resultData = new ArrayList<String>(Arrays.asList(result));
                gridView.setAdapter(new movieAdapter(getActivity(),resultData));

                // New data is back from the server.  Hooray!
            }
        }

    }
}
