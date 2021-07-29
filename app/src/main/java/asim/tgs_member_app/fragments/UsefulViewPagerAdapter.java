package asim.tgs_member_app.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import asim.tgs_member_app.utils.ObserverFragment;


/**
 * Created by Tayyab on 8/30/2017.
 */

public class UsefulViewPagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
    private final List<Fragment> _FragmentList = new ArrayList<>();
    private final List<String> _FragmentTitleList = new ArrayList<>();

    public UsefulViewPagerAdapter(androidx.fragment.app.FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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

    public void AddFragments(Fragment fragment, String title) {
        _FragmentList.add(fragment);
        _FragmentTitleList.add(title);

    }

}
