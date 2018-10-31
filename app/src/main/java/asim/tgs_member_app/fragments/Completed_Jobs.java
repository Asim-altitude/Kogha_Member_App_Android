package asim.tgs_member_app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.CompletedJobAdapter;
import asim.tgs_member_app.models.CompletedJobObject;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class Completed_Jobs extends Fragment
{
    private ListView suggested_jobs_list;
    private List<CompletedJobObject> list_data;
    private CompletedJobAdapter adapter;
    private SharedPreferences settings;
    private String key;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String member_id,lat,lon;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View rootview = inflater.inflate(R.layout.suggested_jobs_layout,null);
         suggested_jobs_list = (ListView) rootview.findViewById(R.id.suggested_jobs_list);
        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.upcoming_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.theme_primary));

         member_id = settings.getString(Constants.PREFS_USER_ID,"0");
         lat = settings.getString(Constants.PREFS_USER_LAT,"0");
         lon = settings.getString(Constants.PREFS_USER_LNG,"0");
        key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");
        list_data = new ArrayList<>();
        adapter = new CompletedJobAdapter(list_data,getContext());
        suggested_jobs_list.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateList(member_id,lat,lon);
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                populateList(member_id,lat,lon);
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        return rootview;
    }

    private ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void populateList(String member_id,String lat,String lon) {

        asyncHttpClient.setConnectTimeout(40000);
        Log.e("completed_jobs",Constants.Host_Address + "members/get_my_completed_jobs/"+member_id+"/"+key);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/get_my_completed_jobs/"+member_id+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            /*    progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
     */       }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response success",responseData);
                    JSONObject object = new JSONObject(responseData);
                    if (object.getString("status").equalsIgnoreCase("success"))
                         parseResult(object);

                    swipeRefreshLayout.setRefreshing(false);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response failure",responseData);

                    swipeRefreshLayout.setRefreshing(false);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void parseResult(JSONObject object) {

        try
        {
            if (!object.getString("message").equalsIgnoreCase("Invalid Key")) {
                JSONArray data = object.getJSONArray("data");
                if (data != null) {
                    list_data.clear();
                    String order_id,meet_location,destination,datetime_ordered,meet_datetime,order_total
                            ,total_distance,status,instructions,name,full_image,completed_datetime,booking_type,unifrom;
                    for (int i=0;i<data.length();i++)
                    {
                        CompletedJobObject completedJobObject = new CompletedJobObject();
                        JSONObject obj_json = data.getJSONObject(i);
                        order_id =  obj_json.getString("order_id");
                        meet_location =  obj_json.getString("meet_location");
                        datetime_ordered =  obj_json.getString("datetime_ordered");
                        meet_datetime =  obj_json.getString("meet_datetime");
                        order_total =  obj_json.getString("order_total");
                        total_distance =  obj_json.getString("total_distance");
                        status =  obj_json.getString("status");
                        instructions =  obj_json.getString("instructions");
                        destination =  obj_json.getString("destination");
                        name =  obj_json.getString("customer_name");
                        full_image =  obj_json.getString("customer_full_img");
                        completed_datetime = obj_json.getString("completed_datetime");
                        booking_type = obj_json.getString("booking_type");

                        if (obj_json.has("uniform"))
                            unifrom = obj_json.getString("uniform");
                        else
                            unifrom = "0";

                        completedJobObject.setOrder_id(order_id);
                        completedJobObject.setMeet_loc(meet_location);
                        completedJobObject.setDatetime_meet(meet_datetime);
                        completedJobObject.setDatetime_ordered(datetime_ordered);
                        completedJobObject.setOrder_total(order_total);
                        completedJobObject.setTotal_distance(total_distance);
                        completedJobObject.setStatus(status);
                        completedJobObject.setInstructions(instructions);
                        completedJobObject.setDestination(destination);
                        completedJobObject.setCustomer_name(name);
                        completedJobObject.setCustomer_image(full_image);
                        completedJobObject.setCompleted_Time(completed_datetime);
                        completedJobObject.setBooking_type(booking_type);
                        completedJobObject.setUniform_id(unifrom);

                        list_data.add(completedJobObject);
                    }
                    //Notify Data Adapter
                    adapter.notifyDataSetChanged();
                }
            }
            else
            {
                UtilsManager.showAlertMessage(getContext(),"","Invalid Key");
            }
        }
        catch (Exception e)
        {
            Log.e("error ",e.getMessage());
        }
    }

}
