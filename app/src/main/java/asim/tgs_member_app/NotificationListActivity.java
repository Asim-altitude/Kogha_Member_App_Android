package asim.tgs_member_app;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
        // Show menu icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle(R.string.notifications);
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

        adapter.addFragment(gen, "General");

        final ChatNotificationFrame chat_frame = new ChatNotificationFrame();
        adapter.addFragment(chat_frame, "Chat");

        final JobNotificationFrame job_frame = new JobNotificationFrame();
        adapter.addFragment(job_frame, "Job");

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
}
