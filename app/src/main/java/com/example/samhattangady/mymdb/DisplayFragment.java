package com.example.samhattangady.mymdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by samhattangady on 1/2/16.
 */

public class DisplayFragment extends Fragment {

    DisplayStructAdapter mDisplayAdapter;

    public DisplayFragment(){
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    public void updateData() {
        DownloadMovieData downloadMovieData = new DownloadMovieData();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        downloadMovieData.execute(pref.getString(getString(R.string.type_key), getString(R.string.type_default)),
                pref.getString(getString(R.string.order_key), getString(R.string.order_default)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*String[] arr= {
            "Hi", "Bye", "Kadlekai"
        };

        ArrayList<String> ar = new ArrayList<String>(Arrays.asList(arr));

        DisplayStruct A = new DisplayStruct();
        DisplayStruct B = new DisplayStruct();
        A.setName("Hi");
        B.setName("Bye");

        DisplayStruct[] arr = {A,B};

        ArrayList<DisplayStruct> ar = new ArrayList<DisplayStruct>(Arrays.asList(arr));*/

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();

        // We dont want the images per row to be blown up
        // But we also want the full width to be filled with image
        // considering we are downloading images of 185p width, we calculate

        int coloumns = (int)(screenWidth/185.0);
        int coloumnWidth = screenWidth/coloumns;

        mDisplayAdapter = new DisplayStructAdapter(
                        getActivity(),
                        R.layout.grid_display_template,
                        R.id.grid_display_image,
                        //ar);
                        new ArrayList<DisplayStruct>());

        View rootView = inflater.inflate(R.layout.fragment_display, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.display_grid_view);
        gridView.setColumnWidth(coloumnWidth);
        gridView.setAdapter(mDisplayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getActivity();
                DisplayStruct displayStruct = mDisplayAdapter.getItem(position);
                Intent displayIntent = new Intent(context, DetailsActivity.class)
                        .putExtra("DisplayStruct", displayStruct);
                startActivity(displayIntent);
            }
        });

        return rootView;

    }

    public class DownloadMovieData extends AsyncTask<String, Void, DisplayStruct[]> {

        private DisplayStruct[] geDataFromJson(String displayJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_NAME = "name";
            final String OWM_TITLE = "title";
            final String OWM_OVERVIEW = "overview";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_BACKDROP_PATH = "backdrop_path";
            final String OWM_POPULARITY = "popularity";
            final String OWM_RATINGS = "vote_average";

            JSONObject displayJson = new JSONObject(displayJsonStr);
            JSONArray displayArray = displayJson.getJSONArray(OWM_RESULTS);

            DisplayStruct[] resultStrs = new DisplayStruct[displayArray.length()];
            for(int i = 0; i < displayArray.length(); i++) {

                // Get the JSON object representing the movie/show
                JSONObject displayData = displayArray.getJSONObject(i);

                resultStrs[i] = new DisplayStruct();

                resultStrs[i].name = displayData.optString(OWM_NAME);
                if (resultStrs[i].name == null){
                    resultStrs[i].name = displayData.getString(OWM_TITLE);
                }
                resultStrs[i].overview = displayData.getString(OWM_OVERVIEW);
                resultStrs[i].ratings = displayData.getString(OWM_RATINGS);
                resultStrs[i].poster = displayData.getString(OWM_POSTER_PATH);
                resultStrs[i].backdrop = displayData.getString(OWM_BACKDROP_PATH);
                resultStrs[i].popularity = displayData.getString(OWM_POPULARITY);
            }

            return resultStrs;
        }

        @Override
        protected void onPostExecute(DisplayStruct[] result) {
            if (result != null) {
                mDisplayAdapter.clear();
                String[] retArray = new String[result.length];
                for (int i=0; i<result.length; i++) {
                    retArray[i] = result[i].name;
                }
                mDisplayAdapter.addAll(result);
            }
        }

        @Override
        protected DisplayStruct[] doInBackground(String ... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String displayJsonStr = null;
            String typePath = params[0];
            String sortByQuery = params[1];
            String apiQuery = "6f6d4a9d8af691700a5b158604218e05";

            try {

                String baseurl = "http://api.themoviedb.org/3/discover";
                Uri.Builder builder = Uri.parse(baseurl).buildUpon()
                        .appendPath(typePath)
                        .appendQueryParameter("api_key", apiQuery)
                        .appendQueryParameter("sort_by", sortByQuery);

                String urlString = builder.build().toString();
                    URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();;

                // Read the input stream into a String
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
                displayJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("DownloadMovieData","Error-",e);
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DownloadMovieData", "Error closing stream", e);
                    }
                }
            }

            if (displayJsonStr != null) {
                try {
                    return (geDataFromJson(displayJsonStr));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.e("DoInBackground","JsonStr is null, Download failed.");
            }
            return null;
        }
    }
}