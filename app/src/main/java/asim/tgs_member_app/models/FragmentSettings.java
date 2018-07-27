package asim.tgs_member_app.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Asim Shahzad on 12/8/2017.
 */
public class FragmentSettings
{
    private SharedPreferences prefs;
    private Context context;

    public FragmentSettings(Context context)
    {
        this.context = context;
        prefs =context.getSharedPreferences("fragment_settings",Context.MODE_PRIVATE);
    }

    public void setTabIndex(String index)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("tab_index",index);
        editor.commit();
    }

    public String getTabIndex()
    {
      return prefs.getString("tab_index","0");

    }

    public String getUserPassword()
    {
        return prefs.getString("pass","");
    }

    public void setUserPassword(String password)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pass",password);
        editor.commit();
    }

    public void setValue(String key,String value)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getValue(String key)
    {
        return prefs.getString(key,"");
    }

}
