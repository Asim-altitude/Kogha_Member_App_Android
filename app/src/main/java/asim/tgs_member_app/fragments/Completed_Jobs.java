package asim.tgs_member_app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import asim.tgs_member_app.adapters.CompletedJobAdapter;
import asim.tgs_member_app.models.CompletedJobObject;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Order_Service_Info;
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
                    String order_id,meet_location,destination,datetime_ordered,meet_datetime,datetime_accepted,datetime_started,order_total
                             ,item_desc,delivery_person,doc_image,main_service_name,item_type,total_distance,status,instructions,name,member_share,full_image,completed_datetime,booking_type,unifrom,no_of_hrs;
                    for (int i=0;i<data.length();i++)
                    {
                        CompletedJobObject completedJobObject = new CompletedJobObject();
                        JSONObject obj_json = data.getJSONObject(i);
                        order_id =  obj_json.getString("order_id");
                        meet_location =  obj_json.getString("meet_location");
                        datetime_ordered =  obj_json.getString("datetime_ordered");
                        meet_datetime =  obj_json.getString("meet_datetime");
                        datetime_started = obj_json.getString("datetime_started");
                        datetime_accepted = obj_json.getString("datetime_accepted");
                        order_total =  obj_json.getString("order_total");
                        total_distance =  obj_json.getString("total_distance");
                        member_share =  obj_json.getString("member_share");
                        status =  obj_json.getString("status");

                        item_desc =  obj_json.getString("lalamove_description");
                        doc_image =  obj_json.getString("lalamove_image");
                        delivery_person =  obj_json.getString("lalamove_receiver_name");
                        item_type =  obj_json.getString("lalamove_type");


                        main_service_name = obj_json.getString("main_service_name");
                        no_of_hrs =  obj_json.getString("no_of_hours");
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



                        JSONArray order_details = obj_json.getJSONArray("order_details");

                        String service_id,service_type_id,hour;
                        String service_name = "",service_total="",item_id,item_total,basic_service;
                        ArrayList<Order_Service_Info> order_service_infos = new ArrayList<Order_Service_Info>();
                        for (int j=0;j<order_details.length();j++)
                        {
                            JSONObject obj = order_details.getJSONObject(j);


                            service_id = obj.getString("service_id");
                            service_type_id = obj.getString("service_type_id");
                            service_name = obj.getString("service_type_name");

                            hour = obj.getString("no_of_hours");
                            item_total = obj.getString("member_share");
                            basic_service = obj.getString("basic_service");
                            service_total = obj.getString("member_share");
                            item_id = obj.getString("item_id");


                            Order_Service_Info order_service_info = new Order_Service_Info();
                            order_service_info.setService_id(service_id);
                            order_service_info.setService_type_id(service_type_id);
                            order_service_info.setService_name(service_name);
                            order_service_info.setHour(hour);
                            order_service_info.setTotal(service_total);
                            order_service_info.setOrder_item_id(item_id);
                            order_service_info.setItem_total(item_total);
                            order_service_info.setBasic_service(basic_service);



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


                        completedJobObject.setOrder_service_infoList(order_service_infos);
                        completedJobObject.setOrder_id(order_id);
                        completedJobObject.setMeet_loc(meet_location);
                        completedJobObject.setDatetime_meet(meet_datetime);

                        completedJobObject.setItem_desc(item_desc);
                        completedJobObject.setDelivery_person(delivery_person);
                        completedJobObject.setItem_type(item_type);
                        completedJobObject.setDoc_image(doc_image);


                        completedJobObject.setMain_service_name(main_service_name);
                        completedJobObject.setDatetime_ordered(datetime_ordered);
                        completedJobObject.setDatetime_started(datetime_started);
                        completedJobObject.setDatetime_accepted(datetime_accepted);
                        completedJobObject.setOrder_total(order_total);
                        completedJobObject.setTotal_distance(total_distance);
                        completedJobObject.setStatus(status);
                        completedJobObject.setMember_share(member_share);
                        completedJobObject.setNo_of_hours(no_of_hrs);
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
