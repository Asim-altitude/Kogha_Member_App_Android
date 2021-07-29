package asim.tgs_member_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import asim.tgs_member_app.fragments.UsefulViewPagerAdapter;
import asim.tgs_member_app.fragments.notifications_tabs.ChatNotificationFrame;
import asim.tgs_member_app.fragments.notifications_tabs.GeneralNotificationFrame;
import asim.tgs_member_app.fragments.notifications_tabs.JobNotificationFrame;
import asim.tgs_member_app.models.Constants;

public class NotificationListActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout general,chat,job;

    private void setupToolbar(){
        androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.pageTitle)).setText("Notifications");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_tabs_main);

        setupToolbar();

        viewPager = (ViewPager) findViewById(R.id.sos_viewpager);
        general = (LinearLayout) findViewById(R.id.selected_tab1);
        chat = (LinearLayout) findViewById(R.id.selected_tab2);
        job = (LinearLayout) findViewById(R.id.selected_tab3);


        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                general.setBackgroundResource(R.drawable.tab_selected_bg);
                chat.setBackgroundResource(R.drawable.order_place_item_background);
                job.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(0);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.setBackgroundResource(R.drawable.tab_selected_bg);
                general.setBackgroundResource(R.drawable.order_place_item_background);
                job.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(1);

            }
        });

        job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job.setBackgroundResource(R.drawable.tab_selected_bg);
                chat.setBackgroundResource(R.drawable.order_place_item_background);
                general.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(2);

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        setupViewPager(viewPager,getIntent());


    }

    private void setupViewPager(final ViewPager viewPager, Intent bundle) {
        final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getSupportFragmentManager());


        int index=0;

        if (bundle!=null)
            index = bundle.getIntExtra("index",0);

        final GeneralNotificationFrame gen = new GeneralNotificationFrame();

        adapter.AddFragments(gen, "General");

        final ChatNotificationFrame chat_frame = new ChatNotificationFrame();
        adapter.AddFragments(chat_frame, "Chat");

        final JobNotificationFrame job_frame = new JobNotificationFrame();
        adapter.AddFragments(job_frame, "Job");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if (position==0)
                        general.performClick();
                    if (position==1)
                        chat.performClick();
                    if (position==2)
                        job.performClick();
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

        viewPager.setCurrentItem(Constants.selected_notification_tab);
        Constants.selected_notification_tab =0;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
