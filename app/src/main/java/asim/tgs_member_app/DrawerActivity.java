package asim.tgs_member_app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.core.view.GravityCompat;
import asim.tgs_member_app.adapters.ExpandableSideMenu;
import asim.tgs_member_app.finance.FinanceScreen;
import asim.tgs_member_app.fragments.Completed_Jobs;
import asim.tgs_member_app.fragments.DashBoard_Frame;
import asim.tgs_member_app.fragments.DashboardUI;
import asim.tgs_member_app.fragments.DummyFrame;
import asim.tgs_member_app.fragments.Suggested_Jobs;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.models.PhoneContact;
import asim.tgs_member_app.service.LocationListnerServices;
import asim.tgs_member_app.sos.SOS_Settings_PREFS;
import asim.tgs_member_app.sos.SoS_Call_Screen;
import asim.tgs_member_app.utils.NotifyUpdates;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DrawerActivity extends AppCompatActivity implements NotifyUpdates{
    private NavigationView navigationView;
    private androidx.drawerlayout.widget.DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private androidx.appcompat.widget.Toolbar toolbar;
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
    ExpandableListView side_menu_list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sos_btn_icon:
                //do something
                try {
                    if (list.size() > 0) {
                        startActivity(new Intent(context, SoS_Call_Screen.class));
                    } else {
                        startActivity(new Intent(context, SOS_Settings_Screen.class));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                  break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    ArrayList<PhoneContact> list = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        toolbar =  findViewById(R.id.toolbar);
        side_menu_list = findViewById(R.id.expandable_menu);
        list = new SOS_Settings_PREFS(this).getSOSPhone(Constants.USER_SOS_PHONE);

        context = this;
        loadUserPermissions();

        setSupportActionBar(toolbar);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mHandler = new Handler();

        drawer =  findViewById(R.id.drawer_layout);

     //   navigationView = (NavigationView) findViewById(R.id.nav_view);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        mem_id = settings.getString(Constants.PREFS_USER_ID, "");
        settings.edit().putBoolean(Constants.PREFS_USER_ACTIVE,true).apply();
        final String image = settings.getString(Constants.PREFS_USER_IMAGE, "");

        if (getIntent().hasExtra("index"))
            dashboard_tab_index = 0;//getIntent().getIntExtra("index",0);

        //SAVE DEVICE INFO ON SERVER
        getDeviceInfo();

        // Navigation view header
       // navHeader = navigationView.getHeaderView(0);
        TextView name = (TextView)findViewById(R.id.txtName);
        final CircleImageView imageView = (CircleImageView) findViewById(R.id.userProfilePic);
        name.setText(mem_name);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        Picasso.with(DrawerActivity.this).load(image).into(imageView);
        progressBar.setVisibility(View.GONE);

        androidx.appcompat.app.ActionBarDrawerToggle actionBarDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(DrawerActivity.this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // load toolbar titles from string resources

        // initializing navigation menu


        if (approved.equalsIgnoreCase("no"))
        {
          /*  navigationView.getMenu().removeItem(R.id.nav_dashboard);
            navigationView.getMenu().removeItem(R.id.suggested_jobs);
            navigationView.getMenu().removeItem(R.id.completed_jobs);
            navigationView.getMenu().removeItem(R.id.my_notifications);
            navigationView.getMenu().removeItem(R.id.my_notifications);*/

            PrepareMenu(false);

            ExpandableSideMenu sideMenuAdapter = new ExpandableSideMenu(DrawerActivity.this, _listDataHeader, _listDataChild);
            sideMenuAdapter.setApproved(false);
            side_menu_list.setAdapter(sideMenuAdapter);

            side_menu_list.setOnChildClickListener(onUnApprovedChildClickListener);
            side_menu_list.setOnGroupClickListener(onUnApprovedGroupClickListner);

        }
        else
        {
            PrepareMenu(true);

            ExpandableSideMenu sideMenuAdapter = new ExpandableSideMenu(DrawerActivity.this, _listDataHeader, _listDataChild);
            sideMenuAdapter.setApproved(true);
            side_menu_list.setAdapter(sideMenuAdapter);
            side_menu_list.setOnChildClickListener(onChildClickListener);
            side_menu_list.setOnGroupClickListener(onGroupClickListener);
        }

        //setUpNavigationView();



        Bundle bundle = new Bundle();
        bundle.putInt("index",dashboard_tab_index);
        DashboardUI dashBoard_frame = new DashboardUI();
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
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    private void PrepareMenu(boolean isApproved) {

        if (!isApproved)
        {
            _listDataHeader = new ArrayList<>();
            _listDataChild = new HashMap<>();

           /* _listDataHeader.add("Dashboard");
            _listDataHeader.add("Suggested Jobs");
            _listDataHeader.add("Completed Jobs");*/
            //  _listDataHeader.add("Tracking ETA");
            _listDataHeader.add("My Profile");
            /*_listDataHeader.add("Notifications");*/
            _listDataHeader.add("Languages");
            _listDataHeader.add("Logout");

      /*  ArrayList<String> my_orders = new ArrayList<>();
        my_orders.add("Confirmed Orders");
        my_orders.add("My Cart");
        my_orders.add("Scheduled Jobs");

        ArrayList<String> settings = new ArrayList<>();
        settings.add("Edit Profile");
        settings.add("SOS Settings");*/
            ArrayList<String> settings = new ArrayList<>();
            settings.add("Edit profile");
            settings.add("Allowed services");
            settings.add("Documents");
           // settings.add("Bank Info");
            _listDataChild.put(_listDataHeader.get(0), settings);
            _listDataChild.put(_listDataHeader.get(1), new ArrayList<String>());
            _listDataChild.put(_listDataHeader.get(2), new ArrayList<String>());

            // _listDataChild.put(_listDataHeader.get(3), new ArrayList<String>());
          /*  _listDataChild.put(_listDataHeader.get(5), new ArrayList<String>());
            _listDataChild.put(_listDataHeader.get(6), new ArrayList<String>());
            */
            return;
        }
        _listDataHeader = new ArrayList<>();
        _listDataChild = new HashMap<>();

        _listDataHeader.add("Dashboard");
        _listDataHeader.add("Suggested Jobs");
        _listDataHeader.add("Completed Jobs");
        _listDataHeader.add("Finance");
        _listDataHeader.add("My Profile");
        _listDataHeader.add("Notifications");
        _listDataHeader.add("Languages");
        _listDataHeader.add("Logout");

      /*  ArrayList<String> my_orders = new ArrayList<>();
        my_orders.add("Confirmed Orders");
        my_orders.add("My Cart");
        my_orders.add("Scheduled Jobs");

        ArrayList<String> settings = new ArrayList<>();
        settings.add("Edit Profile");
        settings.add("SOS Settings");*/
        ArrayList<String> settings = new ArrayList<>();
        settings.add("Edit profile");
        settings.add("Allowed services");
        settings.add("Documents");
        settings.add("Bank Info");
        settings.add("SOS Settings");
       // settings.add("Bank Info");

        _listDataChild.put(_listDataHeader.get(0), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(1), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(2), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(3), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(4), settings);
        _listDataChild.put(_listDataHeader.get(5), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(6), new ArrayList<String>());
        _listDataChild.put(_listDataHeader.get(7), new ArrayList<String>());


    }


    private ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            if (groupPosition == 4 && childPosition == 0) {
                startActivity(new Intent(DrawerActivity.this, MyProfileActivity.class));
            }
            else if (groupPosition == 4 && childPosition == 1) {
                startActivity(new Intent(DrawerActivity.this, AllowedServices.class));
            } else if (groupPosition == 4 && childPosition == 2) {
                startActivity(new Intent(DrawerActivity.this, UploadedDocumentsScreen.class));
            } else if (groupPosition == 4 && childPosition == 3) {
                startActivity(new Intent(DrawerActivity.this, BankScreen.class).putExtra("edit",true));
            }else if (groupPosition == 4 && childPosition == 4) {
                startActivity(new Intent(DrawerActivity.this, SOS_Settings_Screen.class));
            }


            drawer.closeDrawers();
            return false;
        }
    };

    private ExpandableListView.OnChildClickListener onUnApprovedChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            if (groupPosition == 0 && childPosition == 0) {
                startActivity(new Intent(DrawerActivity.this, MyProfileActivity.class));
            }
            else if (groupPosition == 0 && childPosition == 1) {
                startActivity(new Intent(DrawerActivity.this, AllowedServices.class));
            } else if (groupPosition == 0 && childPosition == 2) {
                startActivity(new Intent(DrawerActivity.this, UploadedDocumentsScreen.class));
            }else if (groupPosition == 0 && childPosition == 3) {
                startActivity(new Intent(DrawerActivity.this, BankScreen.class).putExtra("edit",true));
            }


            drawer.closeDrawers();
            return false;
        }
    };

    private ExpandableListView.OnGroupClickListener onUnApprovedGroupClickListner = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (groupPosition == 0) {
              //  startActivity(new Intent(DrawerActivity.this,MyProfileActivity.class));
            } else if (groupPosition == 1) {
                startActivity(new Intent(DrawerActivity.this, LanguageSelection.class));
                finish();
                drawer.closeDrawer(Gravity.LEFT);
            } else if (groupPosition == 2) {
                Constants.logOutUser(DrawerActivity.this);
                logOutUser();
            } else if (groupPosition == 3) {
               // startActivity(new Intent(DrawerActivity.this, LanguageSelection.class));
            } else if (groupPosition == 4) {
               /* Constants.logOutUser(DrawerActivity.this);
                logOutUser();*/
            }



            return false;
        }
    };

    private ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @SuppressLint("WrongConstant")
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

            if (groupPosition == 0) {
                manager.beginTransaction().replace(R.id.content_main_frame,new DashboardUI()).commit();
                setTitle(R.string.dashboard);
                drawer.closeDrawer(Gravity.START);
            } else if (groupPosition == 1) {
                manager.beginTransaction().replace(R.id.content_main_frame,new Suggested_Jobs()).commit();
                setTitle(R.string.suggested_job);
                drawer.closeDrawer(Gravity.START);
            } else if (groupPosition == 2) {
                manager.beginTransaction().replace(R.id.content_main_frame,new Completed_Jobs()).commit();
                setTitle(R.string.completed_jobs);
                drawer.closeDrawer(Gravity.START);
            }else if (groupPosition == 3) {
                startActivity(new Intent(DrawerActivity.this,FinanceScreen.class));
                drawer.closeDrawer(Gravity.START);
            } else if (groupPosition == 4) {
                //startActivity(new Intent(DrawerActivity.this,MyProfileActivity.class));
            } else if (groupPosition == 5) {
                startActivity(new Intent(DrawerActivity.this,NotificationListActivity.class));
                drawer.closeDrawer(Gravity.START);
            } else if (groupPosition == 6) {
                startActivity(new Intent(DrawerActivity.this, LanguageSelection.class));
                drawer.closeDrawer(Gravity.START);
            } else if (groupPosition == 7) {
               // startActivity(new Intent(DrawerActivity.this, UploadedDocumentsScreen.class));
                Constants.logOutUser(DrawerActivity.this);
                logOutUser();
            }
           /* else if (groupPosition == 8) {
                startActivity(new Intent(DrawerActivity.this, LanguageSelection.class));
            }
            else if (groupPosition == 9) {
                Constants.logOutUser(DrawerActivity.this);
                logOutUser();
            }*/

            return false;
        }
    };

    private void loadUserPermissions() {
        SharedPreferences sh_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String login_status = sh_prefs.getString(Constants.APPROVED,"yes");
        approved = login_status;
        if (login_status.equalsIgnoreCase("no"))
            Constants.can_login = false;
        else
            Constants.can_login = true;
    }

    androidx.fragment.app.FragmentManager manager = getSupportFragmentManager();
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
                drawer.closeDrawer(Gravity.LEFT);
                //Checking if the item is in checked state or not, if not make it in checked state

                return true;
            }
        });


        androidx.appcompat.app.ActionBarDrawerToggle actionBarDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

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
            updateBaseContextLocale(this);
            checkGps();
            refreshData();
            startLocationService();
            if (Constants.DIRECTION_API_KEY.equalsIgnoreCase(""))
                getDirectionKey();

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

        TextView name =  findViewById(R.id.txtName);
        CircleImageView imageView =  findViewById(R.id.userProfilePic);
        name.setText(mem_name);
        Picasso.with(DrawerActivity.this).load(image).into(imageView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    @Override
    public void onJobAccepted(int code) {
        if (manager==null)
            manager = getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putInt("index",1);
        DashboardUI dashboardUI = new DashboardUI();
        dashboardUI.setArguments(bundle);
        manager.beginTransaction().replace(R.id.content_main_frame,dashboardUI).commit();
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService(new Intent(DrawerActivity.this,LocationListnerServices.class));
            } else {
                startForegroundService(new Intent(DrawerActivity.this,LocationListnerServices.class));
            }

            startService(new Intent(DrawerActivity.this,LocationListnerServices.class));
        }
    }

    public void getDirectionKey()
    {

        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.get(DrawerActivity.this, Constants.Host_Address + "customers/get_direction_key/tgs_appkey_amin/member", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Constants.DIRECTION_API_KEY = jsonObject.getString("data");
                    Log.e("KEY_FOUND",Constants.DIRECTION_API_KEY);
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
                    String response = new String(responseBody);
                    Log.e("DIR_KEY_REQ_FAIL",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }


    private Context updateBaseContextLocale(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String language = settings.getString(Constants.PREF_LOCAL,"en");//LocalHelper.getLanguage(context);//sharedPreferences.getString(Constants.LANGUAGE,Locale.getDefault().getLanguage());//.getSavedLanguage(); // Helper method to get saved language from SharedPreferences
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


}
