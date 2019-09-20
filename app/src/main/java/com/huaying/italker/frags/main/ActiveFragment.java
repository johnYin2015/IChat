package com.huaying.italker.frags.main;

import android.util.Log;

import com.huaying.common.app.Fragment;
import com.huaying.common.widget.GalleryView;
import com.huaying.italker.R;

import butterknife.BindView;

@SuppressWarnings("ALL")
public class ActiveFragment extends Fragment {

    private static final String TAG = ActiveFragment.class.getSimpleName();
    //private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @BindView(R.id.gv_gallery)
    GalleryView mGallery;

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
        /*if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Log.e(TAG, "授予了权限");
            setupGallery();
        }*/
        setupGallery();
    }

    private void setupGallery() {
        Log.e(TAG,"fragment setupGallery");
        mGallery.setup(getLoaderManager(), count -> {

        });
    }

    /**
     * fragment的该方法在用户对弹出的权限申请对话框进行操作时没被回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult");
    }*/

}
