package asim.tgs_member_app.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import asim.tgs_member_app.service.BackgroundLocationService;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

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


   public static double distance(double lat1, double lon1, double lat2, double lon2) {

       /*   Location locationA = new Location("Place A");
          locationA.setLatitude(lat1);
          locationA.setLongitude(lon1);

          Location locationB = new Location("Place B");
          locationB.setLatitude(lat2);
          locationB.setLongitude(lon2);


          double distance_bw = locationA.distanceTo(locationB);

          return distance_bw;*/



      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1))
              * Math.sin(deg2rad(lat2))
              + Math.cos(deg2rad(lat1))
              * Math.cos(deg2rad(lat2))
              * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515 * 1000;
      return (dist);
   }

   private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
   }

   private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
   }


   public static void startLocationService(Context context,int state)
   {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         context.startForegroundService(new Intent(context, BackgroundLocationService.class).putExtra("state",state));
      } else {
         context.startService(new Intent(context,BackgroundLocationService.class).putExtra("state",state));
      }
   }


   public static LatLng getLocationFromAddress(Context context, String strAddress) {
      LatLng p1 = null;
      if (strAddress!=null) {
         if (!strAddress.equalsIgnoreCase("")) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            try {
               // May throw an IOException
               address = coder.getFromLocationName(strAddress, 1);
               if (address == null) {
                  return null;
               }

               Address location = address.get(0);
               p1 = new LatLng(location.getLatitude(), location.getLongitude());

            } catch (Exception ex) {

               ex.printStackTrace();
            }
         }
      }

      return p1;
   }



   public static void showAlertMessage(Context context, String title, String message) {

      try {

         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle(title);
         builder.setMessage(message);
         builder.setNegativeButton("OK", null);
         if (context != null)
            builder.show();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static int convertDipToPixels(float dips, Context context) {
      return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
   }

   public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
      ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
         if (serviceClass.getName().equals(service.service.getClassName())) {
            return true;
         }
      }
      return false;
   }

   public static void setListViewHeightBasedOnChildren(ListView listView) {
      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter != null) {
         int totalHeight = 0;
         int size = listAdapter.getCount();
         for (int i = 0; i < size; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
         }
         totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params.height = totalHeight;
         listView.setLayoutParams(params);

      }

   }

   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   public static String getPathFromUri(final Context context, final Uri uri) {

      final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

      // DocumentProvider
      if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
         // ExternalStorageProvider
         final String docId = DocumentsContract.getDocumentId(uri);
         if (docId.startsWith("raw:")) {
            return docId.replaceFirst("raw:", "");
         }

         if (isExternalStorageDocument(uri)) {
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
               return Environment.getExternalStorageDirectory() + "/" + split[1];
            }

            // TODO handle non-primary volumes
         }
         // DownloadsProvider
         else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
         }
         // MediaProvider
         else if (isMediaDocument(uri)) {

            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
               contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
               contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
               contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
         }
      }
      // MediaStore (and general)
      else if ("content".equalsIgnoreCase(uri.getScheme())) {

         // Return the remote address
         if (isGooglePhotosUri(uri))
            return uri.getLastPathSegment();

         return getDataColumn(context, uri, null, null);
      }
      // File
      else if ("file".equalsIgnoreCase(uri.getScheme())) {
         return uri.getPath();
      }

      return null;
   }

   public static boolean setListViewHeightBasedOnItems(ListView listView) {

      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter != null) {

         int numberOfItems = listAdapter.getCount();

         // Get total height of all items.
         int totalItemsHeight = 0;
         for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            float px = 500 * (listView.getResources().getDisplayMetrics().density);
            item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalItemsHeight += item.getMeasuredHeight();
         }

         // Get total height of all item dividers.
         int totalDividersHeight = listView.getDividerHeight() *
                 (numberOfItems - 1);
         // Get padding
         int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

         // Set list height.
         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params.height = totalItemsHeight + totalDividersHeight + totalPadding;
         listView.setLayoutParams(params);
         listView.requestLayout();
         //setDynamicHeight(listView);
         return true;

      } else {
         return false;
      }

   }


   @RequiresApi(api = Build.VERSION_CODES.M)
   public static void addNewRideEvent(Context ctx, String title, String desc, String st_date) {
      try {
        /* ContentResolver cr = ctx.getContentResolver();
         ContentValues values = new ContentValues();

         values.put(CalendarContract.Events.DTSTART, st_date);
         values.put(CalendarContract.Events.TITLE, title);
         values.put(CalendarContract.Events.DESCRIPTION, desc);

         TimeZone timeZone = TimeZone.getDefault();
         values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

         // default calendar
         values.put(CalendarContract.Events.CALENDAR_ID, 1);

         values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
                 + getDTUtil(st_date));
         // for one hour
         values.put(CalendarContract.Events.DURATION, "+P1H");

         values.put(CalendarContract.Events.HAS_ALARM, 1);

         // insert event to calendar
         Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
*/

         long calID = 1;
         long startMillis = 0;
         long endMillis = 0;
         String inputPattern = "yyyy-MM-dd HH:mm:ss";
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputPattern);
         Date start_date = simpleDateFormat.parse(st_date);

         Calendar beginTime = Calendar.getInstance();
         int month, year, day, hr, min;
         month = Integer.parseInt(DateFormat.format("MM", start_date).toString()) - 1;
         year = Integer.parseInt(DateFormat.format("yyyy", start_date).toString());
         day = Integer.parseInt(DateFormat.format("dd", start_date).toString());
         hr = Integer.parseInt(DateFormat.format("hh", start_date).toString());
         min = Integer.parseInt(DateFormat.format("mm", start_date).toString());
         beginTime.set(year, month, day, hr, min);
         startMillis = beginTime.getTimeInMillis();
         Calendar endTime = Calendar.getInstance();
         int min_ = Integer.parseInt(DateFormat.format("mm", start_date).toString());
         if (hr <= 11)
            endTime.set(year, month, day, hr + 1, min);
         else
            endTime.set(year, month, day, hr + 1, 5);

         endMillis = endTime.getTimeInMillis();

         ContentResolver cr = ctx.getContentResolver();
         ContentValues values = new ContentValues();
         values.put(CalendarContract.Events.DTSTART, startMillis);
         values.put(CalendarContract.Events.DTEND, endMillis);
         values.put(CalendarContract.Events.TITLE, title);
         values.put(CalendarContract.Events.DESCRIPTION, desc);
         values.put(CalendarContract.Events.CALENDAR_ID, calID);
         values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
         if (ctx.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
         }
         Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
         long eventID = Long.parseLong(uri.getLastPathSegment());
      }
      catch (Exception e){
         e.printStackTrace();
      }

   }

   private static String getDTUtil(String date){
      String inputPattern = "dd/MM/yyyy";
      SimpleDateFormat yyyyMMdd = new SimpleDateFormat(inputPattern);
      Calendar dt = Calendar.getInstance();

// Where untilDate is a date instance of your choice, for example 30/01/2012
      dt.setTime(new Date(date));

// If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
      dt.add(Calendar.DATE, 1);
      String dtUntill = yyyyMMdd.format(dt.getTime());

      return dtUntill;
   }

   public static String getDataColumn(Context context, Uri uri, String selection,
                                      String[] selectionArgs) {

      Cursor cursor = null;
      final String column = "_data";
      final String[] projection = {
              column
      };

      try {
         cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                 null);
         if (cursor != null && cursor.moveToFirst()) {
            final int index = cursor.getColumnIndexOrThrow(column);
            return cursor.getString(index);
         }
      } finally {
         if (cursor != null)
            cursor.close();
      }
      return null;
   }


   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is ExternalStorageProvider.
    */
   public static boolean isExternalStorageDocument(Uri uri) {
      return "com.android.externalstorage.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is DownloadsProvider.
    */
   public static boolean isDownloadsDocument(Uri uri) {
      return "com.android.providers.downloads.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is MediaProvider.
    */
   public static boolean isMediaDocument(Uri uri) {
      return "com.android.providers.media.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is Google Photos.
    */
   public static boolean isGooglePhotosUri(Uri uri) {
      return "com.google.android.apps.photos.content".equals(uri.getAuthority());
   }
}
