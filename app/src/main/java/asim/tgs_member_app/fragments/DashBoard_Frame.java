package asim.tgs_member_app.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import asim.tgs_member_app.BumbleRideActivity;
import asim.tgs_member_app.Current_job_screen;
import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.service.BackgroundLocationService;
import asim.tgs_member_app.utils.GPSTracker;
import asim.tgs_member_app.utils.RideDirectionPointsDB;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class DashBoard_Frame extends Fragment
{
    private static final String TAG = "DashBoard_Frame";

    private androidx.viewpager.widget.ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout upcoming,completed,tabs_lay,current_layout;

    private String selected_language;
    private String key;
    private String member_id,member_name,lat,lon;


    private String pickup,detination,mem_share,booking_type,customer_name,customer_image,cust_mobile,order_datetime="",meet_datetime="",
            item_desc,delivery_person,doc_image,item_type,cust_id,order_id,total_distance,meet_date,service_id,no_of_hrs,service_name;
    private FirebaseDatabase firebaseDatabase;
    com.google.android.material.tabs.TabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooTView = inflater.inflate(R.layout.dashboard_layout, container, false);
    ///setToolBar();
        firebaseDatabase = FirebaseDatabase.getInstance();
        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        member_id = settings.getString(Constants.PREFS_USER_ID,"0");
        lat = settings.getString(Constants.PREFS_USER_LAT,"0");
        lon = settings.getString(Constants.PREFS_USER_LNG,"0");
        key ="tgs_appkey_amin";
        viewPager =  rooTView.findViewById(R.id.sos_viewpager);
        upcoming = (LinearLayout) rooTView.findViewById(R.id.selected_tab1);
        completed = (LinearLayout) rooTView.findViewById(R.id.selected_tab2);
        tabs_lay = (LinearLayout) rooTView.findViewById(R.id.tabs_lay);
        current_layout = (LinearLayout) rooTView.findViewById(R.id.current_layout);

        tabs = rooTView.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        current_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (haveJob && isBumble)
                {
                   /* startActivity(new Intent(getContext(),BumbleRideActivity.class)

                            .putExtra("mem_share",mem_share)
                            .putExtra("customer_name",customer_name)
                            .putExtra("customer_image",customer_image)
                            .putExtra("cust_mobile", cust_mobile));

*/
                    Intent intent = new Intent(getContext(), Current_job_screen.class);
                    intent.putExtra("isBumble",true);
                    intent.putExtra("pickup", pickup);
                    intent.putExtra("detination", detination);
                    intent.putExtra("mem_share", mem_share);
                    intent.putExtra("order_datetime", order_datetime);
                    intent.putExtra("meet_datetime", meet_datetime);
                    intent.putExtra("booking_type", booking_type);
                    intent.putExtra("service_name", service_name);
                    intent.putExtra("no_of_hours", no_of_hrs);
                    intent.putExtra("customer_name", customer_name);
                    intent.putExtra("customer_image", customer_image);
                    intent.putExtra("cust_mobile", cust_mobile);
                    intent.putExtra("cust_id", cust_id);
                    intent.putExtra("order_id", order_id);

                    intent.putExtra("item_desc", item_desc);
                    intent.putExtra("delivery_person", delivery_person);
                    intent.putExtra("doc_image", doc_image);
                    intent.putExtra("doc_type", item_type);

                    intent.putExtra("total_distance", total_distance);
                    startActivity(intent);
                    return;
                }

                if (haveJob) {
                    Intent intent = new Intent(getContext(), Current_job_screen.class);
                    intent.putExtra("isBumble",false);
                    intent.putExtra("pickup", pickup);
                    intent.putExtra("detination", detination);
                    intent.putExtra("mem_share", mem_share);
                    intent.putExtra("order_datetime", order_datetime);
                    intent.putExtra("meet_datetime", meet_datetime);
                    intent.putExtra("booking_type", booking_type);
                    intent.putExtra("service_name", service_name);
                    intent.putExtra("no_of_hours", no_of_hrs);
                    intent.putExtra("customer_name", customer_name);
                    intent.putExtra("customer_image", customer_image);
                    intent.putExtra("cust_mobile", cust_mobile);
                    intent.putExtra("cust_id", cust_id);
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("total_distance", total_distance);

                    intent.putExtra("item_desc", item_desc);
                    intent.putExtra("delivery_person", delivery_person);
                    intent.putExtra("doc_image", doc_image);
                    intent.putExtra("doc_type", item_type);


                    startActivity(intent);
                }
            }
        });

        settings =getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        selected_language = settings.getString(Constants.PREF_LOCAL,"english");

        if (!selected_language.equalsIgnoreCase("english"))
        {
            ViewGroup.LayoutParams params = upcoming.getLayoutParams();
            params.height = params.height + 20;
            upcoming.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = completed.getLayoutParams();
            params1.height = params1.height + 20;
            completed.setLayoutParams(params1);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) tabs_lay.getLayoutParams();
            params3.height = params3.height + 30;
            tabs_lay.setLayoutParams(params3);
        }

        upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcoming.setBackgroundResource(R.drawable.tab_selected_bg);
                completed.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(0);
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completed.setBackgroundResource(R.drawable.tab_selected_bg);
                upcoming.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(1);

            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        setupViewPager(viewPager,getArguments());


        return rooTView;
    }

    public class ViewPagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
        private final List<Fragment> _FragmentList = new ArrayList<>();
        private final List<String> _FragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return _FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return _FragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _FragmentTitleList.get(position);
        }

        public void AddFragments(Fragment fragment, String title) {
            _FragmentList.add(fragment);
            _FragmentTitleList.add(title);

        }

    }

    private void setupViewPager(androidx.viewpager.widget.ViewPager viewPager, Bundle bundle) {

        int index=0;

        index = settings.getInt(Constants.CURRENT_TAB,0);

        if (bundle!=null)
            index = bundle.getInt("index");


        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getFragmentManager());

        Upcoming_Jobs upcoming_jobs = new Upcoming_Jobs();
        pagerAdapter.AddFragments(upcoming_jobs,"Upcoming Jobs");

        Suggested_Jobs suggested_jobs = new Suggested_Jobs();
        pagerAdapter.AddFragments(suggested_jobs,"Suggested Jobs");


        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(index);

        // final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getFragmentManager());


       /* int index=0;

        index = settings.getInt(Constants.CURRENT_TAB,0);

        if (bundle!=null)
         index = bundle.getInt("index");



        final Upcoming_Jobs upcoming_ = new Upcoming_Jobs();

        adapter.addFragment(upcoming_,getResources().getString(R.string.Upcoming_jobs));

        Suggested_Jobs completed_ = new Suggested_Jobs();
        adapter.addFragment(completed_,getResources().getString(R.string.suggested_jobs));


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    upcoming.performClick();
                    if (UtilsManager.shouldRefresh) {
                        adapter.updateFragments();
                        UtilsManager.shouldRefresh = false;
                    }
                }
                else {
                    completed.performClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // tabLayout.setupWithViewPager(viewPager);
        //  setupTabIcons();

        //  TabLayout.Tab tab= tabLayout.getTabAt(index);
        //  tab.select();
        //  setupTabIcons();

        viewPager.setCurrentItem(index);*/

    }

    @Override
    public void onPause() {
        super.onPause();
        try {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

           // getContext().startService(new Intent(getContext(),BackgroundLocationService.class));
            checkCurrentJob();
            /*if (!UtilsManager.isMyServiceRunning(getContext(),BackgroundLocationService.class))
                 UtilsManager.startLocationService(getContext(),0);*/

            getCurrentLocation();

           // setUpTimer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private static final Integer INTERVAL_MIN = 1;
    private static final Integer INTERVAL_SEC = 10;

    private void setUpTimer()
    {
        if (countDownTimer!=null)
        {
            if (isRunning)
                return;
        }

        countDownTimer = new CountDownTimer(INTERVAL_MIN * INTERVAL_SEC*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("counting",(millisUntilFinished/1000)+"");
                isRunning = true;

            }

            @Override
            public void onFinish() {
                Log.e("counting done","...........");
                try {
                  checkCurrentJob();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };

        countDownTimer.start();

    }

    AsyncHttpClient asyncClient = new AsyncHttpClient();
    boolean haveJob = false;
    String test_pick = "Patiala Associates, Mir Chakar Khan Road, I-8 Markaz Islamabad";//"Faizabad Interchange, Murree Road, Islamabad, Pakistan";
    String test_dest = "6th Road, Rawalpindi";

    private void checkCurrentJob()
    {

        asyncClient.setConnectTimeout(20000);

        Log.e("CURRENT_JOB_API_MEMBER", Constants.Host_Address + "members/show_my_current_job/" + member_id + "/" + key);
        asyncClient.get(getContext(), Constants.Host_Address + "members/show_my_current_job/" + member_id + "/" + key, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response", response);
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray array = responseObj.getJSONArray("data");
                    JSONObject data = array.getJSONObject(0);

                    pickup = data.getString("meet_location");
                    service_id = data.getString("service_id");
                    detination = data.getString("destination");
                    mem_share = data.getString("member_share");
                    booking_type = data.getString("booking_type");
                    customer_name = data.getString("customer_name");
                    customer_image = data.getString("customer_profile_img");
                    cust_mobile = data.getString("customer_mobile");
                    cust_id = data.getString("customer_id");
                    order_id = data.getString("order_id");

                    item_desc =  data.getString("lalamove_description");
                    doc_image =  data.getString("lalamove_image");
                    delivery_person =  data.getString("lalamove_receiver_name");
                    item_type =  data.getString("lalamove_type");


                    no_of_hrs = data.getString("no_of_hours");
                    meet_datetime = data.getString("meet_datetime");
                    service_name = data.getString("service_name");
                    order_datetime = data.getString("datetime_ordered");
                    total_distance = data.getString("total_distance");
                    // meet_date = data.getString("meetup_time");

                    Log.e("refreshed", "data refreshed");

                    String mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
                    String mem_id = settings.getString(Constants.PREFS_USER_ID, "");
                    String current_job = settings.getString(Constants.CURRENT_JOB,"1");
                    String lat = settings.getString(Constants.PREFS_USER_LAT,"");
                    String lon = settings.getString(Constants.PREFS_USER_LNG,"");


                    SharedPreferences sharedPreferences =getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(Constants.MEET_LOCATION,pickup);
                    editor.putString(Constants.DESTINATION,detination);
                    editor.putString(Constants.TOTAL,mem_share);
                    editor.putString(Constants.ORDER_ID,order_id);
                    editor.putString(Constants.KOGHA_CUSTOMER_ID,cust_id);

                    editor.apply();


                    if (!order_id.equalsIgnoreCase("null")) {
                        current_layout.setVisibility(View.VISIBLE);
                        current_layout.getChildAt(0).setBackgroundColor(Color.parseColor("#00944F"));
                        ((TextView)current_layout.getChildAt(0)).setText("Current Job");
                        haveJob = true;
                        settings.edit().putString(Constants.CURRENT_JOB,order_id).apply();
                        final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lon + "");
                        member.setCurrent_job(order_id);

                        String key = mem_id + "_member";
                        if (!mem_id.equalsIgnoreCase(""))
                            firebaseDatabase.getReference().child("members").child(key).setValue(member);

                    }
                    else {
                        current_layout.setVisibility(View.VISIBLE);
                        ((TextView)current_layout.getChildAt(0)).setBackgroundColor(Color.BLACK);
                        ((TextView)current_layout.getChildAt(0)).setText("Currently You Don't Have Any Job");
                        haveJob = false;
                        settings.edit().putString(Constants.CURRENT_JOB,"0").apply();

                        final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lon + "");
                        member.setCurrent_job("0");

                      /*  String key = mem_id + "_member";
                        if (!mem_id.equalsIgnoreCase(""))
                           firebaseDatabase.getReference().child("members").child(key).setValue(member);*/
                    }

                    isRunning = false;
                    if (countDownTimer!=null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }

                    firebaseDatabase.getReference().child("members").child("_member").removeValue();

                    if (service_id.equalsIgnoreCase("3")) {

                        /*editor.putString(Constants.MEET_LOCATION,test_pick);
                        editor.putString(Constants.DESTINATION,test_dest);
                        editor.apply();*/
                        isBumble = true;

                    }
                    else
                    {
                        new RideDirectionPointsDB(getContext()).clearSavedPoints();
                    }



                  //  setUpTimer();
                }
                catch (Exception e)
                {
                    new RideDirectionPointsDB(getContext()).clearSavedPoints();
                    Log.e("refreshed",e.getMessage());
                    current_layout.setVisibility(View.GONE);
                    settings.edit().putString(Constants.CURRENT_JOB,"0").apply();
                    haveJob = false;

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    String response = new String(responseBody);
                    Log.e("response",response);
                }
                catch (Exception e)
                {
                    Log.e("refreshed",e.getMessage());
                }
            }
        });

    }

    private void stopLocationService(){
        try {
            if (UtilsManager.isMyServiceRunning(getActivity(), BackgroundLocationService.class)) {
                getActivity().stopService(new Intent(getActivity(), BackgroundLocationService.class));

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startLocationService() {
        try {
            if (!UtilsManager.isMyServiceRunning(getActivity(), BackgroundLocationService.class)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getActivity().startForegroundService(new Intent(getActivity(), BackgroundLocationService.class));
                } else {
                    getActivity().startService(new Intent(getActivity(), BackgroundLocationService.class));
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean isBumble = false;


    String current_job = "0";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                boolean isActive_loggedin = settings.getBoolean(Constants.PREFS_USER_ACTIVE,false);

                if (true) {

                    current_job = settings.getString(Constants.CURRENT_JOB,"0");
                    member_id = settings.getString(Constants.PREFS_USER_ID,"");
                    Log.e("location found", location.getLatitude() + "-" + location.getLongitude());
                    final MemberLocationObject member = new MemberLocationObject(member_id,member_name, "driver", location.getLatitude() + "", location.getLongitude() + "");
                    member.setCurrent_job(current_job);

                    String key = member_id + "_member";
                    if (!member_id.equalsIgnoreCase(""))
                        firebaseDatabase.getReference().child("members").child(key).setValue(member);
                    else
                        locManager.removeUpdates(this);

                    Log.e("location updated for ",  member_name);
                    editor = settings.edit();
                    editor.putString(Constants.PREFS_USER_LAT, location.getLatitude() + "");
                    editor.putString(Constants.PREFS_USER_LNG, location.getLongitude() + "");

                    editor.apply();
                }

            } catch (Exception e) {
                Log.e("location error", e.getMessage());
            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    LocationManager locManager;

    private void updateUserLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListenerGPS);
        //  locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListenerGPS);
    }

    void checkGps() {

        try {
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }
            else
            {
                updateUserLocation();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    android.app.AlertDialog alert;


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (locManager!=null && locationListenerGPS!=null)
                locManager.removeUpdates(locationListenerGPS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private  boolean location_found = false;
    private void getCurrentLocation()
    {

        try {

            GPSTracker gpsTracker = null;
            Location location;
            gpsTracker = new GPSTracker(getContext());
            if (gpsTracker.canGetLocation()) {
                location = gpsTracker.getLocation();
                if (location != null) {
                    location_found = true;
                    settings.edit().putString(Constants.PREFS_USER_LAT, location.getLatitude() + "").apply();
                    settings.edit().putString(Constants.PREFS_USER_LNG, location.getLongitude() + "").apply();


                    Log.e(TAG, "getCurrentLocation: "+location.getLatitude()+" lon "+location.getLongitude());

                }
            } else {
                if (settings == null)
                    settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

                String mem_id = settings.getString(Constants.PREFS_USER_ID, "117");
                String mem_name = settings.getString(Constants.PREFS_USER_NAME, "Asim Shahzad");

                getLastLocation(mem_id, mem_name);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getLastLocation(final String mem_id,final String mem_name) {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location loc) {
                        // GPS location can be null if GPS is switched off
                        try {
                            if (loc != null) {
                                settings.edit().putString(Constants.PREFS_USER_LAT, loc.getLatitude() + "").apply();
                                settings.edit().putString(Constants.PREFS_USER_LNG, loc.getLongitude() + "").apply();
                                Log.e(TAG, "getCurrentLocation: "+loc.getLatitude()+" lon "+loc.getLongitude());
                                final MemberLocationObject member = new MemberLocationObject(mem_id,mem_name, "driver",loc.getLatitude()+ "", loc.getLongitude() + "");
                                member.setCurrent_job("0");
                                String key = mem_id + "_member";
                                if (!mem_id.equalsIgnoreCase(""))
                                    FirebaseDatabase.getInstance().getReference().child("members").child(key).setValue(member);

                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }
}
