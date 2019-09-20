package com.huaying.italker.frags.account;


import com.huaying.common.app.Fragment;
import com.huaying.common.widget.PortraitView;
import com.huaying.italker.R;
import com.huaying.italker.frags.media.GalleryFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更新信息的界面
 */
public class UpdateInfoFragment extends Fragment {

    @SuppressWarnings("unused")
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    public UpdateInfoFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {

                    }
                })
                //建议使用getChildFragmentManager，因为在frag中
                //tag
                .show(getChildFragmentManager(),GalleryFragment.class.getName());
    }
}
