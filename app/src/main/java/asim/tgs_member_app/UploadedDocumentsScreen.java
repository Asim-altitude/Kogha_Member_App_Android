package asim.tgs_member_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import asim.tgs_member_app.fragments.DocumnetFrame;
import asim.tgs_member_app.fragments.SimpleDocFrame;
import asim.tgs_member_app.fragments.UsefulViewPagerAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.utils.UtilsManager;


public class UploadedDocumentsScreen extends AppCompatActivity {


   SharedPreferences settings;
    LinearLayout uploaded,missing_lay;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_tabs);
        settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        setupToolbar();
        viewPager = (ViewPager) findViewById(R.id.docs_viewpager);
        uploaded = (LinearLayout) findViewById(R.id.selected_tab1);
        missing_lay = (LinearLayout) findViewById(R.id.selected_tab2);
        tabLayout = findViewById(R.id.tabs);

        uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaded.setBackgroundResource(R.drawable.tab_selected_bg);
                missing_lay.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(0);
            }
        });

        missing_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                missing_lay.setBackgroundResource(R.drawable.tab_selected_bg);
                uploaded.setBackgroundResource(R.drawable.order_place_item_background);
                viewPager.setCurrentItem(1);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getSupportFragmentManager());

        int index=0;

        final DocumnetFrame docs_frame = new DocumnetFrame();
        final SimpleDocFrame simpleDocFrame = new SimpleDocFrame();

        adapter.AddFragments(docs_frame,"Service Documents");

        adapter.AddFragments(simpleDocFrame,"Others");

        /*MissingDocuments missing = new MissingDocuments();
        adapter.addFragment(missing,"Missing");*/


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.pageTitle)).setText("Documents");
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
