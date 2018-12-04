package asim.tgs_member_app.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import asim.tgs_member_app.BumbleRideActivity;
import asim.tgs_member_app.LoginActivity;
import asim.tgs_member_app.R;
import asim.tgs_member_app.fragments.DirectionModules.DirectionFinder;
import asim.tgs_member_app.fragments.DirectionModules.DirectionFinderListener;
import asim.tgs_member_app.fragments.DirectionModules.Route;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MOOrderPlace;
import asim.tgs_member_app.models.Member;
import asim.tgs_member_app.models.MemberLocationObject;
import asim.tgs_member_app.models.Ride;
import asim.tgs_member_app.models.RideStatus;
import asim.tgs_member_app.service.LocationListnerServices;
import asim.tgs_member_app.utils.DIstanceNotifier;
import asim.tgs_member_app.utils.DistanceListner;
import asim.tgs_member_app.utils.GPSTracker;
import asim.tgs_member_app.utils.RideDirectionPointsDB;
import de.hdodenhof.circleimageview.CircleImageView;
import me.shaohui.bottomdialog.BottomDialog;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by Asim Shahzad on 2/18/2018.
 */
public class BumbleRideJobMap extends Fragment implements OnMapReadyCallback,GoogleMap.OnCameraMoveListener,
        DirectionFinderListener,GoogleMap.OnMarkerClickListener {
    public View rootview;
    View mapView;
    LocationManager locationManager;
    public static double longitudeGPS, latitudeGPS;
    private int resource = R.drawable.car;
    private MOOrderPlace orderPlace = new MOOrderPlace();

    public static boolean shouldDraw = false;
    public static boolean show = false;
    public static LatLng start,end;
    public static String start_add,end_add;

    private FirebaseDatabase databasePath;
    private ArrayList<Member> members_saved;


    SharedPreferences settings;
    private void loadCurrentMembers()
    {
        String members_data = settings.getString(Constants.CURRENT_JOB_MEMBERS,"");
        if (!members_data.equalsIgnoreCase(""))
             parseData(members_data);
    }

    private void parseData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.getJSONArray("members");

            members_saved = new ArrayList<>();
            for (int i=0;i<array.length();i++)
            {
                JSONObject member = array.getJSONObject(i);
                String id = member.getString("id");
                String name = member.getString("display_name");
                //  String email = member.getString("email");
                //  String dob = member.getString("dob");
                //  String address = member.getString("address");
                String image = member.getString("profile_img");
                String lat = member.getString("lat");
                String lon = member.getString("lng");
                //  String passport = member.getString("ic_passport");

                Log.e("currentorder_image",image);
                asim.tgs_member_app.models.Member mem_obj = new asim.tgs_member_app.models.Member(id,name,"",image,lat,lon,"","","");
                members_saved.add(mem_obj);

            }
        } catch (Exception e) {

        }
    }


    DriverReachedCallback driverReachedCallback;
    DistanceListner distanceListner;
    DIstanceNotifier dIstanceNotifier;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            driverReachedCallback = (DriverReachedCallback) activity;
            distanceListner = (DistanceListner) activity;
            dIstanceNotifier = (DIstanceNotifier) activity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkGps();
        members = new ArrayList<>();
        member_markers = new ArrayList<>();


    }

    boolean isJobRunning = false;
    private Ride ride = null;
    private TextView location_points;
    private GPSTracker gpsTracker;
    private  boolean location_found = false;
    private void getCurrentLocation()
    {
        if (settings==null)
            settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        String lat =  settings.getString(Constants.PREFS_USER_LAT, "");
        String lon =  settings.getString(Constants.PREFS_USER_LNG, "");

        if (!lat.equals("") && !lon.equals(""))
        {
            latitudeGPS = Double.parseDouble(lat);
            longitudeGPS = Double.parseDouble(lon);

            return;
        }

        Location location;
        gpsTracker = new GPSTracker(getContext());
        if (gpsTracker.canGetLocation())
        {
            location = gpsTracker.getLocation();
            if (location!=null) {
                location_found = true;
                settings.edit().putString(Constants.PREFS_USER_LAT, location.getLatitude() + "").apply();
                settings.edit().putString(Constants.PREFS_USER_LNG, location.getLongitude() + "").apply();

            }
        }
        else
        {
            if (settings==null)
                settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

            String mem_id = settings.getString(Constants.PREFS_USER_ID,"117");
            String mem_name = settings.getString(Constants.PREFS_USER_NAME,"Asim Shahzad");

            getLastLocation(mem_id,mem_name);
        }

    }

    public void getLastLocation(final String mem_id,final String mem_name) {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
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


                                latitudeGPS = loc.getLatitude();
                                longitudeGPS = loc.getLongitude();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_main_map,container ,false);

        location_points = rootview.findViewById(R.id.location_text);
        location_points.setVisibility(View.GONE);
        settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        loadCurrentMembers();
        SupportMapFragment mapfrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);//lower APIS ma getFragmentManager use karna ha
        mapView = mapfrag.getView();
        mapfrag.getMapAsync(this);

        getCurrentLocation();
        try
        {
            isJobRunning = getArguments().getBoolean("job_running");
            ride = (Ride) getArguments().getSerializable("ride");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        databasePath = FirebaseDatabase.getInstance();

        return rootview;
    }

    boolean isZoomed = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    List<MemberLocationObject> members;
    List<Marker> member_markers;
    double distance_member = 0;
    String distance_extension = "KM";
    Marker pickup,destination;
    private void updateMembersLocation()
    {
        databasePath.getReference().child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("added","aa");

                try
                {
                    MemberLocationObject memberLocationObject = dataSnapshot.getValue(MemberLocationObject.class);
                    LatLng pickup_lat_lng = new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude());
                    LatLng drop_lat_lng = new LatLng(ride.getDrop_location().getLatitude(),ride.getDrop_location().getLongitude());

                    boolean allow = false;

                    for (int i=0;i<members_saved.size();i++)
                    {
                        if (memberLocationObject.getMem_id().equalsIgnoreCase(members_saved.get(i).getMem_id())) {
                            allow = true;
                            break;
                        }
                    }

                    if (allow) {

                        distance_member = distance(ride.getMeet_location().getLatitude(), ride.getMeet_location().getLongitude(),
                                Double.parseDouble(memberLocationObject.getMem_lat()),
                                Double.parseDouble(memberLocationObject.getMem_lng()));

                        distance_member = distance_member * 1000;

                        distanceListner.onDistanceUpdate(distance_member);

                        if (!memberLocationObject.getMem_id().equalsIgnoreCase("0")) {
                            if (member_markers.size()==0) {
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                                Double.parseDouble(memberLocationObject.getMem_lng())))
                                        .title(memberLocationObject.getMem_name())
                                        .icon(BitmapDescriptorFactory.fromResource(resource))

                                );
                                marker.setTag(memberLocationObject.getMem_id());
                                member_markers.add(marker);

                            }

                            location_points.setText(memberLocationObject.getMem_lat()+","+memberLocationObject.getMem_lng());
                            members.add(memberLocationObject);


                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (int i = 0; i < member_markers.size(); i++) {
                                    Marker m = member_markers.get(i);
                                    builder.include(m.getPosition());
                                }

                                if (ride.getRideStatus()== RideStatus.STATE_INITIAL)
                                {
                                    builder.include(new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));
                                   // drawLine(latLng,ride.getMeet_location());

                                    pickup = mMap.addMarker(new MarkerOptions()
                                            .position(pickup_lat_lng)
                                            .title(ride.getPickup_loc())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red)));

                                    distance_member = distance(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude(),
                                            Double.parseDouble(memberLocationObject.getMem_lat()),
                                            Double.parseDouble(memberLocationObject.getMem_lng()));

                                    distance_member = distance_member * 1000;

                                }
                                else if (ride.getRideStatus()== RideStatus.STATE_REACHED)
                                {
                                    builder.include(new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));
                                }
                                else if (ride.getRideStatus()== RideStatus.STATE_JOB_STARTED)
                                {
                                  //  builder.include(new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));
                                    builder.include(drop_lat_lng);
                                    drawLine(pickup_lat_lng,drop_lat_lng);
                                    LatLng member_location = new LatLng( Double.parseDouble(memberLocationObject.getMem_lat()),
                                            Double.parseDouble(memberLocationObject.getMem_lng()));

                                    if (dIstanceNotifier!=null)
                                        dIstanceNotifier.onDistanceChange(member_location);

                                    pickup = mMap.addMarker(new MarkerOptions()
                                            .position(pickup_lat_lng)
                                            .title(ride.getPickup_loc())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red)));

                                    destination = mMap.addMarker(new MarkerOptions()
                                            .position(drop_lat_lng)
                                            .title(ride.getDestination_loc())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_black)));
                                }


                            int padding = 150; // offset from edges of the map in pixels
                            bounds = builder.build();
                           CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            if (!isZoomed) {

                                mMap.animateCamera(cu);
                                isZoomed = false;

                            }
                                bounds = builder.build();

                                new handleUILoadingTask().execute();
                            }
                        }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("changed","aeaf");
                try {
                    boolean allow = false;
                    LatLng pickup_lat_lng = new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude());
                    LatLng drop_lat_lng = new LatLng(ride.getDrop_location().getLatitude(),ride.getDrop_location().getLongitude());

                    MemberLocationObject memberLocationObject = dataSnapshot.getValue(MemberLocationObject.class);
                    for (int i = 0; i < member_markers.size(); i++) {
                        if (member_markers.get(i).getTag().toString().equalsIgnoreCase(memberLocationObject.getMem_id())) {

                          /*  member_markers.get(i).setPosition(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng())));
                        */    allow = true;

                            LatLng begin = member_markers.get(i).getPosition();
                            LatLng end = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng()));
                            LatLng pickup = new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude());

                            LatLng drop = new LatLng(ride.getDrop_location().getLatitude(),ride.getDrop_location().getLongitude());

                            location_points.setText(memberLocationObject.getMem_lat()+","+memberLocationObject.getMem_lng());


                            // member_markers.get(i).setTitle(memberLocationObject.getMem_name());
                            animateCarOnMap(end, begin, member_markers.get(i));
                            if (ride.getRideStatus().equals(RideStatus.STATE_INITIAL)) {
                              //  drawLine(end, pickup);
                            }
                            else if (ride.getRideStatus().equals(RideStatus.STATE_JOB_STARTED))
                            {

                                drawLine(pickup_lat_lng,drop_lat_lng);
                                LatLng member_location = new LatLng( Double.parseDouble(memberLocationObject.getMem_lat()),
                                        Double.parseDouble(memberLocationObject.getMem_lng()));

                                if (dIstanceNotifier!=null)
                                    dIstanceNotifier.onDistanceChange(member_location);
                            }
                        }

                    }

                /*    try {
                        LatLng member_latlng = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()), Double.parseDouble(memberLocationObject.getMem_lng()));
                        end_add = Constants.getCompleteAddressString(getActivity(),member_latlng.latitude,member_latlng.longitude);
                        drawLine(pick_up_lat_lng,member_latlng);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
*/

                    if (allow) {

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < member_markers.size(); i++) {
                            Marker m = member_markers.get(i);
                            builder.include(m.getPosition());
                        }

                        if (ride.getRideStatus() == RideStatus.STATE_INITIAL) {
                            builder.include(new LatLng(ride.getMeet_location().getLatitude(), ride.getMeet_location().getLongitude()));
                            LatLng latLng = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng()));
                           //  drawLine(latLng,new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));

                            pickup = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ride.getMeet_location().getLatitude(), ride.getMeet_location().getLongitude()))
                                    .title(ride.getPickup_loc())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_black)));

                            distance_member = distance(ride.getMeet_location().getLatitude(), ride.getMeet_location().getLongitude(),
                                    Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng()));

                            distance_member = distance_member * 1000;

                           /* if (distance_member<=8)
                            {
                                if (driverReachedCallback!=null)
                                    driverReachedCallback.onDriverReached();
                            }*/
                        } else if (ride.getRideStatus() == RideStatus.STATE_REACHED) {
                               builder.include(new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));
                            LatLng latLng = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng()));
                            //  drawLine(latLng,ride.getMeet_location());

                           /* if (pickup!=null)
                                pickup.remove();*/


                        } else if (ride.getRideStatus() == RideStatus.STATE_JOB_STARTED) {
                               builder.include(new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude()));
                            LatLng latLng = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                    Double.parseDouble(memberLocationObject.getMem_lng()));


                            pickup = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ride.getMeet_location().getLatitude(), ride.getMeet_location().getLongitude()))
                                    .title(ride.getPickup_loc())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red)));

                            destination = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ride.getDrop_location().getLatitude(), ride.getDrop_location().getLongitude()))
                                    .title(ride.getDestination_loc())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_black)));
                        }

                        int padding = 150; // offset from edges of the map in pixels
                        bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                        if (!isZoomed) {

                            mMap.animateCamera(cu);
                            isZoomed = false;

                        }
                        new handleUILoadingTask().execute();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("removed","awd");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e("moved","efe");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    float v = 0.0f;
    private void animateCarOnMap(final LatLng end,final LatLng start,final Marker marker) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(end);
        LatLngBounds bounds = builder.build();
      /*  CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 17);
        mMap.animateCamera(mCameraUpdate);
     */  /* if (marker==null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        }*/
        marker.setPosition(end);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v = valueAnimator.getAnimatedFraction();
                double lng = v * end.longitude + (1 - v)
                        * start.longitude;
                double lat = v * end.latitude + (1 - v)
                        * start.latitude;
                LatLng newPos = new LatLng(lat, lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(start, newPos));
              /*  mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(newPos)
                                .zoom(17.0f).build()));*/
            }
        });
        valueAnimator.start();
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }


    @Override
    public void onCameraMove() {

    }


    class handleUILoadingTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

            }
            catch (Exception e)
            {
             e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }


    RideDirectionPointsDB directionPointsDB = null;
    ArrayList<LatLng> points = null;
    private void drawLine(LatLng start_,LatLng end_)
    { try {

        directionPointsDB = new RideDirectionPointsDB(getActivity());
        points = directionPointsDB.getDirectionPointsList();
        if (points!=null)
        {
            if (points.size()>0)
            {
                showPolyLineDirection();
               /* if (smoothMovementThread.getStatus()!= AsyncTask.Status.RUNNING)
                    smoothMovementThread.execute();*/
            }
            else
            {
                String url = getMapsApiDirectionsUrl(start_, end_);
                ReadTask downloadTask = new ReadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
        else {
            String url = getMapsApiDirectionsUrl(start_, end_);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
        //  addDropInLocationMarker();
    }

    private boolean added =false;

    LatLngBounds bounds = null;
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setPadding(30, 0, 0, 0);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
      //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        setUpMap();

        if (!location_found)
            getCurrentLocation();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (member_markers!=null) {
                    if (member_markers.size()==0) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitudeGPS,
                                        longitudeGPS))
                                .icon(BitmapDescriptorFactory.fromResource(resource)));

                        builder.include(new LatLng(latitudeGPS,longitudeGPS));

                        member_markers.add(marker);

                        location_points.setText(latitudeGPS+","+longitudeGPS);

                        LatLng latLng = new LatLng(ride.getMeet_location().getLatitude(),ride.getMeet_location().getLongitude());
                        pickup = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(ride.getPickup_loc())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red)));

                        builder.include(new LatLng(latitudeGPS,longitudeGPS));
                        builder.include(latLng);

                        int padding = 150; // offset from edges of the map in pixels
                        bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                        if (!isZoomed) {

                            mMap.animateCamera(cu);
                            isZoomed = false;

                        }

                        bounds = builder.build();
                        mMap.moveCamera(cu);

                    }

                }


            }
        });
        googleMap.setMyLocationEnabled(false);

        updateMembersLocation();


    }

    SmoothMovementThread smoothMovementThread = new SmoothMovementThread();
    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        if (mMap != null) {
            LatLng loc = new LatLng(latitudeGPS, longitudeGPS);
            Log.d("GPS", latitudeGPS + " " + longitudeGPS);
            mMap.clear();
            //mMap.addMarker(new MarkerOptions().position(loc).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.st)));
          //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));


            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on left bottom

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_END, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.setMargins(0, 0, 0, 30);
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListenerGPS);

        mMap.setOnMarkerClickListener(this);

      /*  if (ride!=null)
            drawLine(ride.getMeet_location(),ride.getDrop_location());
*/
    }

    private Marker user_marker;
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

            Log.d("LOCATION", "LOCATION CHANGED");

            latitudeGPS= location.getLatitude();
            longitudeGPS=location.getLongitude();

          /*  if (user_marker!=null)
            {
                user_marker.setPosition(new LatLng(latitudeGPS,longitudeGPS));
            }
            else
            {
            user_marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitudeGPS,longitudeGPS))
                            .title("My Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red)));
            }
            if (mMap != null) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                start = loc;
                mMap.clear();
                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.st)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
            }
            drawPath();*/
        }



        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void drawPath()
    {
        if (shouldDraw && !isLineDrawn && show) {
            try {
                isLineDrawn =true;

                String[] parts = start_add.split(" ");

                if (parts.length>8) {
                    start_add="";
                    for (int i = parts.length-6; i <parts.length;i++)
                    {

                        start_add+=parts[i];
                    }

                }

              /*  parts = end_add.split(" ");

                if (parts.length>8) {
                    end_add="";
                    for (int i = parts.length-5; i <parts.length;i++)
                    {

                        end_add+=parts[i];
                    }

                }*/

                new DirectionFinder(BumbleRideJobMap.this, start_add, end_add).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("exception",e.getMessage());
            }
        }
        //  drawLine();
    }

    private void movecamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(start);
        builder.include(end);

        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);

        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(end, 15.0f));

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();


    }

    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // Checking if Google Play Services Available or not
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    //draw a line bw two paths

    boolean isLineDrawn = false;

    private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route

     /*   String[] parts = pick_location.split(" ");

        if (parts.length>8) {
            pick_location="";
            for (int i = parts.length-5; i <parts.length;i++)
            {

                pick_location+=parts[i];
            }

        }*/

     /*   parts = end_add.split(" ");

        if (parts.length>8) {
            end_add="";
            for (int i = parts.length-5; i <parts.length;i++)
            {

                end_add+=parts[i];
            }

        }*/

        String str_origin = "origin="+origin.latitude+","+origin.longitude;//.replaceAll("\\s+","").replaceAll("&","");;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;//.replaceAll("\\s+","").replaceAll("&","");


        // Sensor enabled
        String sensor = "sensor=false";
        String optimize ="optimize:true";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+optimize+"&"+sensor;

        // Output format
        String output = "json";

        String API_KEY;
        if (Constants.DIRECTION_API_KEY.equalsIgnoreCase(""))
            API_KEY = getActivity().getResources().getString(R.string.DIRECTION_API_KEY);
        else
            API_KEY = Constants.DIRECTION_API_KEY;

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&key="+API_KEY+"";
        Log.e("request url",url);

        return url;

    }


    private GoogleMap mMap;
    private Button btnFindPath;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }



    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_black))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        // movecamera();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


       /* for (int i =0;i<member_markers.size();i++)
        {
            if (marker.getTag().equals(members.get(i).getMem_id()))
            {
                //
                startChat(members.get(i));
                Log.e("marker clicked ",(members.get(i).getMem_name()));
                break;
            }
        }*/
        return false;
    }


    private class ReadTask extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);

            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line ="";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();


            }
            catch (Exception e) {
                Log.d("Exception reading url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }



    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i=0 ; i < jRoutes.length() ; i ++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String,String>>();
                    for(int j = 0 ; j < jLegs.length() ; j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for(int k = 0 ; k < jSteps.length() ; k ++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for(int l = 0 ; l < list.size() ; l ++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }




    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            // traversing through routes
            points = new ArrayList<LatLng>();
            for (int i = 0; i < routes.size(); i++) {

                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                break;
            }

            if (points.size()>0) {
                showPolyLineDirection();
              /*  if (smoothMovementThread.getStatus()!= AsyncTask.Status.RUNNING)
                    smoothMovementThread.execute();*/
                if (directionPointsDB==null)
                    directionPointsDB = new RideDirectionPointsDB(getActivity());

                directionPointsDB.saveDirectionPoints(points);
            }

        }
    }

    private void showPolyLineDirection()
    {
        PolylineOptions polyLineOptions = null;
        polyLineOptions = new PolylineOptions();

        polyLineOptions.addAll(points);
        polyLineOptions.width(5);
        polyLineOptions.color(Color.BLACK);
        polyLineOptions.startCap(new SquareCap());
        polyLineOptions.endCap(new SquareCap());
        polyLineOptions.jointType(JointType.ROUND);

        if (polyLineOptions!=null)
            mMap.addPolyline(polyLineOptions);
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            return add;

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return "";
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    class SmoothMovementThread extends AsyncTask<Boolean,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Boolean... params) {

            try {
                startSmoothMovement();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            isMoving = false;
            Toast.makeText(getActivity(),"Done",Toast.LENGTH_LONG).show();
        }
    }

    boolean isMoving = false;
    private void startSmoothMovement() {
        if (isMoving)
            return;

        isMoving = true;
        String mem_id = members_saved.get(0).getMem_id();
        String mem_name = members_saved.get(0).getMem_name();
        for (int i=0;i<points.size();i++)
        {
            String lat =points.get(i).latitude+""; //intent.getStringExtra(LocationListnerServices.SERIVICE_LATITUDE);
            String lng =points.get(i).longitude+""; //intent.getStringExtra(LocationListnerServices.SERIVICE_LONGITUDE);
            Log.e("location_changed",lat+"-"+lng);


            final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lng + "");
            member.setCurrent_job("21");

            String key = mem_id + "_member";
            if (!mem_id.equalsIgnoreCase("")) {
                FirebaseDatabase.getInstance().getReference().child("members").child(key).setValue(member);
                Log.e("member_position","member position updated on firebase");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
