package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.adapters.DocumentAdapter;
import asim.tgs_member_app.adapters.ServicesAdapter;
import asim.tgs_member_app.fragments.DocumnetFrame;
import asim.tgs_member_app.fragments.MissingDocuments;
import asim.tgs_member_app.fragments.Suggested_Jobs;
import asim.tgs_member_app.fragments.Upcoming_Jobs;
import asim.tgs_member_app.fragments.UsefulViewPagerAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberDocument;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;


public class UploadedDocumentsScreen extends AppCompatActivity {


   SharedPreferences settings;
    LinearLayout uploaded,missing_lay;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_tabs);
        settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        setupToolbar();
        viewPager = (ViewPager) findViewById(R.id.docs_viewpager);
        uploaded = (LinearLayout) findViewById(R.id.selected_tab1);
        missing_lay = (LinearLayout) findViewById(R.id.selected_tab2);

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

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getSupportFragmentManager());


        int index=0;


        final DocumnetFrame docs_frame = new DocumnetFrame();

        adapter.addFragment(docs_frame,"Uploaded");

        MissingDocuments missing = new MissingDocuments();
        adapter.addFragment(missing,"Missing");


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    uploaded.performClick();
                    if (UtilsManager.shouldRefresh) {
                        adapter.updateFragments();
                        UtilsManager.shouldRefresh = false;
                    }
                }
                else {
                    missing_lay.performClick();
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

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.upload_documents);
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



}
