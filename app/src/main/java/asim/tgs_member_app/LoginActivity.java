package asim.tgs_member_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.ErrorCodes;
import asim.tgs_member_app.models.FragmentSettings;
import asim.tgs_member_app.models.MOLoginResponse;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.registration.SignUp_Two;
import asim.tgs_member_app.restclient.BaseModel;
import asim.tgs_member_app.restclient.ErrorModel;
import asim.tgs_member_app.restclient.RestServiceClient;
import asim.tgs_member_app.utils.GPSTracker;
import asim.tgs_member_app.utils.LanguageConfig;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * A login screen that offers login via email/password.
 */


public class LoginActivity extends AppCompatActivity implements Observer, View.OnClickListener {

   public static final String TAG = "LoginActivity";

   private CheckBox remember;

   RestServiceClient restServiceClient;

   // UI references.
   private EditText mUsernameView;
   private EditText mPasswordView;

   private MOLoginResponse loginResponse;
   private FusedLocationProviderClient mFusedLocationClient;



   LocationManager locationManager;
   SharedPreferences settings;

   private static final String SOS_ENABLE_DISABLE_KEY = "enable_disable_key";

   private FirebaseDatabase databaseReference;

   private GPSTracker gpsTracker;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      gpsTracker = new GPSTracker(LoginActivity.this);

      settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
      boolean login = settings.getBoolean(Constants.PREFS_LOGIN_STATE,false);

      if(login) {
         startActivity(new Intent(this, DrawerActivity.class));
         finish();
      }

      LoadLocalConfigurations();

      databaseReference = FirebaseDatabase.getInstance();



      // Set up the login form.
      remember = (CheckBox) findViewById(R.id.checkBoxRemember);
      mUsernameView =  findViewById(R.id.username);

