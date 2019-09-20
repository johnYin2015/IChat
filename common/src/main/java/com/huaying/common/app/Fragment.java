package com.huaying.common.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class Fragment extends androidx.fragment.app.Fragment {

    private View mRoot;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Unbinder mRootUnBinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layId = getContentLayoutId();
        if (mRoot == null) {
            //初始化当前根布局，但不在创建时加container里边
            View root = inflater.inflate(layId, container, false);
            initWidget(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                //将当前root从其父容器移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //当view创建完初始化数据
        initData();
    }

    /**
     * 获得当前界面的资源文件id
     * 一定要复写
     *
     * @return 资源文件id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    private void initWidget(View root) {
        mRootUnBinder = ButterKnife.bind(this, root);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initArgs(getArguments());
    }

    /**
     * 初始化相关参数
     * @param bundle 参数
     */
    @SuppressWarnings("unused")
    private void initArgs(Bundle bundle) {
    }

    /**
     * 返回键触发时回调
     * @return 返回True表示已处理逻辑，activity不用自己finish;否则未处理逻辑，activity自己走自己逻辑
     */
    boolean onBackPressed(){
        return false;
    }

}
