package asim.tgs_member_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import asim.tgs_member_app.Current_job_screen;
import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.utils.NotifyUpdates;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class DashBoard_Frame extends Fragment
{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout upcoming,completed,tabs_lay,current_layout;

    private String selected_language;
    private String key;
    private String member_id,lat,lon;

    private String pickup,detination,mem_share,customer_name,customer_image,cust_mobile,cust_id,order_id,total_distance,meet_date;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooTView = inflater.inflate(R.layout.dashboard_layout, container, false);
    ///setToolBar();
        settings = getContext().getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
        member_id = settings.getString(Constants.PREFS_USER_ID,"0");
        lat = settings.getString(Constants.PREFS_USER_LAT,"0");
        lon = settings.getString(Constants.PREFS_USER_LNG,"0");
        key ="tgs_appkey_amin";
        viewPager = (ViewPager) rooTView.findViewById(R.id.sos_viewpager);
        upcoming = (LinearLayout) rooTView.findViewById(R.id.selected_tab1);
        completed = (LinearLayout) rooTView.findViewById(R.id.selected_tab2);
        tabs_lay = (LinearLayout) rooTView.findViewById(R.id.tabs_lay);
        current_layout = (LinearLayout) rooTView.findViewById(R.id.current_layout);

        current_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Current_job_screen.class);

                intent.putExtra("pickup",pickup);
                intent.putExtra("detination",detination);
                intent.putExtra("mem_share",mem_share);
                intent.putExtra("customer_name",customer_name);
                intent.putExtra("customer_image",customer_image);
                intent.putExtra("cust_mobile",cust_mobile);
                intent.putExtra("cust_id",cust_id);
                intent.putExtra("order_id",order_id);
                intent.putExtra("total_distance",total_distance);

                startActivity(intent);
            }
        });

        settings =getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        selected_language = settings.getString(Constants.PREF_LOCAL,"english");

        if (!selected_language.equalsIgnoreCase("english"))
        {
            ViewGroup.LayoutParams params = upcoming.getLayoutParams();
            params.height = params.height + 20;
            upcoming.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = completed.getLayoutParams();
            params1.height = params1.height + 20;
            completed.setLayoutParams(params1);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) tabs_lay.getLayoutParams();
            params3.height = params3.height + 30;
            tabs_lay.setLayoutParams(params3);
        }

        upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcoming.setBackgroundResource(R.drawable.tab_selected_bg);
                completed.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(0);
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completed.setBackgroundResource(R.drawable.tab_selected_bg);
                upcoming.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(1);

            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        setupViewPager(viewPager,getArguments());


        return rooTView;
    }


    private void setupViewPager(ViewPager viewPager,Bundle bundle) {
        final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getFragmentManager());


        int index=0;

        if (bundle!=null)
         index = bundle.getInt("index");

        final Upcoming_Jobs upcoming_ = new Upcoming_Jobs();

        adapter.addFragment(upcoming_,getResources().getString(R.string.Upcoming_jobs));

        Suggested_Jobs completed_ = new Suggested_Jobs();
        adapter.addFragment(completed_,getResources().getString(R.string.suggested_jobs));


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    upcoming.performClick();
                    if (UtilsManager.shouldRefresh) {
                        adapter.updateFragments();
                        UtilsManager.shouldRefresh = false;
                    }
                }
                else {
                    completed.performClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // tabLayout.setupWithViewPager(viewPager);
        //  setupTabIcons();

        //  TabLayout.Tab tab= tabLayout.getTabAt(index);
        //  tab.select();
        //  setupTabIcons();

        viewPager.setCurrentItem(index);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            checkCurrentJob();
            setUpTimer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private static final Integer INTERVAL_MIN = 1;
    private static final Integer INTERVAL_SEC = 10;

    private void setUpTimer()
    {
        if (countDownTimer!=null)
        {
            if (isRunning)
                return;
        }

        countDownTimer = new CountDownTimer(INTERVAL_MIN * INTERVAL_SEC*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("counting",(millisUntilFinished/1000)+"");
                isRunning = true;

            }

            @Override
            public void onFinish() {
                Log.e("counting done","...........");
                try {
                  checkCurrentJob();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };

        countDownTimer.start();

    }

    SharedPreferences settings;
    AsyncHttpClient asyncClient = new AsyncHttpClient();

    private void checkCurrentJob()
    {

        asyncClient.setConnectTimeout(20000);

        asyncClient.get(getContext(), Constants.Host_Address + "members/show_my_current_job/" + member_id + "/" + key + "", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response",response);
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray array = responseObj.getJSONArray("data");
                    JSONObject data = array.getJSONObject(0);

                    pickup = data.getString("meet_location");
                    detination = data.getString("destination");
                    mem_share = data.getString("member_share");
                    customer_name = data.getString("customer_name");
                    customer_image = data.getString("customer_profile_img");
                    cust_mobile = data.getString("customer_mobile");
                    cust_id = data.getString("customer_id");
                    order_id = data.getString("order_id");
                    total_distance = data.getString("total_distance");
                   // meet_date = data.getString("meetup_time");

                    Log.e("refreshed","data refreshed");

                    if (!order_id.equalsIgnoreCase("null"))
                        current_layout.setVisibility(View.VISIBLE);
                    else
                        current_layout.setVisibility(View.GONE);

                    isRunning = false;
                    if (countDownTimer!=null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    setUpTimer();
                }
                catch (Exception e)
                {
                    Log.e("refreshed",e.getMessage());
                    current_layout.setVisibility(View.GONE);
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
                    Log.e("refreshed",e.getMessage());
                }
            }
        });

    }

}
