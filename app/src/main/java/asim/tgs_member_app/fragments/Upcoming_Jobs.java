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

import androidx.fragment.app.Fragment;
import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.UpcomingJobAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.OTWStateObject;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.models.StopContactObj;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.OTWRidePrefs;
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
    private String member_id="",lat,lon;
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
         lat = settings.getString(Constants.PREFS_USER_LAT,"4.2105");
         lon = settings.getString(Constants.PREFS_USER_LNG,"101.9758");

        if (lat.equalsIgnoreCase("null"))
            lat = "4.2105";

        if (lon.equalsIgnoreCase("null"))
            lon = "101.9758";

         key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");


        list_data = new ArrayList<>();
        adapter = new UpcomingJobAdapter(list_data,getContext());
        suggested_jobs_list.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preloadAPI();
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                preloadAPI();
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

    private void preloadAPI() {

        Log.e("ID", "populateList: "+member_id );
        asyncHttpClient.setConnectTimeout(40000);
        String url = Constants.Host_Address + "members/make_member_current_job/"+member_id+"/"+key;
        Log.e("UPCOMING",Constants.Host_Address + "members/make_member_current_job/"+member_id+"/"+key);
        asyncHttpClient.get(getContext(), Constants.Host_Address + "members/make_member_current_job/"+member_id+"/"+key, new AsyncHttpResponseHandler() {


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
                try
                {
                    populateList(member_id,lat,lon);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    // swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    populateList(member_id,lat,lon);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }



    private ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void populateList(String member_id,String lat,String lon) {

        Log.e("ID", "populateList: "+member_id );
        asyncHttpClient.setConnectTimeout(40000);
        String url = Constants.Host_Address + "members/my_jobs/"+member_id+"/"+key;
        Log.e("UPCOMING",Constants.Host_Address + "members/my_jobs/"+member_id+"/"+key);
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
                    Log.e("UPCOMING_JOBS",responseData);
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

                    OTWRidePrefs otwRidePrefs = new OTWRidePrefs(getActivity());
                    OTWStateObject otwStateObject;


                    String am_pm,cust_name,cust_img;
                    String order_id,main_id,order_item_id,status_show,service_type_name,meet_location,destination,datetime_ordered,meet_datetime,order_total,job_status,status_id
                            ,item_desc,delivery_person,doc_image,item_type,total_distance,status,instructions,no_of_hours,member_share,booking_type,customer_id,datetime_accepted;
                    String server_time;

                    StopContactObj pick_contact_obj = new StopContactObj();
                    StopContactObj dest_contact_obj = new StopContactObj();

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
                        main_id =  obj_json.getString("main_id");
                        instructions =  obj_json.getString("instructions");
                        destination =  obj_json.getString("destination");
                        service_type_name =  obj_json.getString("service_type_name");

                        item_desc =  obj_json.getString("lalamove_description");
                        doc_image =  obj_json.getString("lalamove_image");
                        delivery_person =  obj_json.getString("lalamove_receiver_name");
                        item_type =  obj_json.getString("lalamove_type");
                        total_distance = obj_json.getString("total_distance");

                        try {
                            if (!item_desc.equalsIgnoreCase("null")) {
                                service_type_name = obj_json.getString("service_name");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        String pickup_address =  obj_json.getString("pickup_address");
                        String pickup_contact =  obj_json.getString("pickup_contact");
                        String destination_address =  obj_json.getString("destination_address");
                        String destination_contact =  obj_json.getString("destination_contact");

                        pick_contact_obj.setAddress(pickup_address);
                        pick_contact_obj.setContact(pickup_contact);

                        dest_contact_obj.setContact(destination_contact);
                        dest_contact_obj.setAddress(destination_address);

                        //service_type_name
                        datetime_accepted = obj_json.getString("datetime_accepted");
                        no_of_hours =  obj_json.getString("no_of_hours");
                        member_share =  obj_json.getString("member_share");
                        status_show =  obj_json.getString("status_show");
                      //  order_item_id =  obj_json.getString("order_item_id");
                        booking_type =  obj_json.getString("booking_type");
                        customer_id =  obj_json.getString("customer_id");
                        server_time =  obj_json.getString("server_time");
                        cust_img =  obj_json.getString("customer_img");
                        cust_name =  obj_json.getString("customer_name");
                        job_status = obj_json.getString("job_status");
                        status_id = obj_json.getString("status");

                        suggestedJobObject.setOrder_id(order_id);
                        suggestedJobObject.setMain_id(main_id);
                        suggestedJobObject.setMeet_loc(meet_location);
                        suggestedJobObject.setDatetime_meet(meet_datetime);

                        suggestedJobObject.setItem_desc(item_desc);
                        suggestedJobObject.setDelivery_person(delivery_person);
                        suggestedJobObject.setItem_type(item_type);
                        suggestedJobObject.setDoc_image(doc_image);
                        suggestedJobObject.setTotal_distance(total_distance);

                        suggestedJobObject.setPick_contact_obj(pick_contact_obj);
                        suggestedJobObject.setDestination_contact_obj(dest_contact_obj);


                        try {

                            JSONArray order_details = obj_json.getJSONArray("order_details");

                            String service_id, service_type_id, hour;
                            String service_name = "", service_total = "", item_id, item_total, basic_service;
                            ArrayList<Order_Service_Info> order_service_infos = new ArrayList<Order_Service_Info>();
                            for (int j = 0; j < order_details.length(); j++) {
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

                                if (j == 0) {
                                    suggestedJobObject.setMember_share(service_total);
                                }

                                boolean added = false;
                                for (int k = 0; k < order_service_infos.size(); k++) {
                                    if (order_service_infos.get(k).getService_name().equalsIgnoreCase(service_name)) {
                                        added = true;
                                        break;
                                    }
                                }

                                if (!added) {
                                    order_service_infos.add(order_service_info);
                                }


                            }


                            suggestedJobObject.setService_list(order_service_infos);


                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        suggestedJobObject.setDatetime_ordered(datetime_ordered);
                        suggestedJobObject.setDatetime_accepted(datetime_accepted);
                        if (i==0) {
                            suggestedJobObject.setOtw_state(status_show);
                            otwStateObject = otwRidePrefs.getOTWState();
                            if (otwStateObject!=null){
                                if (!otwStateObject.getOrder_id().equalsIgnoreCase(order_id)){
                                    otwRidePrefs.saveOTWState(null);
                                }
                            }

                        }else {
                            suggestedJobObject.setOtw_state(status_show);
                        }
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
                        suggestedJobObject.setService_type_name(service_type_name);

                       /* OTWStateObject otwStateObject = otwRidePrefs.getOTWState();

                        if (otwStateObject!=null){
                            if (order_id)
                        }*/
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

                       if (!isAlreadyAdded(order_id))
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


    public boolean isAlreadyAdded(String id){
        boolean added = false;


        for (int i=0;i<list_data.size();i++){
            if (list_data.get(i).getOrder_id().equalsIgnoreCase(id))
            {
                added = true;
                break;
            }
        }


        return added;
    }


    @Override
    public void update(Observable o, Object arg) {
        populateList(member_id, lat, lon);
    }
}
