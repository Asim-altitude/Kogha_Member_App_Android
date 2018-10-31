package asim.tgs_member_app.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Sohaib on 7/23/2017.
 *
 */

public final class UtilsManager {

   public static boolean shouldRefresh = false;

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