      mPasswordView = (EditText) findViewById(R.id.password);
      mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
         @Override
         public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == R.id.email_sign_in_button || id == EditorInfo.IME_NULL) {
               attemptLogin();
               return true;
            }
            return false;
         }
      });

     /* mUsernameView.setText("dani@getranked.com.my");
      mPasswordView.setText("123456");
*/
      Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
      TextView btnSignUp = (TextView) findViewById(R.id.btnSignUp);
      TextView btnForgotPassword = (TextView) findViewById(R.id.btnForgotPassword);

      btnSignUp.setOnClickListener(this);
      mEmailSignInButton.setOnClickListener(this);
      btnForgotPassword.setOnClickListener(this);

      AskPermissions();
      checkGps();
      saveCurrentLocation();


   }




   private void LoadLocalConfigurations()
   {
      String language = settings.getString(Constants.PREF_LOCAL,"");
      if (!language.equalsIgnoreCase(""))
      {
         if (language.equalsIgnoreCase("English"))
            LanguageConfig.setLocale(getApplicationContext(),"en");
         else
            LanguageConfig.setLocale(getApplicationContext(),"ms");

         startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
         finish();
      }
   }

   private void saveCurrentLocation() {

      mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         mFusedLocationClient.getLastLocation()
                 .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                       // Got last known location. In some rare situations this can be null.
                       if (location != null) {
                          // ...

                          SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                          SharedPreferences.Editor editor = pref.edit();
                          editor.putString(Constants.PREFS_USER_LAT, String.valueOf(location.getLatitude()));
                          editor.putString(Constants.PREFS_USER_LNG, String.valueOf(location.getLongitude()));
                          editor.apply();
                       }
                    }
                 });
      }
   }

   //asking for persmission from user (Marshmallow)
   private  void AskPermissions() {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
              != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
              != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                 Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      switch (requestCode) {
         case 1: {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               saveCurrentLocation();
            } else {
               AskPermissions();
            }
         }
      }
   }

   //This method leads you to the alert dialog box.
   void checkGps() {
      locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

      if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         showGPSDisabledAlertToUser();
      }
   }

   //This method configures the Alert Dialog box.
   private void showGPSDisabledAlertToUser() {
      try {
         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
         alertDialogBuilder.setMessage(R.string.gps_alert)
                 .setCancelable(false)
                 .setPositiveButton(R.string.enable_gps,
                         new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               Intent callGPSSettingIntent = new Intent(
                                       android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                               startActivity(callGPSSettingIntent);
                            }
                         });
         alertDialogBuilder.setNegativeButton(R.string.cancel,
                 new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                    }
                 });
         AlertDialog alert = alertDialogBuilder.create();
         alert.show();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void onClick(View view) {

      switch (view.getId()) {

         case R.id.btnForgotPassword:
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            break;

         case R.id.email_sign_in_button:
            attemptLogin();
            break;

         case R.id.btnSignUp:
            startActivity(new Intent(LoginActivity.this, SignUp_Two.class));
            break;

         case R.id.checkBoxRemember:

            String username = mUsernameView.getText().toString();
            String password = mPasswordView.getText().toString();

            // Store values at the time of the login attempt.
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            if (remember.isChecked()) {
               editor.putString(Constants.PREFS_USER_EMAIL, username);
               editor.putString(Constants.PREFS_USER_PASSWORD, password);
            } else {
               editor.putString(Constants.PREFS_USER_EMAIL, "");
               editor.putString(Constants.PREFS_USER_PASSWORD, "");
            }

            editor.apply();

            break;
      }
   }

   private void showDialog(){
      progressDialog = new ProgressDialog(LoginActivity.this);
      progressDialog.setMessage("Please wait...");
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }
   /**
    * Attempts to sign in or register the account specified by the login form.
    * If there are form errors (invalid email, missing fields, etc.), the
    * errors are presented and no actual login attempt is made.
    */
   private void attemptLogin() {


     // startActivity(new Intent(this,DrawerActivity.class));
      // Reset errors.
      mUsernameView.setError(null);
      mPasswordView.setError(null);

      // Store values at the time of the login attempt.
      String username = mUsernameView.getText().toString();
      String password = mPasswordView.getText().toString();


      try
      {
         new FragmentSettings(LoginActivity.this).setUserPassword(password);
      }
      catch (Exception e)
      {
         Log.e("pass save "," error "+e.getMessage());
      }


      boolean cancel = false;
      View focusView = null;

      // Check for a valid password, if the user entered one.
      if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
         mPasswordView.setError(getString(R.string.error_invalid_password));
         focusView = mPasswordView;
         cancel = true;
      }

      // Check for a valid email address.
      if (TextUtils.isEmpty(username)) {
         mUsernameView.setError(getString(R.string.error_field_required));
         focusView = mUsernameView;
         cancel = true;
      }

      if (!isEmailValid(username)) {
         mUsernameView.setError(getString(R.string.error_invalid_email));
         focusView = mUsernameView;
         cancel = true;
      }

      if (cancel) {
         // There was an error; don't attempt login and focus the first
         // form field with an error.
         focusView.requestFocus();
      } else {
         // Show a progress spinner, and kick off a background task to
         // perform the user login attempt.

         // Store values at the time of the login attempt.
         SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
         SharedPreferences.Editor editor_ = settings.edit();
         editor_.commit();

         if (remember.isChecked()) {
            editor_.putString(Constants.PREFS_USER_EMAIL, username);
            editor_.putString(Constants.PREFS_USER_PASSWORD, password);
         } else {
            editor_.putString(Constants.PREFS_USER_EMAIL, "");
            editor_.putString(Constants.PREFS_USER_PASSWORD, "");
         }
         editor_.apply();

         loginApi(username,password);

       /*  showDialog();
         MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
         map.add("email", username);
         map.add("password", password);
         map.add("device_info", token);
         restServiceClient.callService(this, Constants.Host_Address + "login", MOLoginResponse.class, "POST", map, true);
    */  }
   }


   private ProgressDialog progressDialog;
   AsyncHttpClient httpClient = new AsyncHttpClient();
   private void loginApi(final String user_name,final String password)
   {
      httpClient.setConnectTimeout(40000);
      getDeviceInfo();
      RequestParams params = new RequestParams();
      params.add("login_id", user_name);
      params.add("password", password);
      params.add("gcm_id", token);

      Log.e("gcm_id",token);

      Log.e("url",Constants.Host_Address + "members/login");

      httpClient.post(LoginActivity.this, Constants.Host_Address + "members/login", params, new AsyncHttpResponseHandler() {


         @Override
         public void onStart() {
            super.onStart();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Signing In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
         }

         @Override
         public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            if (progressDialog!=null)
               progressDialog.dismiss();
               try {
               String response = new String(responseBody);
               Log.e("response",response);

                  parseResponseLoginApi(response);
            }
            catch (Exception e)
            {
               Log.e("error",e.getMessage());
            }
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            try {
               String response = new String(responseBody);
               Log.e("response",response);
               UtilsManager.showAlertMessage(LoginActivity.this,"","Unable to Login. Server Error");
            }
            catch (Exception e)
            {
               Log.e("error",e.getMessage());
            }
         }
      });

   }

   private void parseResponseLoginApi(String response) {

      String message = "";
      try
      {
         JSONObject object = new JSONObject(response);
         message = object.getString("message");
         JSONArray data = object.getJSONArray("data");

         JSONObject user_object = data.getJSONObject(0);

         String group = user_object.getString("group");
         String id = user_object.getString("member_id");
         String name = user_object.getString("member_name");
         String display_name = user_object.getString("member_display_name");
         String mobile = user_object.getString("member_mobile");
         String email = user_object.getString("member_email");
         String passport = user_object.getString("member_ic_passport");
         String dob = user_object.getString("member_dob");
         String address = user_object.getString("member_address");
         String image = user_object.getString("member_full_img");
         String profile_img = user_object.getString("profile_img");
         String thumb_image = user_object.getString("member_thumb_img");
         String date_registred = user_object.getString("member_date_registered");
         String lat = user_object.getString("member_lat");
         String lng = user_object.getString("member_lng");
         String access_token = user_object.getString("token");
         String document_uploaded = user_object.getString("document_uploaded");
         String can_login = user_object.getString("can_login");


         SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
         SharedPreferences.Editor editor = settings.edit();

         editor.putString(Constants.PREFS_USER_GROUP,group);
         editor.putString(Constants.PREFS_USER_NAME,name);
         editor.putString(Constants.PREFS_USER_EMAIL,email);
         editor.putString(Constants.PREFS_USER_MOBILE,mobile);
         editor.putString(Constants.PREFS_USER_PASSWORD,passport);
         editor.putString(Constants.PREFS_USER_DOB,dob);
         editor.putString(Constants.PREFS_USER_ADDRESS,address);
         editor.putString(Constants.PREFS_USER_IMAGE,image);
         editor.putString(Constants.PREFS_USER_ID,id);
         editor.putString(Constants.PREFS_CUSTOMER_ID,id);

         editor.putString(Constants.APPROVED,can_login);

         CreateFireBaseNode(id,name);

         if (can_login.equalsIgnoreCase("yes"))
            Constants.can_login = true;
         else
            Constants.can_login = false;

         if (gpsTracker.canGetLocation())
         {
            lat = gpsTracker.getLatitude() + "";
            lng = gpsTracker.getLongitude() + "";
         }


         editor.putString(Constants.PREFS_USER_DATE,date_registred);
         editor.putString(Constants.PREFS_USER_LAT,lat);
         editor.putString(Constants.PREFS_USER_LNG,lng);
         editor.putString(Constants.PREFS_PROFILE_IMG,profile_img);
         editor.putString(Constants.PREFS_ACCESS_TOKEN,access_token);
         editor.putString(Constants.PREFS_USER_PASSWORD,mPasswordView.getText().toString().trim());

          editor.putBoolean(Constants.PREFS_LOGIN_STATE,true);
          editor.apply();

         if (can_login.equalsIgnoreCase("Yes"))
         {
            startActivity(new Intent(LoginActivity.this, DrawerActivity.class)
                    .putExtra(Constants.APPROVED,"yes")
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
         }
         else
         {
            startActivity(new Intent(LoginActivity.this, DrawerActivity.class)
                    .putExtra(Constants.APPROVED,"no")
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
         }

       //  startActivity(new Intent(LoginActivity.this, Select_Services_Activity.class));

       /*  if (document_uploaded.equalsIgnoreCase("yes") && can_login.equalsIgnoreCase("yes")) {
            startActivity(new Intent(LoginActivity.this, DrawerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
         }
         else if (!document_uploaded.equalsIgnoreCase("yes"))
         {
            startActivity(new Intent(LoginActivity.this,UploadedDocumentsScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
         }
         else if (!document_uploaded.equalsIgnoreCase("yes") && !can_login.equalsIgnoreCase("yes"))
         {
            startActivity(new Intent(LoginActivity.this,AdminApprovalMessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
         }*/

      }
      catch (Exception e)
      {
         e.printStackTrace();
         UtilsManager.showAlertMessage(LoginActivity.this,"",message);
      }
   }

   private void CreateFireBaseNode(String id,String mem_name)
   {
      getLastLocation(id,mem_name);

   }

   public void getLastLocation(final String mem_id,final String mem_name) {
      // Get last known recent location using new Google Play Services SDK (v11+)
      FusedLocationProviderClient locationClient = getFusedLocationProviderClient(LoginActivity.this);

      if (ActivityCompat.checkSelfPermission(LoginActivity.this,
              Manifest.permission.ACCESS_FINE_LOCATION) !=
              PackageManager.PERMISSION_GRANTED &&
              ActivityCompat.checkSelfPermission(LoginActivity.this,
                      Manifest.permission.ACCESS_COARSE_LOCATION) !=
                      PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         return;
      }
      locationClient.getLastLocation()
              .addOnSuccessListener(new OnSuccessListener<Location>() {
                 @Override
                 public void onSuccess(Location loc) {
                    // GPS location can be null if GPS is switched off
                    try {
                       if (loc != null) {
                          final MemberLocationObject member = new MemberLocationObject(mem_id,mem_name, "driver",loc.getLatitude()+ "", loc.getLongitude() + "");
                          member.setCurrent_job("0");
                          String key = mem_id + "_member";
                          if (!mem_id.equalsIgnoreCase(""))
                             FirebaseDatabase.getInstance().getReference().child("members").child(key).setValue(member);
                       }
                    }
                    catch (Exception e)
                    {
                       e.printStackTrace();
                    }
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                    e.printStackTrace();
                 }
              });
   }

   /**
    * Email Validation
    * @param email Received email
    * @return false or true
    */
   private boolean isEmailValid(String email) {
      Pattern pattern = Patterns.EMAIL_ADDRESS;
      return pattern.matcher(email).matches();
   }

   private boolean isPasswordValid(String password) {
      //TODO: Replace this with your own logic
      return password.length() >= 1;
   }

   @Override
      public void update(Observable observable, Object data) {

      BaseModel baseModel = ((BaseModel) data);

      if(baseModel instanceof ErrorModel){
         progressDialog.dismiss();
         UtilsManager.showAlertMessage(this, "Sorry", "Something went wrong! please try again");
         Log.d(TAG, ((ErrorModel) baseModel).getException());
      } else {

         if (baseModel instanceof MOLoginResponse) {

            loginResponse = ((MOLoginResponse) baseModel);

            progressDialog.dismiss();

            switch (loginResponse.getErrorCode()) {

               case ErrorCodes.NO_ERROR:

                  SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                  SharedPreferences.Editor editor = settings.edit();
                                   editor.putString(Constants.PREFS_ACCESS_TOKEN, loginResponse.getToken());
                  editor.putString(Constants.PREFS_CUSTOMER_ID, loginResponse.getCustomer_id());
                  editor.putString(Constants.PREFS_USER_NAME, loginResponse.getName());
                  editor.putString(Constants.PREFS_USER_EMAIL, mUsernameView.getText().toString());

                  editor.putString(Constants.PREFS_USER_IMAGE, loginResponse.getCustomer_full_img());
                  editor.putString(Constants.PREFS_USER_MOBILE, loginResponse.getMobile());

                  editor.putString(Constants.PREFS_SOS_STATUS, loginResponse.getSos_enable_disable());
                  editor.putString(Constants.PREFS_PROFILE_IMG, loginResponse.getProfile_img());
                  new FragmentSettings(LoginActivity.this)
                          .setValue(SOS_ENABLE_DISABLE_KEY,loginResponse.getSos_enable_disable());

                  String password = mPasswordView.getText().toString();

                  editor.putString(Constants.PREFS_USER_PASSWORD, password);

                  editor.putBoolean(Constants.PREFS_LOGIN_STATE, true);
                  editor.apply();

                  startActivity(new Intent(LoginActivity.this, HomePage.class));
                  finish();

                  String check_key=settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
                  Log.e("check_key",check_key);

                  String check_img=settings.getString(Constants.PREFS_USER_IMAGE, "");
                  Log.e("check_img",check_img);
                  break;

               case ErrorCodes.ACCOUNT_NOT_VERIFIED:

                  AlertDialog.Builder builder = new AlertDialog.Builder(this);
                  builder.setMessage(loginResponse.getMessage());
                  builder.setNegativeButton("Cancel", null);
                  builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                        Intent verificationIntent = new Intent(LoginActivity.this, VerificationActivity.class);
                        verificationIntent.putExtra("email", mUsernameView.getText().toString());
                        startActivity(verificationIntent);
                     }
                  });
                  builder.show();

                  break;

               default:

                  UtilsManager.showAlertMessage(this, "", loginResponse.getMessage());

                  break;
            }
         }
      }
   }
   private String token = "token";
   private void getDeviceInfo() {
      try {

         SharedPreferences sharedPreferences = getApplicationContext().
                 getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
         token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
         // token(token);
         Log.e("token", token);

         if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
               Object value = getIntent().getExtras().get(key);
               Log.d("DEVICE INFO FOUND", "Key: " + key + " Value: " + value);
               //   FcmMessagingService.sendNotification( value.toString());
            }
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
         Log.e("Error getting Key",e.getMessage());
      }
   }

   @Override
   public void onBackPressed() {

   }


   @Override
   protected void attachBaseContext(Context base) {
      super.attachBaseContext(updateBaseContextLocale(base));
   }


   private Context updateBaseContextLocale(Context context) {
      // SharedPreferences sharedPreferences = context.getSharedPreferences(,MODE_PRIVATE);
       SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
       String language = settings.getString(Constants.PREF_LOCAL,"en");//sharedPreferences.getString(Constants.LANGUAGE,Locale.getDefault().getLanguage());//.getSavedLanguage(); // Helper method to get saved language from SharedPreferences
      Locale locale = new Locale(language);
      Locale.setDefault(locale);

      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
         return updateResourcesLocale(context, locale);
      }

      return updateResourcesLocaleLegacy(context, locale);
   }

   @TargetApi(Build.VERSION_CODES.N_MR1)
   private Context updateResourcesLocale(Context context, Locale locale) {
      Configuration configuration = new Configuration(context.getResources().getConfiguration());
      configuration.setLocale(locale);
      return context.createConfigurationContext(configuration);
   }

   @SuppressWarnings("deprecation")
   private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
      Resources resources = context.getResources();
      Configuration configuration = resources.getConfiguration();
      configuration.locale = locale;
      resources.updateConfiguration(configuration, resources.getDisplayMetrics());
      return context;
   }

   @Override
   public void applyOverrideConfiguration(Configuration overrideConfiguration) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
         // update overrideConfiguration with your locale
         // setLocale(overrideConfiguration); // you will need to implement this

         createConfigurationContext(overrideConfiguration);
      }
      super.applyOverrideConfiguration(overrideConfiguration);
   }


    @Override
    protected void onResume() {
        super.onResume();
        updateBaseContextLocale(this);
    }
}

