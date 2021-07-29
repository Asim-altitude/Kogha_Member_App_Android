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

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import asim.tgs_member_app.fragments.ChangePasswordFragment;
import asim.tgs_member_app.fragments.EditProfileFragment;
import asim.tgs_member_app.models.Constants;


public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private androidx.viewpager.widget.ViewPager viewPager;

    private LinearLayout upcoming,completed,tabs_lay,current_layout;
    private com.google.android.material.tabs.TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setupToolbar();

        upcoming = (LinearLayout) findViewById(R.id.selected_tab1);
        completed = (LinearLayout) findViewById(R.id.selected_tab2);
       // tabs_lay = (LinearLayout) findViewById(R.id.tabs_lay);



        viewPager =  findViewById(R.id.viewPager);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

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

     /*   tabLayout.addTab(tabLayout.newTab().setText(R.string.edit_profile));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.change_pass));*/
      //  tabLayout.setupWithViewPager(viewPager);
       // setupViewPager(viewPager,tabLayout);
        setupViewPager(viewPager);
    }

    public class ViewPagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
        private final List<androidx.fragment.app.Fragment> _FragmentList = new ArrayList<>();
        private final List<String> _FragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }



        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            return _FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return _FragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _FragmentTitleList.get(position);
        }

        public void AddFragments(androidx.fragment.app.Fragment fragment, String title) {
            _FragmentList.add(fragment);
            _FragmentTitleList.add(title);

        }

    }

    private void setupViewPager(androidx.viewpager.widget.ViewPager viewPager) {

        int index=0;


        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        final EditProfileFragment edit_ = new EditProfileFragment();
        pagerAdapter.AddFragments(edit_,"Profile");

        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        pagerAdapter.AddFragments(changePasswordFragment,"Change Password");


        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(index);

        // final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getFragmentManager());


       /* int index=0;

        index = settings.getInt(Constants.CURRENT_TAB,0);

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

        viewPager.setCurrentItem(index);*/

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    private void setupToolbar(){
        androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.pageTitle)).setText("My Profile");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
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


}