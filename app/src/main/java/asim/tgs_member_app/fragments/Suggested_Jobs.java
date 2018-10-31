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

import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.SuggestedJobAdapter;
import asim.tgs_member_app.adapters.UpcomingJobAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.Job_Accepted_Callback;
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
        suggestedJobAdapter.setCallBack(new Job_Accepted_Callback() {
            @Override
            public void onJobAccepted(String order_id) {
                createFirebaseNode(order_id);
            }
        });
        suggestedJobAdapter.setNotifier(this);
        suggested_jobs_list.setAdapter(suggestedJobAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum =0;
                populateList(member_id,lat,lon);
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                pageNum =0;
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
        Log.e("suggested_jobs",Constants.Host_Address + "members/get_member_upcoming_orders/"+member_id+"/"+lat+"/"+lon+"/"+key+"/"+pageNum);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/get_member_upcoming_orders/"+member_id+"/"+lat+"/"+lon+"/"+key+"/"+pageNum, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

              String responseData ="";
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();


                    responseData = new String(responseBody,"UTF-8");
                    String end_point =responseData.substring(responseData.indexOf("}")+1,responseData.length());
                    Log.e("response success",end_point);
                    JSONObject object = null;
                    try {

                        object = new JSONObject(end_point);
                        parseResult(object);
                        swipeRefreshLayout.setRefreshing(false);


                    } catch (JSONException e1) {

                        // end_point =responseData.substring(responseData.indexOf("}")+1,responseData.length());
                        Log.e("response success",end_point);
                         object = null;
                        try {
                            object = new JSONObject(responseData);
                        } catch (JSONException e2) {
                            e1.printStackTrace();
                        }
                        parseResult(object);
                        swipeRefreshLayout.setRefreshing(false);
                    }

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

                    if (list_data==null)
                        list_data = new ArrayList<>();
                    else
                        list_data.clear();


                    String am_pm="";
                    String order_id,meet_location,destination,datetime_ordered,meet_datetime,order_total
                            ,server_time,uniform
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
                        uniform = obj_json.getString("uniform");

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
                        suggestedJobObject.setUniform(uniform);

                        JSONArray order_details = obj_json.getJSONArray("order_details");

                        String service_id,service_type_id;
                        String service_name = "";
                        ArrayList<Order_Service_Info> order_service_infos = new ArrayList<Order_Service_Info>();
                        for (int j=0;j<order_details.length();j++)
                        {
                            JSONObject obj = order_details.getJSONObject(j);


                            service_id = obj.getString("service_id");
                            service_type_id = obj.getString("service_type_id");
                            service_name = obj.getString("service_type_name");

                            Order_Service_Info order_service_info = new Order_Service_Info();
                            order_service_info.setService_id(service_id);
                            order_service_info.setService_type_id(service_type_id);
                            order_service_info.setService_name(service_name);

                            boolean added = false;
                            for (int k=0;k<order_service_infos.size();k++)
                            {
                                if (order_service_infos.get(k).getService_name().equalsIgnoreCase(service_name))
                                {
                                    added = true;
                                    break;
                                }
                            }

                            if (!added) {
                                order_service_infos.add(order_service_info);
                            }


                        }

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



                        suggestedJobObject.setService_list(order_service_infos);
                        list_data.add(suggestedJobObject);
                        calculatejobpostedTime(list_data.size()-1,suggestedJobObject.getDatetime_ordered(),suggestedJobObject.getServer_time());
                    }
                    //Notify Data Adapter
                    suggestedJobAdapter = new SuggestedJobAdapter(list_data,getContext());
                    suggestedJobAdapter.setCallBack(new Job_Accepted_Callback() {
                        @Override
                        public void onJobAccepted(String order_id) {
                            createFirebaseNode(order_id);
                        }
                    });
                    suggestedJobAdapter.setNotifier(this);
                    suggested_jobs_list.setAdapter(suggestedJobAdapter);
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

    FirebaseDatabase firebaseDatabase;
    private void createFirebaseNode(String order_id)
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        String mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        String mem_id = settings.getString(Constants.PREFS_USER_ID, "");
        String current_job = settings.getString(Constants.CURRENT_JOB,"1");
        String lat = settings.getString(Constants.PREFS_USER_LAT,"");
        String lon = settings.getString(Constants.PREFS_USER_LNG,"");

        settings.edit().putString(Constants.CURRENT_JOB,order_id).apply();
        final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lon + "");
        member.setCurrent_job(order_id);

        String key = mem_id + "_member";
        if (!mem_id.equalsIgnoreCase(""))
            firebaseDatabase.getReference().child("members").child(key).setValue(member);
    }






    private void calculatejobpostedTime(final int position,String posted_date,String server_time)
    {
        try {
            // String dateString = start_date;
            // SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);
            posted_date = posted_date.replace(" AM","").replace(" PM","");
            server_time = server_time.replace(" AM","").replace(" PM","");
            posted_date = posted_date.replace("/","-")+":03";
            final Date convertedDate = new SimpleDateFormat("dd-MM-yyyy h:mm:ss").parse(posted_date);
            // convertedDate = dateFormat.parse(dateString);
            Calendar old_cal = Calendar.getInstance();
            old_cal.setTime(convertedDate);
            int hrs = old_cal.get(Calendar.HOUR_OF_DAY);
            int min = old_cal.get(Calendar.MINUTE);
            int sec = old_cal.get(Calendar.SECOND);

            int month = old_cal.get(Calendar.MONTH);
            month++;
            int day = old_cal.get(Calendar.DAY_OF_MONTH);

          /*  SimpleDateFormat dateFormat_server = new SimpleDateFormat(current_date);
            final Date convertedDate_server = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(current_date);
*/
            Calendar calender = Calendar.getInstance();
            final Date server_time_current = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(server_time);
            calender.setTime(server_time_current);

            int current_month = calender.get(Calendar.MONTH);
            int current_day = calender.get(Calendar.DAY_OF_MONTH);

            current_month++;
            if (month==current_month)
            {
                if (current_day==day)
                {
                    int current_hrs = calender.get(Calendar.HOUR_OF_DAY);
                    int current_min = calender.get(Calendar.MINUTE);
                    int current_sec = calender.get(Calendar.SECOND);

                    if (current_hrs>=12)
                        current_hrs = current_hrs - 12;

                    if (hrs>=12)
                        hrs = hrs - 12;



                    int rem_hrs = current_hrs - hrs;
                    int rem_minutes = current_min - min;
                    int rem_seconds = current_sec - sec;

                    if (rem_minutes<0)
                    {
                        int tem_min = rem_hrs * 60;
                        tem_min = tem_min - (rem_minutes*-1);
                        rem_hrs = tem_min/60;
                        rem_minutes = tem_min%60;
                    }
                    if (rem_seconds<0)
                    {
                        int tem_sec = rem_minutes * 60;
                        rem_minutes = tem_sec /60;
                        rem_seconds = tem_sec%60;
                    }

                    String calcuated_time = getContext().getResources().getString(R.string.calculating);

                    if (rem_hrs<10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+"0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs<10 && rem_minutes>=10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time = rem_hrs+":0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes>=10 && rem_seconds<10)
                        calcuated_time = rem_hrs+":"+rem_minutes+":0"+rem_seconds;

                    if (rem_hrs>0)
                        list_data.get(position).setJob_posted_time(rem_hrs+" "+getContext().getResources().getString(R.string.hours)+" "+getContext().getResources().getString(R.string.ago)+"");
                    else if (rem_hrs==0)
                        list_data.get(position).setJob_posted_time(rem_minutes+" "+getContext().getResources().getString(R.string.mins)+" "+getContext().getResources().getString(R.string.ago)+"");
                    else if (rem_minutes==0)
                        list_data.get(position).setJob_posted_time(rem_seconds+" "+getContext().getResources().getString(R.string.seconds)+" "+getContext().getResources().getString(R.string.ago)+"");

                    if (rem_hrs==0 && rem_minutes==0)
                        list_data.get(position).setJob_posted_time(rem_seconds+" "+getContext().getResources().getString(R.string.seconds)+" "+getContext().getResources().getString(R.string.ago)+"");




                }
                else if (current_day-day==1)
                {
                    //timer_text.setText(day-current_day+" days");
                    list_data.get(position).setJob_posted_time(getContext().getResources().getString(R.string.yesterday));

                }
                else if (day!=0)
                {
                    //timer_text.setText(day-current_day+" days");
                    list_data.get(position).setJob_posted_time(current_day-day+" "+getContext().getResources().getString(R.string.day)+" "+getContext().getResources().getString(R.string.ago)+"");

                }
                else if (day==0)
                {

                }
            }
            else
            {
                list_data.get(position).setJob_posted_time("1 "+getContext().getResources().getString(R.string.month)+" "+getContext().getResources().getString(R.string.ago)+"");

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
