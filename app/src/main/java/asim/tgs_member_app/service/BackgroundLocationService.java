package asim.tgs_member_app.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import androidx.core.app.NotificationCompat;
import asim.tgs_member_app.DrawerActivity;
import asim.tgs_member_app.R;
import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.chat.FireBaseChatHead;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.models.OTWStateObject;
import asim.tgs_member_app.utils.NotificationHelper;
import asim.tgs_member_app.utils.NotificationUtils;
import asim.tgs_member_app.utils.OTWRidePrefs;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by PC-GetRanked on 3/6/2018.
 */

public class BackgroundLocationService extends Service
{
    private static final String TAG = "BackgroundLocationServi";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler;
    private Runnable runnable;

    private String mem_name,mem_id;
    private FirebaseDatabase firebaseDatabase;
    private String current_job = "0";
    private NotificationHelper notificationHelper;
    @Override
    public void onCreate() {
        super.onCreate();

        firebaseDatabase = FirebaseDatabase.getInstance();
        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        mem_id = settings.getString(Constants.PREFS_USER_ID, "");
        current_job = settings.getString(Constants.CURRENT_JOB, "0");
        Log.e("service status", "service started");
        otwRidePrefs = new OTWRidePrefs(getApplicationContext());
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        notificationHelper = new NotificationHelper(getApplicationContext());
        startLocationUpdates();

       // startTimer();
  /*  handler = new Handler();
    runnable = new Runnable() {
        public void run()
        {
            handler.postDelayed(runnable, 10000);
        }
    };

    handler.postDelayed(runnable, 15000);*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationHelper.createNotificationFordound("Ride", "Ride In Progress", intent);

        }*/
        //checkGps();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            int status = intent.getIntExtra("state", 0);
            if (status == 0) {
                stopForeground(true);
                startForgroundServiceWithNotification("Ride", "Ride in progress");
            } else  {
                stopTracking();

               // startForgroundServiceWithNotification("Trip Started", "Your are on the way to destination");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return START_STICKY;


    }

    private void startForgroundServiceWithNotification(String title, String message)
    {
        NotificationUtils notificationUtils = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationUtils = new NotificationUtils(getApplicationContext());
            Notification.Builder notificationBuilder =
                    notificationUtils.getAndroidChannelNotification(title, message);
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            notificationBuilder.setContentIntent(getLaunchIntent());
            Notification notification = notificationBuilder.build();

            startForeground(1, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Pick Rider")
                    .setContentText("On the way to pick up")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(getLaunchIntent())
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);

        }
    }

    private PendingIntent getLaunchIntent()
    {
        Intent intent = new Intent(getApplicationContext(),DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }


    private LocationRequest mLocationRequest = null;
    private long UPDATE_INTERVAL =  5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 3000; /* 2 sec */

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisonStartfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

       /* Intent intent = new Intent(proximitys);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
     */   //getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,proximityIntent);


        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,locationCallback,Looper.myLooper());
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // do work here

