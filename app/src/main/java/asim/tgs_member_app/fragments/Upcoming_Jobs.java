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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.UpcomingJobAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MOCustomerProfile;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class Upcoming_Jobs extends Fragment implements Observer
{
    private ListView suggested_jobs_list;
    private List<SuggestedJobObject> list_data;
    private UpcomingJobAdapter adapter;
    private SharedPreferences settings;
    private String key;
    private String member_id,lat,lon;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        adapter = new UpcomingJobAdapter(list_data,getContext());
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



    @Override
    public void onResume() {
        super.onResume();
       /* if (UtilsManager.shouldRefresh) {
            populateList(member_id, lat, lon);
            UtilsManager.shouldRefresh = false;
        }*/
        Log.e("onresume ","called");
    }

    private ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void populateList(String member_id,String lat,String lon) {

        asyncHttpClient.setConnectTimeout(40000);
        String url = Constants.Host_Address + "members/my_jobs/"+member_id+"/"+key;
        Log.e("suggested jobs",Constants.Host_Address + "members/my_jobs/"+member_id+"/"+key);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/my_jobs/"+member_id+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
              /*  progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();*/
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
                    Toast.makeText(getContext(),"No jobs accepted yet",Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);

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

            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
            String current_order = settings.getString(Constants.ORDER_ID,"0");

            if (!object.getString("message").equalsIgnoreCase("Invalid Key")) {
                JSONArray data = object.getJSONArray("data");
                if (data != null) {

                    if (list_data==null)
                        list_data = new ArrayList<>();
                    else
                        list_data.clear();

                    String am_pm,cust_name,cust_img;
                    String order_id,order_item_id,meet_location,destination,datetime_ordered,meet_datetime,order_total,job_status,status_id
                            ,total_distance,status,instructions,no_of_hours,member_share,booking_type,customer_id,datetime_accepted;
                   String server_time;
                    for (int i=0;i<data.length();i++)
                    {
                        SuggestedJobObject suggestedJobObject = new SuggestedJobObject();
                        JSONObject obj_json = data.getJSONObject(i);
                        order_id =  obj_json.getString("order_id");
                        meet_location =  obj_json.getString("meet_location");
                        datetime_ordered =  obj_json.getString("datetime_ordered");
                        meet_datetime =  obj_json.getString("meet_datetime");
                       // order_total =  obj_json.getString("order_total");
                       // total_distance =  obj_json.getString("total_distance");
                      //  status =  obj_json.getString("status");
                        instructions =  obj_json.getString("instructions");
                        destination =  obj_json.getString("destination");
                        datetime_accepted = obj_json.getString("datetime_accepted");
                        no_of_hours =  obj_json.getString("no_of_hours");
                        member_share =  obj_json.getString("member_share");
                      //  order_item_id =  obj_json.getString("order_item_id");
                        booking_type =  obj_json.getString("booking_type");
                        customer_id =  obj_json.getString("customer_id");
                        server_time =  obj_json.getString("server_time");
                        cust_img =  obj_json.getString("customer_img");
                        cust_name =  obj_json.getString("customer_name");
                        job_status = obj_json.getString("job_status");
                        status_id = obj_json.getString("status");

                        suggestedJobObject.setOrder_id(order_id);
                        suggestedJobObject.setMeet_loc(meet_location);
                        suggestedJobObject.setDatetime_meet(meet_datetime);
                        suggestedJobObject.setDatetime_ordered(datetime_ordered);
                        suggestedJobObject.setDatetime_accepted(datetime_accepted);
                       // suggestedJobObject.setOrder_total(order_total);
                       // suggestedJobObject.setTotal_distance(total_distance);
                       // suggestedJobObject.setStatus(status);
                        suggestedJobObject.setInstructions(instructions);
                        suggestedJobObject.setDestination(destination);
                        suggestedJobObject.setNo_of_hours(no_of_hours);
                        suggestedJobObject.setMember_share(member_share);
                      //  suggestedJobObject.setOrder_item_id(order_item_id);
                        suggestedJobObject.setCustomer_id(customer_id);
                        suggestedJobObject.setBooking_type(booking_type);
                        suggestedJobObject.setServer_time(server_time);
                        suggestedJobObject.setCustomer_image(cust_img);
                        suggestedJobObject.setCustomer_name(cust_name);
                        suggestedJobObject.setStatus_id(status_id);
                        suggestedJobObject.setJob_status(job_status);
/*
                        if (suggestedJobObject.getStatus().equalsIgnoreCase("pending"))
                            suggestedJobObject.setShow_options(true);
                        else
                            suggestedJobObject.setShow_options(false);


                        if (i==0) {
                            suggestedJobObject.setDatetime_meet("2018-04-20 13:23:20");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==1) {
                            suggestedJobObject.setDatetime_meet("2018-04-15 20:29:40");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==2) {
                            suggestedJobObject.setDatetime_meet("2018-04-02 18:05:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==3) {
                            suggestedJobObject.setDatetime_meet("2018-04-02 21:08:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }*/
                       /* if (i==0) {
                            suggestedJobObject.setDatetime_meet("2018-04-20 13:23:20");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==1) {
                            suggestedJobObject.setDatetime_meet("2018-04-15 20:29:40");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==2) {
                            suggestedJobObject.setDatetime_meet("2018-04-03 19:15:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==3) {
                            suggestedJobObject.setDatetime_meet("2018-04-02 21:08:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }*/

                       if (!booking_type.equalsIgnoreCase("") && booking_type.equalsIgnoreCase("Scheduled"))
                       {
                           try {
                               if (meet_datetime.contains("AM"))
                                   am_pm = "AM";
                               else
                                   am_pm = "PM";

                              meet_datetime =  meet_datetime.replace(" AM","").replace(" PM","");

                               SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm");
                               Date date = dateFormat.parse(meet_datetime);
                               dateFormat = new SimpleDateFormat("dd-MM-yyyy h:mm");
                               String formmated_date = dateFormat.format(date);
                               suggestedJobObject.setDatetime_meet(formmated_date+am_pm);
                           }
                           catch (Exception e)
                           {
                               e.printStackTrace();
                           }
                       }

                       if (!current_order.equalsIgnoreCase(suggestedJobObject.getOrder_id()))
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


    @Override
    public void update(Observable o, Object arg) {
        populateList(member_id, lat, lon);
    }
}
