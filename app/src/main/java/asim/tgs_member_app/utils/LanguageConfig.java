package asim.tgs_member_app.utils;

/**
 * Created by PC-GetRanked on 5/9/2018.
 */

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class LanguageConfig
{

    public static void setLocale(Context cnx,String lang) {

        try {
            Locale myLocale = new Locale(lang);
            Resources res = cnx.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);

        }
        catch (Exception e)
        {
            Log.e("error local",e.getMessage());
        }

    }

}
