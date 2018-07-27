package asim.tgs_member_app.restclient;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import asim.tgs_member_app.models.MOServiceTypeList;

/**
 * Created by Asim Shahzad on 7/5/2017.
 */
public class MyPrefrences
{
    private  Context context;
    private SharedPreferences prefs;

    public MyPrefrences(Context context)
    {
        this.context=context;
        prefs=context.getSharedPreferences("TGS",Context.MODE_PRIVATE);
    }

    public void saveValue(String key,String value)
    {
        int index = key.lastIndexOf("/");
        key = key.substring((index+1),key.length());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public String getValue(String key)
    {
        int index = key.lastIndexOf("/");
        key = key.substring((index+1),key.length());
        String value = prefs.getString(key, "");
        return value;
    }

    public void saveMyOrders(ArrayList<MOServiceTypeList> list,String tag)
    {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(tag, json);
        prefsEditor.commit();
    }

    public ArrayList<MOServiceTypeList> getOrdersList(String tag)
    {
        Gson gson = new Gson();
        String json = prefs.getString(tag, "");
        Type type = new TypeToken<ArrayList<MOServiceTypeList>>(){}.getType();
        ArrayList<MOServiceTypeList> list= gson.fromJson(json, type);
        return list;
    }

    public void clearOrders(String tag)
    {
        ArrayList<MOServiceTypeList> list =  getOrdersList(tag);

        if (list!=null)
            list.clear();

        saveMyOrders(list,tag);

    }

    public void setValue(String key,String value)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    private String getValueFromKey(String key)
    {
        String value = prefs.getString(key,"");
        return  value;
    }

}
