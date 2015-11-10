package com.example.amahan.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private StaggeredGridLayoutManager stagGridLayout;

    private ArrayAdapter<String> movieAdapter;
    private ArrayAdapter<String> mForecastAdapter;
    GridView gridView;
    RecyclerView recyclerView;
    DramaRecyclerViewAdapter rcAdapter;
    DBHelper mydb;
    ArrayList<String> dramaImages;
    LinkedHashMap dramadata = new LinkedHashMap();


    String sort = "all";
    String genre = "all";


    public MainActivityFragment() {
    }

    ArrayList<String> urls = new ArrayList<>();

    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);
        savedState.putSerializable("myKey", dramadata);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mydb = new DBHelper(getActivity());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        dramaImages = new ArrayList<>(mydb.getAllDramaImages());
        if (savedInstanceState != null) {
            LinkedHashMap values = (LinkedHashMap)savedInstanceState.getSerializable("myKey");
            if (values != null) {
                dramadata = values;
            }
        }
        else
        {
            FetchDBTask dbTask = new FetchDBTask();
            dbTask.execute("all", "all");
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
            mydb.destroy();
            dramaTask.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        FetchDBTask dbTask = new FetchDBTask();
        if (id == R.id.action_refresh) {
            mydb.destroy();
            FetchDramaTask dramaTask  = new FetchDramaTask();
            dramaTask.execute();
            return true;
        }
        if (id == R.id.menuSortNewest) {
            sort = "date";
            //return true;
        }
        if (id == R.id.menuSortName){
            sort = "name";
           // return true;
        }
        if (id == R.id.menuSortRating){
            sort = "rating";
            // return true;
        }
        if (id == R.id.menuGenreAll){
            genre = "all";
            // return true;
        }
        if (id == R.id.menuGenreAction){
            genre = "action";
            // return true;
        }
        if (id == R.id.menuGenreComedy){
            genre = "comedy";
            // return true;
        }
        if (id == R.id.menuGenreFamily){
            genre = "family";
            // return true;
        }
        if (id == R.id.menuGenreRomance){
            genre = "romance";
            // return true;
        }

        dbTask.execute(genre,sort);
        rcAdapter.updateResults(dramadata);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rcAdapter = new DramaRecyclerViewAdapter(getActivity(), dramadata);
        View rootView = inflater.inflate(R.layout.temp_activity_main, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        stagGridLayout = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(stagGridLayout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rcAdapter);

        return rootView;

    }

    public class DramaRecyclerViewAdapter  extends RecyclerView.Adapter<DramaViewHolders> {
        private Context context;
        private LinkedHashMap dramaData;


        public DramaRecyclerViewAdapter(Context context, LinkedHashMap dramaData) {
            this.dramaData = dramaData;
            this.context = context;
        }

        public void updateResults(LinkedHashMap results) {
            this.dramaData = results;
            notifyDataSetChanged();
        }

        public String[] getNames()
        {
            String[] dramaNames = new String[this.dramaData.size()];
            Set set = dramaData.entrySet();
            Iterator i = set.iterator();
            int count = 0;
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                dramaNames[count] = (String)me.getKey();
                count++;
            }
            return dramaNames;
        }

        public String[] getImages()
        {
            String[] dramaImages = new String[this.dramaData.size()];
            Set set = dramaData.entrySet();
            Iterator i = set.iterator();
            int count = 0;
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                dramaImages[count] = (String)(((LinkedHashMap)me.getValue()).get("image"));
                count++;
            }
            return dramaImages;
        }

        public int[] getIds()
        {
            int[] dramaIds = new int[this.dramaData.size()];
            Set set = dramaData.entrySet();
            Iterator i = set.iterator();
            int count = 0;
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                dramaIds[count] = (int)(((LinkedHashMap)me.getValue()).get("id"));
                count++;
            }
            return dramaIds;
        }

        @Override
        public DramaViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drama_list, null);
            DramaViewHolders rcv = new DramaViewHolders(layoutView);
            return rcv;
        }
        @Override
        public void onBindViewHolder(DramaViewHolders holder, int position) {
            holder.countryName.setText(getNames()[position]);
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .build();

            imageLoader.displayImage(getImages()[position], holder.countryPhoto,options);
            //Picasso.with(context).load(getImages()[position]).into(holder.countryPhoto);
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
            int[] ids = rcAdapter.getIds();
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

    public class FetchDramaTask extends AsyncTask<String, Void, LinkedHashMap> {

        private final String LOG_TAG = FetchDramaTask.class.getSimpleName();

        private LinkedHashMap getDramaDataFromJson(String dramaJsonStr) throws JSONException {

            final String D_DATA = "data";
            final String D_GENRE = "genre";
            final String D_CAST = "cast";
            final String D_SYNOPSIS = "synopsis";
            final String D_IMAGE = "image";
            final String D_DATE = "date";
            final String D_NAME = "name";
            final String D_RATING = "rating";

            dramaJsonStr = dramaJsonStr + "}";
            dramaJsonStr = "{data: " + dramaJsonStr;
            JSONObject dramaJson = new JSONObject(dramaJsonStr);
            JSONArray dramaArray = dramaJson.getJSONArray(D_DATA);
            LinkedHashMap drama = new LinkedHashMap();

            try{
                for(int i = 0; i < dramaArray.length(); i++) {

                    int date;
                    String synopsis;
                    String image;
                    String name;
                    int rating;
                    JSONArray genre;
                    JSONArray cast;
                    String[] genreList;
                    String[] castList;

                    JSONObject dramaEntry = dramaArray.getJSONObject(i);

                    LinkedHashMap dramaInfo = new LinkedHashMap();

                    name = dramaEntry.getString(D_NAME);
                    date = dramaEntry.getInt(D_DATE);
                    synopsis = dramaEntry.getString(D_SYNOPSIS);
                    image = dramaEntry.getString(D_IMAGE);
                    rating = dramaEntry.getInt(D_RATING);

                    dramaInfo.put("id",i);
                    dramaInfo.put("image",image);
                    drama.put(name,dramaInfo);

                    genre = dramaEntry.getJSONArray(D_GENRE);
                    genreList = new String[genre.length()];
                    for(int ii = 0; ii <genre.length(); ii++){
                        boolean gen = mydb.insertGenre(i+ 1,genre.getString(ii).replaceAll(",","").toLowerCase());
                    }

                    cast = dramaEntry.getJSONArray(D_CAST);
                    castList = new String[cast.length()];
                    for(int ii = 0; ii <cast.length(); ii++){
                        boolean cas = mydb.insertCast(i + 1, cast.getString(ii));
                    }

                    //add cast and genre later after I reformat JSON on the backend

                    //mydb.destroy();
                    boolean insert = mydb.insertDrama(name, synopsis,date,image,rating);

                }
            }
            catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return drama;
        }


        @Override
        protected  LinkedHashMap doInBackground(String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String dramaJsonStr = null;

            try {
                final String dramaBaseURL = "http://rvlvrocelot.pythonanywhere.com/drama/20151101";
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
        protected void onPostExecute(LinkedHashMap result) {
            if (result != null) {
               rcAdapter.updateResults(result);
               //TODO update adapter
            }
        }
    }

    public class FetchDBTask extends AsyncTask<String, Void, LinkedHashMap> {

        private final String LOG_TAG = FetchDBTask.class.getSimpleName();

        @Override
        protected  LinkedHashMap doInBackground(String... params){
            mydb = new DBHelper(getActivity());
            LinkedHashMap LHM;
            LHM = mydb.getAllDrama(params[0],params[1]);
            return  LHM;
        }

        @Override
        protected void onPostExecute(LinkedHashMap result) {
            if (result != null) {
                rcAdapter.updateResults(result);
                dramadata = result;
            }
        }
    }
}