            try {
                if (locationResult.getLastLocation() == null)
                    return;

                Location location = locationResult.getLastLocation();
                current_job = settings.getString(Constants.CURRENT_JOB, "0");
                mem_id = settings.getString(Constants.PREFS_USER_ID, "");
                Log.e("CURRENT_LOCATION", location.getLatitude() + "-" + location.getLongitude());
                final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", location.getLatitude() + "", location.getLongitude() + "");
                member.setCurrent_job(current_job);

                String key = mem_id + "_member";
                if (!mem_id.equalsIgnoreCase("")) {
                    firebaseDatabase.getReference().child("members").child(key).setValue(member);
                }

                checkIfReachedAtPickup(location);


                Log.e("location updated for ", mem_name);
                editor = settings.edit();
                editor.putString(Constants.PREFS_USER_LAT, location.getLatitude() + "");
                editor.putString(Constants.PREFS_USER_LNG, location.getLongitude() + "");

                editor.apply();

            }
            catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "onLocationResult: "+e.getMessage());
            }
            
        }
    };

    private LatLng pickup_cords = null;
    private double distance = 101;
    private boolean isReached = false;
    OTWStateObject otwStateObject = null;
    OTWRidePrefs otwRidePrefs = null;//new OTWRidePrefs(getApplicationContext());
    private void checkIfReachedAtPickup(Location location) {
        try { otwStateObject = otwRidePrefs.getOTWState();
            if (pickup_cords==null) {

                String pickup = otwStateObject.getPickup();
                pickup_cords = UtilsManager.getLocationFromAddress(getApplicationContext(), pickup);
            }

            try
            {
                LatLng endP = new LatLng(location.getLatitude(),location.getLongitude());
                distance = UtilsManager.distance(pickup_cords.latitude,pickup_cords.longitude, endP.latitude,endP.longitude);


                if (distance < 100 && !isReached){
                    Log.e(TAG, "checkIfReachedAtPickup: REACHED "+distance);
                    isReached = true;
                    String chat_id = FireBaseChatHead.getUniqueChatId(mem_id,otwStateObject.getCustomer_id(),otwStateObject.getOrder_id());
                    sendMessage(otwStateObject.getCustomer_id(),"Your member "+mem_name+" is nearby",chat_id);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "checkIfReachedAtPickup: "+e.getMessage());

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void sendMessage(String customer_id,String messge,String chat_id) {
        try {
            asyncHttpClient.setConnectTimeout(30000);


            Log.e("url",Constants.Host_Address + "members/send_chat_notification_to_customer/" +mem_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/tgs_appkey_amin");
            asyncHttpClient.get(BackgroundLocationService.this,Constants.Host_Address + "members/send_chat_notification_to_customer/" +mem_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/tgs_appkey_amin", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        Log.e("response ",response);
                    }
                    catch (Exception e)
                    {
                        Log.e("error_notification",e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        String response = new String(responseBody);
                        Log.e("response ",response);
                    }
                    catch (Exception e)
                    {
                        Log.e("error_notification",e.getMessage());
                    }
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(locationCallback);
            stopTracking();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void stopTracking()
    {
        try {
            stopForeground(true);
            stopSelf();

            notificationHelper.clearNotification();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
            /*    boolean isActive_loggedin = settings.getBoolean(Constants.PREFS_USER_ACTIVE,false);*/

                if (true) {

                    current_job = settings.getString(Constants.CURRENT_JOB,"0");
                    mem_id = settings.getString(Constants.PREFS_USER_ID,"");
                    Log.e("location found", location.getLatitude() + "-" + location.getLongitude());
                    final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", location.getLatitude() + "", location.getLongitude() + "");
                    member.setCurrent_job(current_job);

                    String key = mem_id + "_member";
                    if (!mem_id.equalsIgnoreCase("")) {
                        firebaseDatabase.getReference().child("members").child(key).setValue(member);
                    }
                    else
                    {
                        locManager.removeUpdates(this);
                    }

                    Log.e("location updated for ", mem_name);
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext());
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


    int index = 0;
    CountDownTimer countDownTimer = null;
    private void startTimer()
    {
        countDownTimer = new CountDownTimer(10*60*1000,1000*60) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (index<array.length)
                {

                    Location location = new Location("");
                    location.setLatitude(array[index].latitude);
                    location.setLongitude(array[index].longitude);
                    Log.e("LOCATION_DUM",array[index].latitude+" "+array[index].longitude);
                    index++;
                    if (index==2){
                        location = new Location("");
                        location.setLatitude(pickup_cords.latitude);
                        location.setLongitude(pickup_cords.longitude);
                        checkIfReachedAtPickup(location);
                    }else {
                        checkIfReachedAtPickup(location);
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();


    }

    private LatLng[] array = new LatLng[]{new LatLng(3.1643230,101.56432),new LatLng(3.154430,101.715103),new LatLng(3.434240,101.34659),new LatLng(3.238460,101.681350)};

}
