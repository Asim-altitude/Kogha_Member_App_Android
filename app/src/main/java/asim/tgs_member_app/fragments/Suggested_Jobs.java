package asim.tgs_member_app.fragments;

import android.app.Activity;
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
import android.widget.AbsListView;
import android.widget.ListView;

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
import asim.tgs_member_app.adapters.SuggestedJobAdapter;
import asim.tgs_member_app.adapters.UpcomingJobAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.NotifyUpdates;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class Suggested_Jobs extends Fragment implements NotifyUpdates
{
    private ListView suggested_jobs_list;
    private List<SuggestedJobObject> list_data;
    private UpcomingJobAdapter adapter;
    private SuggestedJobAdapter suggestedJobAdapter;
    private SharedPreferences settings;
    private String key;
    private int pageNum =0;
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
        suggestedJobAdapter = new SuggestedJobAdapter(list_data,getContext());
        suggestedJobAdapter.setNotifier(this);
        suggested_jobs_list.setAdapter(suggestedJobAdapter);

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

        populateList(member_id,lat,lon);

        return rootview;
    }

    NotifyUpdates notifyUpdates;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            notifyUpdates = (NotifyUpdates) activity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void populateList(String member_id,String lat,String lon) {

        asyncHttpClient.setConnectTimeout(40000);
        Log.e("suggested jobs",Constants.Host_Address + "members/get_member_upcoming_orders/"+member_id+"/"+lat+"/"+lon+"/"+key+"/"+pageNum);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/get_member_upcoming_orders/"+member_id+"/"+lat+"/"+lon+"/"+key+"/"+pageNum, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
              /*  if (progressDialog!=null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();


                progressDialog = new ProgressDialog(getContext());
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
                    parseResult(object);
                    swipeRefreshLayout.setRefreshing(false);
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
                    swipeRefreshLayout.setRefreshing(false);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

       final String member_id_ = settings.getString(Constants.PREFS_USER_ID,"0");
       final String lat_ = settings.getString(Constants.PREFS_USER_LAT,"0");
       final String lon_ = settings.getString(Constants.PREFS_USER_LNG,"0");
        key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");


        suggested_jobs_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount)
                {
                    if(preLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        Log.d("Last", "bottom reached");
                        preLast = lastItem;
                        populateList(member_id_,lat_,lon_);
                    }
                }
            }
        });
    }

    private int preLast;
    private void parseResult(JSONObject object) {

        try
        {
            if (!object.getString("message").equalsIgnoreCase("Invalid Key")) {
                JSONArray data = object.getJSONArray("data");
                if (data != null) {
                    String am_pm="";
                    String order_id,meet_location,destination,datetime_ordered,meet_datetime,order_total
                            ,server_time
                            ,total_distance,status,instructions,no_of_hours,member_share,booking_type,customer_id,order_item_id;
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
                        no_of_hours =  obj_json.getString("no_of_hours");
                        member_share =  obj_json.getString("member_share");
                        order_item_id =  obj_json.getString("order_item_id");
                        booking_type =  obj_json.getString("booking_type");
                        customer_id =  obj_json.getString("customer_id");
                        server_time =  obj_json.getString("server_time");

                        suggestedJobObject.setOrder_id(order_id);
                        suggestedJobObject.setMeet_loc(meet_location);
                        suggestedJobObject.setDatetime_meet(meet_datetime);
                        suggestedJobObject.setDatetime_ordered(datetime_ordered);
                        suggestedJobObject.setOrder_total(order_total);
                        suggestedJobObject.setTotal_distance(total_distance);
                        suggestedJobObject.setStatus(status);
                        suggestedJobObject.setInstructions(instructions);
                        suggestedJobObject.setDestination(destination);
                        suggestedJobObject.setNo_of_hours(no_of_hours);
                        suggestedJobObject.setMember_share(member_share);
                        suggestedJobObject.setOrder_item_id(order_item_id);
                        suggestedJobObject.setCustomer_id(customer_id);
                        suggestedJobObject.setBooking_type(booking_type);
                        suggestedJobObject.setServer_time(server_time);

                    /*    if (suggestedJobObject.getStatus().equalsIgnoreCase("pending"))
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
                            suggestedJobObject.setDatetime_meet("2018-04-03 19:15:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
                        if (i==3) {
                            suggestedJobObject.setDatetime_meet("2018-04-02 21:08:50");
                            suggestedJobObject.setBooking_type("Scheduled");
                        }
*/
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

                        list_data.add(suggestedJobObject);
                    }
                    //Notify Data Adapter
                    suggestedJobAdapter.notifyDataSetChanged();
                    pageNum++;
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
    public void callToFragment() {
        notifyUpdates.onJobAccepted(111);
    }

    @Override
    public void onJobAccepted(int code) {

    }
}
