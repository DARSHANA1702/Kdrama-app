package com.example.amahan.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import android.widget.TextView;
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

    private StaggeredGridLayoutManager stagGridLayout;

    private ArrayAdapter<String> movieAdapter;
    private ArrayAdapter<String> mForecastAdapter;
    movieAdapter adapter;
    GridView gridView;
    RecyclerView recyclerView;
    DramaRecyclerViewAdapter rcAdapter;
    DBHelper mydb;
    ArrayList<String> dramaImages;


    public MainActivityFragment() {
    }

    ArrayList<String> urls = new ArrayList<>();

    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);
        savedState.putStringArrayList("myKey", dramaImages);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mydb = new DBHelper(getActivity());
        dramaImages = new ArrayList<>(mydb.getAllDramaImages());
        if (savedInstanceState != null) {
            ArrayList<String> values = savedInstanceState.getStringArrayList("myKey");
            if (values != null) {
                dramaImages = values;
            }
        }
        else
        {
            dramaImages = new ArrayList<>(mydb.getAllDramaImages());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchDramaTask dramaTask  = new FetchDramaTask();
        //only execute async task if the database is empty
        if (!mydb.checkDrama()){
            dramaTask.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mydb.destroy();
            FetchDramaTask dramaTask  = new FetchDramaTask();
            dramaTask.execute();
            return true;
        }
        if (id == R.id.menuSortNewest) {
            dramaImages= mydb.getAllDramaImagesDateSort();
            rcAdapter = new DramaRecyclerViewAdapter(getActivity(), dramaImages);
            stagGridLayout = new StaggeredGridLayoutManager(3,1);
            recyclerView.setLayoutManager(stagGridLayout);
            recyclerView.setAdapter(rcAdapter);
            rcAdapter.notifyDataSetChanged();

           // gridView.setAdapter(adapter);
           // adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.temp_activity_main, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        stagGridLayout = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(stagGridLayout);

        DramaRecyclerViewAdapter rcAdapter = new DramaRecyclerViewAdapter(getActivity(), dramaImages);
        recyclerView.setAdapter(rcAdapter);


        //TODO get off UI thread
        adapter = new movieAdapter(getActivity(),dramaImages);

//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        gridView = (GridView) rootView.findViewById(R.id.gridview_Movie);
//        gridView.setAdapter(adapter);
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//                int[] ids = adapter.getIds();
//                // Sending image id to FullScreenActivity
//                Intent i = new Intent(getActivity(), DetailActivity.class);
//                // passing array index
//
//                final String LOG_TAG = MainActivityFragment.class.getSimpleName();
//
//                for (int dd = 0; dd<ids.length; dd++){
//                    Log.v(LOG_TAG,dramaImages.get(dd));
//                    Log.v(LOG_TAG,Integer.toString(ids[dd]));
//                }
//                Log.v(LOG_TAG,Integer.toString(ids[0]));
//
//                i.putExtra("Id", ids[position]);
//                startActivity(i);
//            }
//        });

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

        public void updateResults(ArrayList<String> results) {
            dramaData = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        public int[] getIds()
        {
            return mydb.getIdByImages(dramaData);
        }

        //---returns the ID of an item---
        public String getItem(int position) {
            return dramaData.get(position);
        }

        private final String LOG_TAG = FetchDramaTask.class.getSimpleName();

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

    public class DramaRecyclerViewAdapter  extends RecyclerView.Adapter<DramaViewHolders> {
        private Context context;
        private ArrayList<String> dramaData;
        public DramaRecyclerViewAdapter(Context context, ArrayList<String> dramaData) {
            this.dramaData = dramaData;
            this.context = context;
        }
        @Override
        public DramaViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drama_list, null);
            DramaViewHolders rcv = new DramaViewHolders(layoutView);
            return rcv;
        }
        @Override
        public void onBindViewHolder(DramaViewHolders holder, int position) {
            holder.countryName.setText("DramaName");
            Picasso.with(context).load(dramaData.get(position)).into(holder.countryPhoto);
        }
        @Override
        public int getItemCount() {
            return this.dramaData.size();
        }
    }
    public class DramaViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView countryName;
        public ImageView countryPhoto;
        public DramaViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            countryName = (TextView) itemView.findViewById(R.id.country_name);
            countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
        }
        @Override
        public void onClick(View view) {
            int[] ids = adapter.getIds();
            // Sending image id to FullScreenActivity
            Intent i = new Intent(getActivity(), DetailActivity.class);
            // passing array index

            final String LOG_TAG = MainActivityFragment.class.getSimpleName();

            for (int dd = 0; dd<ids.length; dd++){
                Log.v(LOG_TAG,dramaImages.get(dd));
                Log.v(LOG_TAG,Integer.toString(ids[dd]));
            }
            Log.v(LOG_TAG,Integer.toString(ids[0]));

            i.putExtra("Id", ids[getPosition()]);
            startActivity(i);
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
            final String D_NAME = "name";

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
                    String name;

                    JSONObject dramaEntry = dramaArray.getJSONObject(i);

                    name = dramaEntry.getString(D_NAME);
                    date = dramaEntry.getInt(D_DATE);
                    synopsis = dramaEntry.getString(D_SYNOPSIS);
                    image = dramaEntry.getString(D_IMAGE);

                    //add cast and genre later after I reformat JSON on the backend

                    //mydb.destroy();
                    boolean insert = mydb.insertDrama(name,synopsis,date,image);

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
                adapter.updateResults(new ArrayList<String>(Arrays.asList(result)));

                // New data is back from the server.  Hooray!
            }
        }

    }
}
