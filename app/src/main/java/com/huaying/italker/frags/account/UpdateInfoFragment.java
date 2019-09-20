package com.huaying.italker.frags.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huaying.common.app.Application;
import com.huaying.common.app.Fragment;
import com.huaying.common.widget.PortraitView;
import com.huaying.italker.R;
import com.huaying.italker.frags.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

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
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //图片精度设置
                        options.setCompressionQuality(96);

                        File dPath = Application.getPortraitTmpFile();

                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(520, 520)
                                .withOptions(options)
                                .start(getActivity());
                    }
                })
                //建议使用getChildFragmentManager，因为在frag中
                //tag
                .show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //收到从act传递的回调，
        if (resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri!=null){
                loadPortrait(resultUri);
            }
        }else if (resultCode==UCrop.RESULT_ERROR){
            final Throwable throwable = UCrop.getError(data);
        }
    }

    private void loadPortrait(Uri uri){

        Glide.with(this)
                .asBitmap()
                .load(uri)
                .centerCrop()
                .into(mPortrait);
    }
}
