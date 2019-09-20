package com.huaying.italker.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.huaying.common.widget.GalleryView;
import com.huaying.italker.R;

import net.qiujuer.genius.ui.Ui;

import androidx.annotation.NonNull;

/**
 * 图片选择Frag
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView mGalleryView;
    private OnSelectedListener mListener;

    public GalleryFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //先用默认的，官方给出
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGalleryView = root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        //noinspection deprecation
        mGalleryView.setup(getLoaderManager(),this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        if (count > 0) {
            dismiss();
            if (mListener!=null){
                String[] paths = mGalleryView.getSelectPath();
                mListener.onSelectedImage(paths[0]);
                //取消和唤起者之间的引用，加快内存回收
                mListener =null;
            }
        }
    }

    public interface OnSelectedListener{
        void onSelectedImage(String path);
    }

    private static class TransStatusBottomSheetDialog extends BottomSheetDialog{

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window==null)
                return;
            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            //状态栏高度
            int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 25);

            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,dialogHeight<=0?ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight);
        }
    }

    public GalleryFragment setListener(OnSelectedListener listener) {
        mListener =listener;
        return this;
    }
}
