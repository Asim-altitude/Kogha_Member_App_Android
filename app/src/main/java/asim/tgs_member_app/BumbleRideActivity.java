package asim.tgs_member_app;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.chat.FireBaseChatHead;
import asim.tgs_member_app.fragments.BumbleRideJobMap;
import asim.tgs_member_app.fragments.DriverReachedCallback;
import asim.tgs_member_app.models.BumblePayment;
import asim.tgs_member_app.models.BumbleRideInformation;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.LatLong;
import asim.tgs_member_app.models.Member;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.models.Ride;
import asim.tgs_member_app.models.RideStatus;
import asim.tgs_member_app.utils.BumbleRidePrefs;
import asim.tgs_member_app.utils.DistanceListner;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import me.shaohui.bottomdialog.BottomDialog;


public class BumbleRideActivity extends AppCompatActivity implements DriverReachedCallback,DistanceListner {

    FirebaseDatabase firebaseDatabase;
    private String member_id,ride_id;
    private Button reached,start_btn;
    private TextView cancel_ride_btn,end_ride_btn;

    public static final String MOVING  = "0";
    public static final String REACHED = "1";
    public static final String STARTED = "2";
    public static final String CANCELLED = "3";
    public static final String PAYMENT = "5";
    public static final String RECEIVE = "6";
    public static final String CASH = "10";
    public static final String PAID = "11";

