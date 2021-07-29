package asim.tgs_member_app;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;


import java.math.BigDecimal;

import asim.tgs_member_app.fragments.Immediate_Jobs_Frame;
import asim.tgs_member_app.restclient.RestServiceClient;

/**
 * Created by PC-GetRanked on 10/16/2017.
 */

public class HomePage extends AppCompatActivity  {

    private String fullName;
    private View nav_header;
    NavigationView navigationView;
    androidx.drawerlayout.widget.DrawerLayout drawer;


    RestServiceClient restServiceClient;


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback_dest = new ResultCallback<PlaceBuffer>() {

        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            // Get the Place object from the buffer.

          //  manager.beginTransaction().replace(R.id.content_frame, new MainMap()).commit();
            //mLatitude= String.valueOf(place2.getLatLng().latitude);
            //mLongitude= String.valueOf(place2.getLatLng().longitude);
            //LatLng newLatLngTemp2 = new LatLng(place2.getLatLng().latitude, place2.getLatLng().longitude);


        }
    };

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }



    Dialog dialog;
    private LinearLayout distance_exceed_message;
  /*  private void showDistanceDialog()
    {
        dialog = new Dialog(HomePage.this);
        dialog.setContentView(R.layout.distance_exceeded_dialog);
        dialog.setCanceledOnTouchOutside(false);

        Button yes_btn, no_btn;

        yes_btn = (Button) dialog.findViewById(R.id.yes_btn);
        no_btn = (Button) dialog.findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extra_charges_agreed = true;
                dialog.dismiss();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extra_charges_agreed = false;
                dialog.dismiss();
            }
        });



        dialog.show();
    }*/




    private AppBarLayout appbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

        getSizeName(this);
        try {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            Log.e("height", String.valueOf(height));
            Log.e("width", String.valueOf(width));

            int height_val = pxToDp(height);
            Log.e("height_val", String.valueOf(height_val));

           /* appbar = (AppBarLayout) findViewById(R.id.appBarMain);
            float heightDp = (float) (getResources().getDisplayMetrics().heightPixels / 1.52);
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
            lp.height = (int) heightDp;*/
 /*
            E/height: 1920
            E/width: 1080

            E/height: 2392
            E/width: 1440*/
        } catch (Exception e) {

        }

        androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        drawer =  findViewById(R.id.drawer_layout);

        androidx.appcompat.app.ActionBarDrawerToggle actionBarDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(HomePage.this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

      //  setupNavigationView();


        manager.beginTransaction().replace(R.id.content_main_frame,new Immediate_Jobs_Frame()).commit();
        setTitle("Current Jobs");

    }

    androidx.fragment.app.FragmentManager manager = getSupportFragmentManager();
   /* private void setupNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.navigation_drawer_items);
        SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        fullName = settings.getString(Constants.PREFS_USER_NAME, "");

         nav_header = LayoutInflater.from(HomePage.this).inflate(R.layout.nav_header, null);
         TextView name = (TextView) nav_header.findViewById(R.id.txtName);
         name.setText(fullName);
         navigationView.addHeaderView(nav_header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                }
                drawer.closeDrawer(Gravity.LEFT);
                return false;
            }
        });

    }*/

    private static String getSizeName(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                Log.e("small", "small");
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                Log.e("normal", "normal");
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                Log.e("large", "large");
                return "large";
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9

                Log.e("xlarge", "xlarge");
                return "xlarge";
            default:
                Log.e("undefined", "undefined");
                return "undefined";


        }
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }





}
