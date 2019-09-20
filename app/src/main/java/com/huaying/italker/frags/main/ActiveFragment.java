package com.huaying.italker.frags.main;

import android.util.Log;

import com.huaying.italker.common.app.Fragment;
import com.huaying.italker.common.widget.GalleryView;
import com.huaying.italker.R;

import butterknife.BindView;

@SuppressWarnings("ALL")
public class ActiveFragment extends Fragment {

    private static final String TAG = ActiveFragment.class.getSimpleName();

    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.e(TAG, "initData");
    }

}
