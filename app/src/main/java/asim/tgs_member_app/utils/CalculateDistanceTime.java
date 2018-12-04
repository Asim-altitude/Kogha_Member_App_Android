package asim.tgs_member_app.utils;

/**
 * Created by PC-GetRanked on 6/26/2018.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import asim.tgs_member_app.R;

public class CalculateDistanceTime {

    private taskCompleteListener mTaskListener;
    private Context mContext;


    public CalculateDistanceTime(Context context) {
        mContext = context;
    }

    public void setLoadListener(taskCompleteListener taskListener) {
        mTaskListener = taskListener;
    }

    int mem_id_index;
    public void setMemberId(int id)
    {
        this.mem_id_index = id;
    }


    public void getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";
        String API_KEY = mContext.getResources().getString(R.string.DIRECTION_API_KEY);
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key="+API_KEY;


        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API

        downloadTask.execute(url);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public interface taskCompleteListener {
        void taskCompleted(String[] time_distance, int id);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DistanceTimeParser parser = new DistanceTimeParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String distance = "";
            String duration_distance = "";

            try {

                String[] date_dist = new String[2];
                if (result.size() < 1) {
                    Log.e("Error : ", "No Points found");
                    date_dist[0] = "Away";
                    date_dist[1] = "Away";
                    mTaskListener.taskCompleted(date_dist, mem_id_index);
                    return;
                }


                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {

                    // Fetching i-th route
                    HashMap<String, String> tmpData = result.get(i);
                    Set<String> key = tmpData.keySet();
                    Iterator it = key.iterator();
                    while (it.hasNext()) {
                        String hmKey = (String) it.next();
                        duration_distance = tmpData.get(hmKey);

                        System.out.println("Key: " + hmKey + " & Data: " + duration_distance);

                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    date_dist[i] = duration_distance;
                }

                mTaskListener.taskCompleted(date_dist, mem_id_index);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
