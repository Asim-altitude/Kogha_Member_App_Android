package asim.tgs_member_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by PC-GetRanked on 4/24/2018.
 */

public class RideDirectionPointsDB
{

    private SharedPreferences prefs;
    public static final String DIRECTION_DB = "direction_db";
    public static final String DIRECTION_KEY = "direction_key";
    public RideDirectionPointsDB(Context context) {

        if (context!=null)
            prefs = context.getSharedPreferences(DIRECTION_DB,Context.MODE_PRIVATE);
    }

    public void saveDirectionPoints(ArrayList<LatLng> points)
    {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(points);
        editor.putString(DIRECTION_KEY,json);
        editor.apply();
    }

    public ArrayList<LatLng> getDirectionPointsList()
    {
        Gson gson = new Gson();
        String json = prefs.getString(DIRECTION_KEY, "");

        if (json.equalsIgnoreCase(""))
            return null;

        Type type = new TypeToken<ArrayList<LatLng>>(){}.getType();
        ArrayList<LatLng> points= gson.fromJson(json, type);

        return points;

    }

    public void clearSavedPoints()
    {
        if (prefs!=null)
        {
            prefs.edit().putString(DIRECTION_KEY,"").apply();
        }
    }

}
