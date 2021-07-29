package asim.tgs_member_app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

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

import androidx.fragment.app.Fragment;
import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.ImmediateJobAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class Immediate_Jobs_Frame extends Fragment
{
    private ListView suggested_jobs_list;
    private List<SuggestedJobObject> list_data;
    private ImmediateJobAdapter adapter;
    private SharedPreferences settings;
    private String key;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View rootview = inflater.inflate(R.layout.suggested_jobs_layout,null);
         suggested_jobs_list = (ListView) rootview.findViewById(R.id.suggested_jobs_list);
        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);


        String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
        String lat = settings.getString(Constants.PREFS_USER_LAT,"0");
        String lon = settings.getString(Constants.PREFS_USER_LNG,"0");
        key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");

        populateList(member_id,lat,lon);
        return rootview;
    }

    private ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void populateList(String member_id,String lat,String lon) {
        list_data = new ArrayList<>();
        adapter = new ImmediateJobAdapter(list_data,getContext());
        suggested_jobs_list.setAdapter(adapter);


        asyncHttpClient.setConnectTimeout(40000);
        String url = Constants.Host_Address + "members/get_member_new_orders/"+member_id+"/"+lat+"/"+lon+"/"+key;
        Log.e("suggested jobs",Constants.Host_Address + "members/get_member_new_orders/"+member_id+"/"+lat+"/"+lon+"/"+key);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/get_member_new_orders/"+member_id+"/"+lat+"/"+lon+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response success",responseData);
                    JSONObject object = new JSONObject(responseData);
                    parseResult(object);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response failure",responseData);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
                    String order_id,meet_location,destination,datetime_ordered,meet_datetime,order_total
                            ,total_distance,status,instructions;
                    for (int i=0;i<data.length();i++)
                    {
                        SuggestedJobObject suggestedJobObject = new SuggestedJobObject();
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

                        suggestedJobObject.setOrder_id(order_id);
                        suggestedJobObject.setMeet_loc(meet_location);
                        suggestedJobObject.setDatetime_meet(meet_datetime);
                        suggestedJobObject.setDatetime_ordered(datetime_ordered);
                        suggestedJobObject.setOrder_total(order_total);
                        suggestedJobObject.setTotal_distance(total_distance);
                        suggestedJobObject.setStatus(status);
                        suggestedJobObject.setInstructions(instructions);
                        suggestedJobObject.setDestination(destination);

                        list_data.add(suggestedJobObject);
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
