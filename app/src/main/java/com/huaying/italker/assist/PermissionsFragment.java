package com.huaying.italker.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.huaying.italker.R;
import com.huaying.italker.common.app.Application;
import com.huaying.italker.frags.media.GalleryFragment;

import java.util.List;
import java.util.Objects;

/**
 * 权限申请弹出框
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {


    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //先用默认的，官方给出
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获得布局中控件
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        root.findViewById(R.id.btn_submit)
        .setOnClickListener(v -> requestPerm());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    private void refreshState(View root) {
        if(root==null)
            return;

        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network).setVisibility(haveNetworkPerm(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_read).setVisibility(haveReadPerm(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_write).setVisibility(haveWritePerm(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_audio).setVisibility(haveRecordAudioPerm(context)?View.VISIBLE:View.GONE);

    }

    /**
     * 是否有网络权限
     * @param context
     * @return
     */
    private static boolean haveNetworkPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };

        return EasyPermissions.hasPermissions(context,perms);
    }

    private static boolean haveReadPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        return EasyPermissions.hasPermissions(context,perms);
    }

    private static boolean haveWritePerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        return EasyPermissions.hasPermissions(context,perms);
    }

    private static boolean haveRecordAudioPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };

        return EasyPermissions.hasPermissions(context,perms);
    }

    //私有show方法
    private static void show(FragmentManager fm){
        new PermissionsFragment()
                .show(fm,PermissionsFragment.class.getName());
    }

    /**
     * 检查是否有所有权限
     * @param context
     * @param fm
     * @return
     */
    public static boolean haveAll(Context context, FragmentManager fm){
        boolean haveAll = haveNetworkPerm(context)
                &&haveReadPerm(context)
                &&haveWritePerm(context)
                &&haveRecordAudioPerm(context);
        if (!haveAll){
            show(fm);
        }

        return haveAll;
    }

    private static final int RC = 0x0100;

    @AfterPermissionGranted(RC)
    private void requestPerm(){
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(),perms)){
            Application.showToast(R.string.label_permission_ok);
            refreshState(getView());
        }else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.title_assist_permissions),RC,perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}