    BumbleRidePrefs bumbleRidePrefs;
    private void loadCurrentRide() {
        bumbleRidePrefs = new BumbleRidePrefs(BumbleRideActivity.this);
        bum_info = bumbleRidePrefs.getCurrentBumbleInfo();
        if (bum_info != null)
            FirebaseDatabase.getInstance().getReference().child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id).setValue(bum_info);
        else {
            BumbleRideInformation bumbleRideInformation = new BumbleRideInformation();
            bumbleRideInformation.setCustomer_id(customer_id);
            bumbleRideInformation.setOrder_id(order_id);
            bumbleRideInformation.setMember_id(member_id);
            bumbleRideInformation.setRide_status("pending");
            bumbleRidePrefs.saveBumbleRide(bumbleRideInformation);

            FirebaseDatabase.getInstance().getReference().child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id).setValue(bumbleRideInformation);

        }
    }

    TextView fare_text,customer_name,customer_phone;
    CircleImageView customer_image;
    Button payment_btn;
    LinearLayout chat_layout,contact_layout;
    private String cust_imagePath,cust_name,cust_mobile;

    LocationManager locationManager;
    LinearLayout riderLayout,payment_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bumble_ride);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sha_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        order_id = sha_prefs.getString(Constants.ORDER_ID,"0");
        customer_id = sha_prefs.getString(Constants.KOGHA_CUSTOMER_ID,"20");
        member_id = sha_prefs.getString(Constants.PREFS_USER_ID,"117");
        cust_name = getIntent().getStringExtra("customer_name");
        cust_imagePath = getIntent().getStringExtra("customer_image");
        total = getIntent().getStringExtra("mem_share");
        cust_mobile = getIntent().getStringExtra("cust_mobile");

        chat_id = FireBaseChatHead.getUniqueChatId(member_id,customer_id,order_id);
        ride_id = FireBaseChatHead.getUniqueRideId(member_id,customer_id,order_id);

        reached = findViewById(R.id.reached_btn);
        start_btn = findViewById(R.id.start_job_btn);
        cancel_ride_btn = findViewById(R.id.cancel_ride_btn);
        end_ride_btn = findViewById(R.id.end_ride_btn);
        payment_layout = findViewById(R.id.payment_layout);


        cancel_ride_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* */
               CancellationConfirmation();
            }
        });

      /*  customer_name = findViewById(R.id.customer_name);
        customer_image = findViewById(R.id.customer_image);
        customer_phone = findViewById(R.id.customer_contact);

        customer_phone.setText(cust_mobile);
        customer_name.setText(cust_name);
        Log.e("image_path",Constants.Customer_Image_BASE_PATH+cust_imagePath);
        Glide.with(BumbleRideActivity.this).load(Constants.Customer_Image_BASE_PATH+cust_imagePath).into(customer_image);

        chat_layout = findViewById(R.id.driver_chat_layout);
        contact_layout = findViewById(R.id.driver_contact_layout);

        contact_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+customer_phone.getText().toString().trim()));
                startActivity(intent);
            }
        });

        chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BumbleRideActivity.this, ChatActivity.class);
                intent.putExtra("chat_id",chat_id);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
            }
        });
*/

        showCustomerInfo();
        reached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(REACHED);
                reached.setVisibility(View.GONE);
                start_btn.setVisibility(View.VISIBLE);
                showReachedBtn  = true;

            }
        });

        end_ride_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  showPaymentDialog();
                JobEndConfirmation();
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(STARTED);
                start_btn.setVisibility(View.GONE);
                cancel_ride_btn.setVisibility(View.GONE);
                end_ride_btn.setVisibility(View.VISIBLE);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        ShowLocations();
        getSettings();

        showLoadingView();
        PayementListner();

    }

    Dialog cancel_job_prompt;
    private void CancellationConfirmation()
    {
        cancel_job_prompt = new Dialog(BumbleRideActivity.this);
        cancel_job_prompt.setContentView(R.layout.cancel_prompt_driver);
        cancel_job_prompt.setCanceledOnTouchOutside(true);
        cancel_job_prompt.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cancel = (TextView) cancel_job_prompt.findViewById(R.id.do_not_cancel);
        TextView confirm = (TextView) cancel_job_prompt.findViewById(R.id.cancel_booking);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_job_prompt.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelOrderApi(order_id);
                upDateRideStatus(CANCELLED);
                cancel_job_prompt.dismiss();
            }
        });

        cancel_job_prompt.show();
    }


    Dialog end_job_prompt;

    private void JobEndConfirmation()
    {
        end_job_prompt = new Dialog(BumbleRideActivity.this);
        end_job_prompt.setContentView(R.layout.prompt_end_job_dialog);
        end_job_prompt.setCanceledOnTouchOutside(true);
        end_job_prompt.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cancel = (TextView) end_job_prompt.findViewById(R.id.do_not_end);
        TextView confirm = (TextView) end_job_prompt.findViewById(R.id.end_ride);

        ImageView close_btn = end_job_prompt.findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_job_prompt.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_job_prompt.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endJobApi();
                end_job_prompt.dismiss();
            }
        });

        end_job_prompt.show();
    }


    BumbleRideInformation bum_info = null;
    private void SetUpRideStateListner(FirebaseDatabase firebaseDatabase)
    {
        ride_id = FireBaseChatHead.getUniqueRideId(member_id,customer_id,order_id);
        firebaseDatabase.getReference().child(FireBaseChatHead.BUMBLE_RIDE)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    showReachedBtn = false;
                    bum_info = dataSnapshot.getValue(BumbleRideInformation.class);

                    if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase("finished")) {
                        finish();
                        Toast.makeText(BumbleRideActivity.this,"Job finished",Toast.LENGTH_LONG).show();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CANCELLED)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust"))
                    {
                        //Notification("Ride cancelled","Ride cancelled by customer");
                        showNotificationPopup();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CASH)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust"))
                    {
                        showPaymentDialog(bum_info.getCash_amount());
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(RECEIVE)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust"))
                    {
                       // Toast.makeText(BumbleRideActivity.this,"Customer paid online",Toast.LENGTH_LONG).show();
                        sendResponsedData(order_id,customer_id,total);

                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id))
                        loadCurrentOrder();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    bum_info = dataSnapshot.getValue(BumbleRideInformation.class);
                    showReachedBtn = false;
                    if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase("finished")) {
                        finish();
                        Toast.makeText(BumbleRideActivity.this,"Job finished",Toast.LENGTH_LONG).show();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CANCELLED)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust")
                            )
                    {
                      //  Notification("Ride cancelled","Ride cancelled by customer");
                        showNotificationPopup();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CASH)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust"))
                    {
                        showPaymentDialog(bum_info.getCash_amount());
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(RECEIVE)
                            && bum_info.getUpdated_by().equalsIgnoreCase("cust"))
                    {
                       // Toast.makeText(BumbleRideActivity.this,"Customer paid online",Toast.LENGTH_LONG).show();
                        sendResponsedData(order_id,customer_id,total);

                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id))
                        loadCurrentOrder();
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private String order_id,meet_location,drop_location,chat_id;
    SharedPreferences sha_prefs;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    String customer_id;
    ArrayList<Member> members;
    int state = 1;
    private void getSettings() {
        sha_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String customer_name = sha_prefs.getString(Constants.PREFS_USER_NAME,"");
        String customer_lat = sha_prefs.getString(Constants.PREFS_USER_LAT,"");
        String customer_lon = sha_prefs.getString(Constants.PREFS_USER_LNG,"");
        String customer_image = sha_prefs.getString(Constants.PREFS_USER_IMAGE,"");
        order_id = sha_prefs.getString(Constants.ORDER_ID,"0");

        members = new ArrayList<>();
        asyncHttpClient.setConnectTimeout(30000);
        Log.e("SELECTED_MEMBERS", Constants.Host_Address + "orders/get_selected_members/tgs_appkey_amin/"+ order_id);

        asyncHttpClient.get(BumbleRideActivity.this, Constants.Host_Address + "orders/get_selected_members/tgs_appkey_amin/"+ order_id, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                // showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
                    settings.edit().putString(Constants.CURRENT_JOB_MEMBERS,response).apply();

                    // stopLoading();

                    JSONObject object = new JSONObject(new String(responseBody));
                    JSONArray array = object.getJSONArray("members");

                    members = new ArrayList<>();
                    for (int i=0;i<array.length();i++)
                    {
                        JSONObject member = array.getJSONObject(i);
                        String id = member.getString("id");
                        String name = member.getString("display_name");
                        //  String email = member.getString("email");
                        //  String dob = member.getString("dob");
                        String number = member.getString("mobile_number");
                        String image = member.getString("profile_img");
                        String lat = member.getString("lat");
                        String lon = member.getString("lng");
                        //  String passport = member.getString("ic_passport");

                        Log.e("current order image",image);
                        Member mem_obj = new Member(id,name,"",image,lat,lon,"","","");
                        mem_obj.setMem_phone(number);
                        members.add(mem_obj);

                    }


                    ride_id = FireBaseChatHead.getUniqueRideId(members.get(0).getMem_id(),customer_id,order_id);
                    chat_id = FireBaseChatHead.getUniqueChatId(members.get(0).getMem_id(),customer_id,order_id);
                    SetUpRideStateListner(firebaseDatabase);
                   // loadCurrentRide();
                    loadCurrentOrder();


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    // stopLoading();
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Log.e("response",jsonObject.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    LatLng pickup_lat_lng,destination_lat_lng;

    String total = "";
    private void ShowLocations()
    {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        total = sharedPreferences.getString(Constants.TOTAL, "");
        String pick = sharedPreferences.getString(Constants.MEET_LOCATION, "");
        String destination_ = sharedPreferences.getString(Constants.DESTINATION, "");

        try {
            pickup_lat_lng = Constants.getLocationFromAddress(BumbleRideActivity.this, pick);
            destination_lat_lng = Constants.getLocationFromAddress(BumbleRideActivity.this, destination_);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadMap(int state) {
        try {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            String meet_location = prefs.getString("meet_location", null);

            Ride ride = new Ride();

            RideStatus rideStatus = null;
            if (state == 1)
                rideStatus = RideStatus.STATE_INITIAL;
            else if (state == 2)
                rideStatus = RideStatus.STATE_REACHED;
            else
                rideStatus = RideStatus.STATE_JOB_STARTED;

            ride.setRideStatus(rideStatus);

            ride.setPickup_loc(meet_location);
            ride.setDestination_loc(drop_location);

            ride.setMeet_location(new LatLong(pickup_lat_lng.latitude, pickup_lat_lng.longitude));
            ride.setDrop_location(new LatLong(destination_lat_lng.latitude, destination_lat_lng.longitude));

            Bundle bundle = new Bundle();
            bundle.putString("pick_up", meet_location);
            bundle.putBoolean("job_running", true);
            bundle.putString("order_id", order_id);
            bundle.putInt("bumble", 1);
            bundle.putSerializable("ride", ride);

            BumbleRideJobMap mem_map = new BumbleRideJobMap();
            mem_map.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, mem_map).commit();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }


    }

    boolean isReached = false;
    @Override
    public void onDriverReached()
    {
        try {
           /* if (!isReached) {
                upDateRideStatus("reached");
                isReached = true;
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void upDateRideStatus(String status)
    {
        try {

            BumbleRideInformation bum_info = new BumbleRideInformation();
            bum_info.setCustomer_id(customer_id);
            bum_info.setMember_id(member_id);
            bum_info.setOrder_id(order_id);
            bum_info.setUpdated_by("mem");
            bum_info.setRide_status(status);

            firebaseDatabase.getReference().child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id).setValue(bum_info);

          /*  if (status.equals(PAYMENT))
                UtilsManager.showAlertMessage(BumbleRideActivity.this,"","Wait while customer pays");*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void endJobApi()
    {
        asyncHttpClient.setConnectTimeout(30000);
        asyncHttpClient.get(BumbleRideActivity.this, Constants.Host_Address + "customers/stop_job/" + order_id + "/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                    String response = new String(responseBody);
                    JSONObject object = new JSONObject(response);
                    Log.e("response stop",response);
                    upDateRideStatus("finished");
                    UtilsManager.showAlertMessage(BumbleRideActivity.this,"","Wait while customer pays");
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
                    JSONObject object = new JSONObject(response);
                    Log.e("response stop",response);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    private void loadCurrentOrder()
    {
        asyncHttpClient.setConnectTimeout(20000);

        asyncHttpClient.get(BumbleRideActivity.this, Constants.Host_Address + "customers/my_bumble_ride_job_status/" + order_id + "/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response",js.toString());
                    showReachedBtn = false;
                    JSONObject data = js.getJSONObject("data");
                    customer_id = data.getString("customer_id");
                    member_id = data.getString("member_id");
                    String status = data.getString("status");
                    ride_id = FireBaseChatHead.getUniqueRideId(member_id,customer_id,order_id);
                    chat_id = FireBaseChatHead.getUniqueChatId(member_id,customer_id,order_id);
                    hideLoadingView();
                    if (status.equalsIgnoreCase(MOVING) || status.equalsIgnoreCase(PAID))
                    {
                        loadMap(1);
                        showReachedBtn = true;
                        reached.setVisibility(View.VISIBLE);
                        end_ride_btn.setVisibility(View.GONE);
                        cancel_ride_btn.setVisibility(View.VISIBLE);
                    }

                    if (status.equalsIgnoreCase(REACHED))
                    {

                        loadMap(2);
                        updateOrderStatus(REACHED);
                        //Notification("Driver Reached","Your driver reached on pickup");
                        reached.setVisibility(View.GONE);
                        start_btn.setVisibility(View.VISIBLE);
                        end_ride_btn.setVisibility(View.GONE);
                        cancel_ride_btn.setVisibility(View.VISIBLE);
                    }

                    if (status.equalsIgnoreCase(STARTED))
                    {
                        loadMap(3);

                        reached.setVisibility(View.GONE);
                        start_btn.setVisibility(View.GONE);
                        cancel_ride_btn.setVisibility(View.GONE);
                        end_ride_btn.setVisibility(View.VISIBLE);
                    }

                    if (status.equalsIgnoreCase(CANCELLED))
                    {
                        Notification("Ride Cancelled","Your ride has been cancelled");
                        finish();
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    loadMap(1);
                    showReachedBtn = true;
                    start_btn.setVisibility(View.GONE);
                    end_ride_btn.setVisibility(View.GONE);
                    cancel_ride_btn.setVisibility(View.VISIBLE);
                    Log.e("Inside_Crash",e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response_failure",js.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
            }
        });
    }

    private void updateOrderStatus(final String status)
    {
        asyncHttpClient.setConnectTimeout(20000);

        Log.e("update_status",Constants.Host_Address + "customers/bumble_ride_change_status/" + order_id +"/"+member_id+"/"+status+"/tgs_appkey_amin/"+customer_id);
        asyncHttpClient.get(BumbleRideActivity.this, Constants.Host_Address + "customers/bumble_ride_change_status/" + order_id +"/"+member_id+"/"+status+"/tgs_appkey_amin/"+customer_id, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response",js.toString());
                    BumbleRideInformation bum_info = new BumbleRideInformation();
                    bum_info.setCustomer_id(customer_id);
                    bum_info.setMember_id(member_id);
                    bum_info.setOrder_id(order_id);
                    bum_info.setRide_status(status);
                    bum_info.setUpdated_by("mem");
                    FirebaseDatabase.getInstance().getReference()
                            .child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id)
                            .setValue(bum_info);



                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response_failure",js.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    Dialog bottom_dialog;
    private void showPaymentDialog(String total_amount)
    {
        bottom_dialog = new Dialog(BumbleRideActivity.this);
        bottom_dialog.setContentView(R.layout.payment_acknowledgment_layout);
        bottom_dialog.setCanceledOnTouchOutside(false);
        TextView total_ = bottom_dialog.findViewById(R.id.amount_fare);
        total_.setText("RM"+total_amount);
        Button pay = bottom_dialog.findViewById(R.id.payment_btn);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponsedData(order_id,customer_id,total);
            }
        });

        bottom_dialog.show();
    }

    private void showLoadingView()
    {
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
    }

    private void  hideLoadingView()
    {
        findViewById(R.id.loading_view).setVisibility(View.GONE);
    }

    AsyncHttpClient asHclient = new AsyncHttpClient();
    private void showDialog()
    {
        try {
            progress = new ProgressDialog(BumbleRideActivity.this);
            progress.setMessage("acknowledgment...");
            progress.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void hidDialog()
    {
      progress.dismiss();
    }


    public void sendResponsedData(final  String order_id , final  String amount, final String customer_id) {

        asHclient.setURLEncodingEnabled(false);
        asHclient.setTimeout(60000);


        Log.e("MOL_PAY", Constants.Host_Address + "orders/update_payment_status/tgs_appkey_amin/" + order_id + "/" + customer_id + "/" + amount+"/cash");


        asHclient.get(Constants.Host_Address + "orders/update_payment_status/tgs_appkey_amin/" + order_id + "/" + customer_id + "/" + amount+"/cash", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // Initiated the request
                showDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Successfully got a response
                try {
                    hidDialog();

                    endJobApi();
                    final String responseString = new String(responseBody, "UTF-8");
                    Log.e("transaction_resp", responseString);
                    JSONObject responseJsonObject = new JSONObject(responseString);
                    String status = responseJsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {

                        String error = responseJsonObject.getString("message");
                        Toast.makeText(BumbleRideActivity.this, "Payment successfull", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String error = responseJsonObject.getString("message");

                        Toast.makeText(BumbleRideActivity.this, "" + error, Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                // Response failed :(
//                Log.e("transaction_resp", new String(responseBody));
                hidDialog();

                //   addToModel(responseString);
                // Toast.makeText(MOLPaymentPage.this, "Internet/Domain Issue.Try Again! ", Toast.LENGTH_LONG).show();
                error.printStackTrace(System.out);
            }

            @Override
            public void onRetry(int retryNo) {
                // Request was retried
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)

            }
        });



    }

    private int NOTIFICATION_ID = 101;
    public void Notification(String title,String message)
    {
        Intent  cancel_intent = new Intent(this, BumbleRideActivity.class);

        cancel_intent.putExtra("mem_share",total)
            .putExtra("customer_name",cust_name)
            .putExtra("customer_image",cust_imagePath)
            .putExtra("cust_mobile", cust_mobile);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, cancel_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.kogha_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());

    }

    private boolean showReachedBtn = true;
    @Override
    public void onDistanceUpdate(double meters) {

       /* if (meters<200) {
            if (showReachedBtn) {
                reached.setVisibility(View.VISIBLE);
                showReachedBtn = false;
            }
            else {
                reached.setVisibility(View.GONE);
            }
        }

        if (meters<500 && meters>200) {
            if (showReachedBtn) {
                start_btn.setVisibility(View.VISIBLE);

            }
            else
                start_btn.setVisibility(View.GONE);
        }*/
    }


    private ProgressDialog progress;

    private void showDialog(String message)
    {
        progress = new ProgressDialog(BumbleRideActivity.this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(message);
        progress.show();
    }

    private void hideLoader()
    {
        if (progress!=null)
            if (progress.isShowing())
                progress.dismiss();
    }

    private void CancelOrderApi(String order_number)
    {
        asyncHttpClient.setConnectTimeout(20000);
        Log.e("CANCEL_CURRENT_JOB",Constants.Host_Address + "orders/cancel_current_order/tgs_appkey_amin/" + order_number);
        asyncHttpClient.get(BumbleRideActivity.this, Constants.Host_Address + "orders/cancel_current_order/tgs_appkey_amin/" + order_number, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Cancelling Ride");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    hideLoader();
                    BumbleRideInformation bum_info = new BumbleRideInformation();
                    bum_info.setCustomer_id(customer_id);
                    bum_info.setMember_id(member_id);
                    bum_info.setOrder_id(order_id);
                    bum_info.setRide_status(CANCELLED);
                    bum_info.setUpdated_by("cust");
                    FirebaseDatabase.getInstance().getReference()
                            .child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id)
                            .setValue(bum_info);

                    String response = new String(responseBody);
                    JSONObject object = new JSONObject(response);
                    Log.e("success response",response);
                    finish();
                   /* if (object.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(JobProgressScreen.this, "Your order cancelled successfully", Toast.LENGTH_LONG).show();
                        RemoveCurrentJobDetails();
                        startActivity(new Intent(JobProgressScreen.this,HomePage_.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                    else
                    {
                        UtilsManager.showAlertMessage(JobProgressScreen.this,"",object.getString("message"));
                    }*/

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    hideLoader();
                    String response = new String(responseBody);
                    Log.e("failure response",response);
                    UtilsManager.showAlertMessage(BumbleRideActivity.this,"","Could not cancel order.");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    String current_job = "0";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                boolean isActive_loggedin = settings.getBoolean(Constants.PREFS_USER_ACTIVE,false);

                if (true) {

                    current_job = settings.getString(Constants.CURRENT_JOB,"0");
                    Log.e("location found", location.getLatitude() + "-" + location.getLongitude());
                    final MemberLocationObject member = new MemberLocationObject(member_id, members.get(0).getMem_name(), "driver", location.getLatitude() + "", location.getLongitude() + "");
                    member.setCurrent_job(current_job);

                    String key = member_id + "_member";
                    if (!member_id.equalsIgnoreCase(""))
                        firebaseDatabase.getReference().child("members").child(key).setValue(member);

                    Log.e("location updated for ",  members.get(0).getMem_name());
                    editor = settings.edit();
                    editor.putString(Constants.PREFS_USER_LAT, location.getLatitude() + "");
                    editor.putString(Constants.PREFS_USER_LNG, location.getLongitude() + "");

                    editor.apply();
                }

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
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListenerGPS);
        //  locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListenerGPS);
    }

    void checkGps() {

        try {
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }
            else
            {
                updateUserLocation();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    android.app.AlertDialog alert;

    private Dialog job_cancelled;
    private void showNotificationPopup()
    {
        job_cancelled = new Dialog(BumbleRideActivity.this);
        job_cancelled.setContentView(R.layout.job_cancelled);
        job_cancelled.setCanceledOnTouchOutside(false);

        job_cancelled.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        TextView done = job_cancelled.findViewById(R.id.ok_btn);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        job_cancelled.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showCustomerInfo()
    {
        findViewById(R.id.rider_information_layout).setVisibility(View.VISIBLE);

        TextView estimated_time_reach = (TextView) findViewById(R.id.estimated_time_to_reach);
        TextView status_text = (TextView) findViewById(R.id.bodyguard_status_text);
        TextView bodyguard_rating = (TextView) findViewById(R.id.bodyguard_rating);
        TextView bodyguard_name = (TextView) findViewById(R.id.bodyguard_name);
        CircleImageView bodyguard_image = (CircleImageView) findViewById(R.id.bodyguard_image);

        ImageView call_btn = (ImageView) findViewById(R.id.bodyguard_call_btn);
        ImageView chat_btn = (ImageView) findViewById(R.id.bodyguard_chat_btn);

        bodyguard_name.setText(cust_name);


        Glide.with(BumbleRideActivity.this).load(Constants.Customer_Image_BASE_PATH+cust_imagePath)
                .placeholder(R.drawable.ic_avatar).into(bodyguard_image);

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showChatDialog(driver_name,image);

                Intent intent = new Intent(BumbleRideActivity.this, ChatActivity.class);
                intent.putExtra("chat_id",chat_id);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+cust_mobile));
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    /////////////////LISTEN FOR PAYMENT STATUS OF ORDER//////////////////////////////////

    private void PayementListner()
    {
        FirebaseDatabase.getInstance().getReference().child("payments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();

                if (count==0) {
                    // findViewById(R.id.pay_now).setVisibility(View.VISIBLE);
                    payment_layout.setVisibility(View.VISIBLE);
                }
                else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        BumblePayment bumblePayment = dataSnapshot1.getValue(BumblePayment.class);
                        if (bumblePayment.getOrder_id().equals(order_id)) {
                            if (bumblePayment.getPayment_code().equals("0"))
                                payment_layout.setVisibility(View.VISIBLE);
                            else
                                payment_layout.setVisibility(View.GONE);

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
