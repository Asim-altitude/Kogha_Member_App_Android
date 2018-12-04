package asim.tgs_member_app.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by Sohaib on 7/23/2017.
 *
 */

public final class UtilsManager {

   public static boolean shouldRefresh = false;
   public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
      int Radius = 6371;// radius of earth in Km
      double lat1 = StartP.latitude;
      double lat2 = EndP.latitude;
      double lon1 = StartP.longitude;
      double lon2 = EndP.longitude;
      double dLat = Math.toRadians(lat2 - lat1);
      double dLon = Math.toRadians(lon2 - lon1);
      double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
              + Math.cos(Math.toRadians(lat1))
              * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
              * Math.sin(dLon / 2);
      double c = 2 * Math.asin(Math.sqrt(a));
      double valueResult = Radius * c;
      double km = valueResult / 1;
      DecimalFormat newFormat = new DecimalFormat("####");
      int kmInDec = Integer.valueOf(newFormat.format(km));
      double meter = valueResult % 1000;
      int meterInDec = Integer.valueOf(newFormat.format(meter));
      Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
              + " Meter   " + meterInDec);

      return Radius * c;
   }
   public static void showAlertMessage(Context context, String title, String message){

      try {

         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle(title);
         builder.setMessage(message);
         builder.setNegativeButton("OK", null);
         if (context!=null)
             builder.show();

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static int convertDipToPixels(float dips,Context context)
   {
      return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
   }

   public static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
      ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
         if (serviceClass.getName().equals(service.service.getClassName())) {
            return true;
         }
      }
      return false;
   }
}
