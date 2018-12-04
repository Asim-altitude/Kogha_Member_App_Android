package asim.tgs_member_app.models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import asim.tgs_member_app.LoginActivity;
import asim.tgs_member_app.service.BackgroundLocationService;

/**
 * Created by Sohaib on 7/23/2017.
 *
 */

public final class Constants {

   public static final String Customer_Image_BASE_PATH = "http://kogha.my/system/uploads/customer_profile_images/thumbs/";
   public static final String Host_Address = "http://kogha.my/system/webservices/v1/"; // Test

   public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

   // Static Tags
   public static final String PREFS_NAME = "com.getranked.teamguard.member.prefs";
   public static final String PREFS_ACCESS_TOKEN = "prefs_access_token";
   public static final String PREFS_USER_NAME = "prefs_user_name";
   public static final String PREFS_USER_EMAIL = "prefs_user_email";
   public static final String PREFS_USER_MOBILE = "prefs_user_mobile";
   public static final String PREFS_USER_PASSWORD = "prefs_user_password";
   public static final String PREFS_CUSTOMER_ID = "prefs_customer_id";
   public static final String KOGHA_CUSTOMER_ID = "kogha_customer_id";
   public static final String PREFS_LOGIN_STATE = "prefs_login_state";
   public static final String PREFS_USER_IMAGE = "prefs_user_image";
   public static final String PREFS_PROFILE_IMG = "prefs_profile_image";
   public static final String PREFS_SOS_STATUS = "prefs_sos_status";
   public static final String PREFS_USER_ID = "prefs_user_id";
   public static final String PREFS_USER_ADDRESS = "prefs_user_address";
   public static final String PREFS_USER_DOB = "prefs_user_dob";
   public static final String PREFS_USER_LAT = "prefs_user_lat";
   public static final String PREFS_USER_LNG = "prefs_user_lng";
   public static final String PREFS_USER_DATE = "prefs_user_date";
   public static final String PREFS_USER_THUMB = "prefs_user_thumb";
   public static final String PREF_LOCAL = "prefs_local";
   public static final String CURRENT_JOB = "current_job";
   public static final String CURRENT_TAB = "current_tab";

   public static final String APPROVED = "approved";
   public static boolean can_login = true;

   public static String DIRECTION_API_KEY = "";

   //static tags for registeration documents
   public static final String IC_CARD_DOC = "ic_card_doc";
   public static final String STRENGTH_DOC = "strength_doc";
   public static final String SIDEBODY_DOC = "sidebody_doc";
   public static final String DRIVING_LICENCE_DOC = "driving_doc";
   public static final String PASSPORT_DOC = "passport_doc";
   public static final String GRANT_DOC = "grant_doc";
   public static final String INSURANCE_DOC = "insurance_doc";
   public static final String INTERIOR_DOC = "interior_doc";
   public static final String CAR_DOC = "car_doc";
   public static final String TOTAL = "total";
   public static final String MEET_LOCATION = "meet_location";
   public static final String DESTINATION = "destination";

   public static boolean hasDrivingLicense = false;
   public static boolean hasProfessionalImage = false;
   public static boolean hasCarImage = false;

   public static String currency = "RM";

   public static final String PREFS_USER_ACTIVE = "prefs_user_logged_in";

   public static final String COMMUNICATION_BETWEEN_FRAGMENTS = "communication_between_fragments";

   public static final String USER_LOCATION = "location";

   public static final String USER_SOS_EMAILS = "user_sos_emails";
   public static final String USER_SOS_PHONE = "user_sos_phone";
   public static final String MAX_DISTANCE_RANGE = "max_distance_range";
   public static final String SELECTED_SOS_MESSAGE = "sos_message";

   public static final String ORDER_ID = "order_id";

   public static final String BROADCAST_INTENT_FILTER = "com.tgs.com.RESTART_SERVICE";
   public static final String USER_NOTIFICATION = "user_notifications";
   public static final String USER_CHAT_NOTIFICATION = "user_chat_notifications";
   public static final String USER_JOB_NOTIFICATION = "user_job_notifications";
   public static final String CURRENT_JOB_MEMBERS = "current_job_members";


   public static final String ADDED_LOCATION_FIREBASE = "updated_location_firebase";

   public static final int NOTICIATION_ID = 01010101;

   public static int selected_notification_tab = 0;

   public static boolean isChatting = false;

   public static final String MEMBER_CHAT_TAG = "mem_chat_tag";
   //MEMBER_CHAT_TAG

   public static void cancelNotification(Context ctx, int notifyId) {
      String ns = Context.NOTIFICATION_SERVICE;
      NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
      nMgr.cancel(notifyId);
   }

   public static double distance(double lat1, double lon1, double lat2, double lon2) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1))
              * Math.sin(deg2rad(lat2))
              + Math.cos(deg2rad(lat1))
              * Math.cos(deg2rad(lat2))
              * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      return (dist);
   }
   private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
   }

   private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
   }

   public static void logOutUser(Context context)
   {
      try {
         SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
         preferences.edit().putString(Constants.PREFS_USER_ID,"").apply();
         preferences.edit().clear().apply();
         preferences.edit().putBoolean(PREFS_USER_ACTIVE,false).apply();
         context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
         context.stopService(new Intent(context, BackgroundLocationService.class));

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   public static LatLng getLocationFromAddress(Context context, String strAddress) {

      Geocoder coder = new Geocoder(context);
      List<Address> address;
      LatLng p1 = null;

      try {
         // May throw an IOException
         address = coder.getFromLocationName(strAddress, 5);
         if (address == null) {
            return null;
         }
         Address location = address.get(0);
         location.getLatitude();
         location.getLongitude();

         p1 = new LatLng(location.getLatitude(), location.getLongitude() );

      } catch (IOException ex) {

         ex.printStackTrace();
      }

      return p1;
   }

}
