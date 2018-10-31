package asim.tgs_member_app.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;

import asim.tgs_member_app.models.BumbleRideInformation;

/**
 * Created by PC-GetRanked on 9/6/2018.
 */

public class BumbleRidePrefs
{
    SharedPreferences prefs;
    Context context;

    public BumbleRidePrefs(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("bumble",Context.MODE_PRIVATE);
    }

    public void saveBumbleRide(BumbleRideInformation bumbleRideInformation)
    {
        Gson gson = new Gson();
        String json = gson.toJson(bumbleRideInformation);
        prefs.edit().putString("bumble_info",json).apply();

    }

    public BumbleRideInformation getCurrentBumbleInfo()
    {
        BumbleRideInformation bumbleRideInformation = null;
        try {

            String json = prefs.getString("bumble_info", null);
            Gson gson = new Gson();
            if (json != null) {
                bumbleRideInformation = gson.fromJson(json, BumbleRideInformation.class);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bumbleRideInformation;
    }



}
