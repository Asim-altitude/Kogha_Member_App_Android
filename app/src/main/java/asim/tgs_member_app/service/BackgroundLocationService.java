package asim.tgs_member_app.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import asim.tgs_member_app.DrawerActivity;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;

/**
 * Created by PC-GetRanked on 3/6/2018.
 */

public class BackgroundLocationService extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler;
    private Runnable runnable;

    private String mem_name,mem_id;
    private FirebaseDatabase firebaseDatabase;
    @Override
public void onCreate() {
    super.onCreate();

    firebaseDatabase = FirebaseDatabase.getInstance();
    settings = this.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
    mem_id = settings.getString(Constants.PREFS_USER_ID, "");
    Log.e("service status","service started");
    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    handler = new Handler();
    runnable = new Runnable() {
        public void run()
        {
            handler.postDelayed(runnable, 10000);
        }
    };

    handler.postDelayed(runnable, 15000);
    checkGps();

}

    @Override
    public void onDestroy() {
        super.onDestroy();
            handler.removeCallbacks(runnable);
            locManager.removeUpdates(locationListenerGPS);
    }


    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                boolean isActive_loggedin = settings.getBoolean(Constants.PREFS_USER_ACTIVE,false);

                if (isActive_loggedin) {

                    Log.e("location found", location.getLatitude() + "-" + location.getLongitude());
                    final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", location.getLatitude() + "", location.getLongitude() + "");

                    String key = mem_id + "_member";
                    firebaseDatabase.getReference().child("members").child(key).setValue(member);

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
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}
