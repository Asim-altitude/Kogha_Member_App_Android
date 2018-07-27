package asim.tgs_member_app;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import asim.tgs_member_app.fragments.ChangePasswordFragment;
import asim.tgs_member_app.fragments.EditProfileFragment;
import asim.tgs_member_app.fragments.Suggested_Jobs;
import asim.tgs_member_app.fragments.Upcoming_Jobs;
import asim.tgs_member_app.fragments.UsefulViewPagerAdapter;
import asim.tgs_member_app.utils.UtilsManager;


public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private LinearLayout upcoming,completed,tabs_lay,current_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setupToolbar();

        upcoming = (LinearLayout) findViewById(R.id.selected_tab1);
        completed = (LinearLayout) findViewById(R.id.selected_tab2);
        tabs_lay = (LinearLayout) findViewById(R.id.tabs_lay);


        ((TextView)findViewById(R.id.pageTitle)).setText(R.string.my_profile);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

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

    private void setupViewPager(ViewPager viewPager,TabLayout tab) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tab.getTabCount());
        viewPager.setAdapter(adapter);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.white_color));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        final UsefulViewPagerAdapter adapter = new UsefulViewPagerAdapter(getSupportFragmentManager());


        int index=0;


        final EditProfileFragment edit_ = new EditProfileFragment();

        adapter.addFragment(edit_,getResources().getString(R.string.edit_my_profile));

        ChangePasswordFragment change_pass_frame = new ChangePasswordFragment();
        adapter.addFragment(change_pass_frame,getResources().getString(R.string.change_my_password));


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

    //------------------ViewPager Adapter class------------------//
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;
        private String tabTitles[] = getApplicationContext().getResources().getStringArray(R.array.profile_tabs);
        //Constructor to the class
        public ViewPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount= tabCount;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    return new EditProfileFragment();
                case 1:
                    return new ChangePasswordFragment();
                default:
                    return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }

    /**
     * Render the toolbar
     */
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");
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
}