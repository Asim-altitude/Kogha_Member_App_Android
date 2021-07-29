package asim.tgs_member_app.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import asim.tgs_member_app.R;

/**
 * Created by PC-GetRanked on 9/14/2018.
 */

public class DummyFrame extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dummy_layout,container,false);

        return rootView;
    }
}
