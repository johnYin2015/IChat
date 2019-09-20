package com.huaying.italker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huaying.italker.common.R;
import com.huaying.italker.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 画廊
 * TODO item点击事件的处理
 */
public class GalleryView extends RecyclerView {

    private static final String TAG = GalleryView.class.getSimpleName();

    private final int LOADER_ID = 0x0100;

    public static final int MAX_IMAGE_COUNT = 3;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MIN_IMAGE_FILE_SIZE = 10 * 1024;

    private LoaderCallback mLoaderCallback = new LoaderCallback();

    private Adapter mAdapter = new Adapter();

    private List<Image> mSelectedImages = new LinkedList<>();

    private SelectedChangeListener mListener;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //cell点击操作。若点击是允许的，则更新相应的cell状态
                //然后更新界面，同理，若不能允许点击（达到了最大的选中数量），则不更新界面
                if (onItemSelectClick(image)){
                    //noinspection unchecked
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     *
     * @param loaderManager
     * @return 任意一个loader id,用来销毁loader
     */
    @SuppressWarnings("JavaDoc")
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * cell点击的具体逻辑
     *
     * @param image 图片
     * @return true:进行了数据更改，你需要刷新，反之不刷新
     */
    private boolean onItemSelectClick(Image image) {
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelect = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {

                //获得提示文字
                String string = getResources().getString(R.string.label_gallery_select_max_size);
                string =String.format(string,MAX_IMAGE_COUNT);
                Toast.makeText(getContext(),string, Toast.LENGTH_LONG).show();

                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }

        if (notifyRefresh) {
            notifySelectChanged();
        }
        return true;
    }

    public String[] getSelectPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) paths[index++] = image.path;

        return paths;
    }

    /**
     * 清空选中的图片
     */
    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        //获得监听者，并判断是否有监听者，然后进行回调数量变化调用
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知adapter数据更改的方法
     * @param images 新的数据
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }


    /***
     * 用于实际加载数据的loader
     */
    @SuppressWarnings("NullableProblems")
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @SuppressWarnings("deprecation")
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//图片的id
                MediaStore.Images.Media.DATA,//图片的路径
                MediaStore.Images.Media.DATE_ADDED //图片的创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.e("GalleryView", "onCreateLoader id = " + id);
            //loader创建时
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");//倒序查询

            }
            //noinspection ConstantConditions
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //loader加载完成时
            List<Image> images = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    //移动游标到开始
                    data.moveToFirst();

                    //得到对应列的index坐标
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);

                    do {
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            continue;
                        }

                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);

                    } while (data.moveToNext());
                }
            }

            Log.e(TAG, "images size" + images.size());

            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {
            // 当Loader销毁或者重置了, 进行界面清空
            updateSource(null);
        }
    }

    /**
     * 内部的数据结构
     */
    private static class Image {
        int id;//数据id
        String path;//图片路径
        long date;//图片的创建日期
        boolean isSelect;//是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            //noinspection EqualsReplaceableByObjectsCall
            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

    }

    /**
     * cell对应的holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPic = itemView.findViewById(R.id.iv_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            //mPic.isHardwareAccelerated()

            Glide.with(getContext())
                    .load(image.path) //加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //不使用缓存，直接从原图加载
                    .centerCrop() //居中剪切
                    .placeholder(R.color.grey_200) //默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);

            mSelected.setChecked(image.isSelect);

            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的一个监听器
     * 当选择的图片数量发生变化时，调用对应的逻辑进行处理
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}
