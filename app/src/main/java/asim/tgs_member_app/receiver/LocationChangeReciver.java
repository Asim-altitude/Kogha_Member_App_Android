package asim.tgs_member_app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.service.LocationListnerServices;
import asim.tgs_member_app.service.ServiceForMemberLocation;

/**
 * Created by PC-GetRanked on 10/3/2018.
 */

public class LocationChangeReciver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    String current_job = "0";
    String mem_id = "";
    String mem_name = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String lat = intent.getStringExtra(LocationListnerServices.SERIVICE_LATITUDE);
        String lng = intent.getStringExtra(LocationListnerServices.SERIVICE_LONGITUDE);
        Log.e("location_changed",lat+"-"+lng);

        if (sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);

        current_job = sharedPreferences.getString(Constants.CURRENT_JOB,"0");
        mem_id = sharedPreferences.getString(Constants.PREFS_USER_ID,"");
        mem_name = sharedPreferences.getString(Constants.PREFS_USER_NAME,"");

        final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lng + "");
        member.setCurrent_job(current_job);

        String key = mem_id + "_member";
        if (!mem_id.equalsIgnoreCase("")) {
            FirebaseDatabase.getInstance().getReference().child("members").child(key).setValue(member);
            Log.e("member_position","member position updated on firebase");
        }
    }
}
