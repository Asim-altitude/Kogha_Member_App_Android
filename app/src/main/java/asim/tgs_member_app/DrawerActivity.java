package asim.tgs_member_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.fragments.Completed_Jobs;
import asim.tgs_member_app.fragments.DashBoard_Frame;
import asim.tgs_member_app.fragments.DummyFrame;
import asim.tgs_member_app.fragments.Suggested_Jobs;
import asim.tgs_member_app.fragments.Upcoming_Jobs;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.receiver.LocationChangeReciver;
import asim.tgs_member_app.service.BackgroundLocationService;
import asim.tgs_member_app.service.ChatMessageNotifier;
import asim.tgs_member_app.service.LocationListnerServices;
import asim.tgs_member_app.service.ServiceForMemberLocation;
import asim.tgs_member_app.utils.NotifyUpdates;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DrawerActivity extends AppCompatActivity implements NotifyUpdates{
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
      // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private FirebaseDatabase firebaseDatabase;
    private String mem_name,mem_id;

    private String approved = "yes";

    private int dashboard_tab_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        loadUserPermissions();

        setSupportActionBar(toolbar);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        mem_id = settings.getString(Constants.PREFS_USER_ID, "");
        settings.edit().putBoolean(Constants.PREFS_USER_ACTIVE,true).apply();
        final String image = settings.getString(Constants.PREFS_USER_IMAGE, "");


        if (getIntent().hasExtra("index"))
            dashboard_tab_index = getIntent().getIntExtra("index",0);

        //SAVE DEVICE INFO ON SERVER
        getDeviceInfo();

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        TextView name = (TextView) navHeader.findViewById(R.id.txtName);
        final CircleImageView imageView = (CircleImageView) navHeader.findViewById(R.id.userProfilePic);
        name.setText(mem_name);
        final ProgressBar progressBar = (ProgressBar) navHeader.findViewById(R.id.progress);
        Glide.with(DrawerActivity.this).load(image).override(400,400).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                imageView.setImageDrawable(resource);
                progressBar.setVisibility(View.GONE);
            }
        });

        // load toolbar titles from string resources

        // initializing navigation menu

        if (approved.equalsIgnoreCase("no"))
        {
            navigationView.getMenu().removeItem(R.id.nav_dashboard);
            navigationView.getMenu().removeItem(R.id.suggested_jobs);
            navigationView.getMenu().removeItem(R.id.completed_jobs);
            navigationView.getMenu().removeItem(R.id.my_notifications);
            navigationView.getMenu().removeItem(R.id.my_notifications);
        }

        setUpNavigationView();

        Bundle bundle = new Bundle();
        bundle.putInt("index",dashboard_tab_index);
        DashBoard_Frame dashBoard_frame = new DashBoard_Frame();
        dashBoard_frame.setArguments(bundle);
        if (approved.equalsIgnoreCase("yes")) {
            manager.beginTransaction().replace(R.id.content_main_frame, dashBoard_frame).commit();
        }
        else {
            manager.beginTransaction().replace(R.id.content_main_frame, new DummyFrame()).commit();

        }

        setTitle(R.string.dashboard);

        //startService(new Intent(DrawerActivity.this, BackgroundLocationService.class));

    }

    private void loadUserPermissions() {
        SharedPreferences sh_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String login_status = sh_prefs.getString(Constants.APPROVED,"yes");
        approved = login_status;
        if (login_status.equalsIgnoreCase("no"))
            Constants.can_login = false;
        else
            Constants.can_login = true;
    }

    FragmentManager manager = getSupportFragmentManager();
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_dashboard:
                        manager.beginTransaction().replace(R.id.content_main_frame,new DashBoard_Frame()).commit();
                        setTitle(R.string.dashboard);
                        break;
                    case R.id.my_profile:
                        startActivity(new Intent(DrawerActivity.this,MyProfileActivity.class));
                        break;
                    case R.id.my_notifications:
                        startActivity(new Intent(DrawerActivity.this,NotificationListActivity.class));
                        break;
                    case R.id.suggested_jobs:
                        manager.beginTransaction().replace(R.id.content_main_frame,new Suggested_Jobs()).commit();
                        setTitle(R.string.suggested_job);
                        break;
                    case R.id.completed_jobs:
                        manager.beginTransaction().replace(R.id.content_main_frame,new Completed_Jobs()).commit();
                        setTitle(R.string.completed_jobs);
                        break;
                    case R.id.allowed_services:
                        startActivity(new Intent(DrawerActivity.this, AllowedServices.class));

                        break;
                    case R.id.language:
                        startActivity(new Intent(DrawerActivity.this, LanguageSelection.class));
                        finish();
                        break;
                    case R.id.upload_documnets:
                        startActivity(new Intent(DrawerActivity.this, UploadedDocumentsScreen.class));
                        break;
                   /* case R.id.test_chat:
                        startActivity(new Intent(DrawerActivity.this,ChatActivity.class).putExtra("chat_id","chat_117_20"));
                        break;
               */     case R.id.log_out:
                        Constants.logOutUser(DrawerActivity.this);
                        logOutUser();
                        break;

                    default:
                        navItemIndex = 0;
                }
                drawer.closeDrawer(Gravity.START);
                //Checking if the item is in checked state or not, if not make it in checked state

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    FirebaseDatabase dataBase;
    private void RemoveFireBaseNode() {
        dataBase = FirebaseDatabase.getInstance();
        dataBase.getReference().child("members").child(mem_id+"_member").removeValue();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home


        super.onBackPressed();
    }


    // show or hide the fab
    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                Log.e("location found", location.getLatitude() + "-" + location.getLongitude());
                final MemberLocationObject member = new MemberLocationObject(mem_id,mem_name,"driver",location.getLatitude()+"",location.getLongitude()+"");

                String key = member.getMem_id()+"_member";
                firebaseDatabase.getReference().child("members").child(key).setValue(member);

                Log.e("location updated for ",mem_name);

            } catch (Exception e) {
                Log.e("location error", e.getMessage());
            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    LocationManager locManager;

    private void updateUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListenerGPS);
       // locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListenerGPS);
    }

    void checkGps() {

        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
        else
        {

            //startService(new Intent(DrawerActivity.this,ChatMessageNotifier.class));
        }

    }

    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DrawerActivity.this);
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
        alert = alertDialogBuilder.create();
        alert.show();
    }

    android.app.AlertDialog alert;


    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            checkGps();
            refreshData();
            startLocationService();

        }
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    private void refreshData() {
        SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        mem_id = settings.getString(Constants.PREFS_USER_ID, "");
        String image = settings.getString(Constants.PREFS_USER_IMAGE, "");
        Log.e("image",image);

        navHeader = navigationView.getHeaderView(0);
        TextView name = (TextView) navHeader.findViewById(R.id.txtName);
        CircleImageView imageView = (CircleImageView) navHeader.findViewById(R.id.userProfilePic);
        name.setText(mem_name);
        Glide.with(getApplicationContext()).load(image).into(imageView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }



    @Override
    public void onJobAccepted(int code) {
        if (manager==null)
            manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.content_main_frame,new DashBoard_Frame()).commit();
    }

    @Override
    public void callToFragment() {

    }

    private String token = "token";
    private void getDeviceInfo() {
        try {

            SharedPreferences sharedPreferences = getApplicationContext().
                    getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
            token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
            // token(token);
            Log.e("token", token);

            if (!token.equalsIgnoreCase(""))
                loadMemberData();

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

    SharedPreferences settings;
    String memberID;
    private void loadMemberData()
    {
        settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        memberID = settings.getString(Constants.PREFS_USER_ID, "");
        saveDeviceIDServer(token);

    }


    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void saveDeviceIDServer(String device_info)
    {
        asyncHttpClient.setConnectTimeout(40000);
        Log.e("device id member",Constants.Host_Address + "members/update_member_device_info/"+memberID+"/"+device_info+"/tgs_appkey_amin");
        asyncHttpClient.get(getApplicationContext(), Constants.Host_Address + "members/update_member_device_info/"+memberID+"/"+device_info+"/tgs_appkey_amin", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    String s = new String(responseBody);
                    Log.e("respnse member",s);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    String s = new String(responseBody);
                    Log.e("respnse",s);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    private void logOutUser()
    {
        asyncHttpClient.setConnectTimeout(20000);
        Log.e("url",Constants.Host_Address + "members/logout/" + mem_id+"/tgs_appkey_amin");
        asyncHttpClient.get(DrawerActivity.this, Constants.Host_Address + "members/logout/" + mem_id+"/tgs_appkey_amin", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response",response);
                    RemoveFireBaseNode();
                    if (UtilsManager.isMyServiceRunning(DrawerActivity.this,LocationListnerServices.class))
                        stopService(new Intent(DrawerActivity.this,LocationListnerServices.class));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e("response",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


    private void startLocationService()
    {
        LocationListnerServices loactionService = new LocationListnerServices();
        loactionService.setContext(DrawerActivity.this);
        if (!UtilsManager.isMyServiceRunning(DrawerActivity.this,LocationListnerServices.class)) {
        /*    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService(new Intent(DrawerActivity.this,LocationListnerServices.class));
            } else {
                startForegroundService(new Intent(DrawerActivity.this,LocationListnerServices.class));
            }*/

            startService(new Intent(DrawerActivity.this,LocationListnerServices.class));
        }
    }

}
