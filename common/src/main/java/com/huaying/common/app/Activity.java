package com.huaying.common.app;

import android.os.Bundle;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面没初始化前初始化窗口
        initWindows();

        if (initArgs(getIntent().getExtras())) {
            int layoutId = getContentLayoutId();
            setContentView(layoutId);
            initWidget();
            initData();
        } else {//没有传入参数则退出当前界面
            finish();
        }
    }

    /**
     * 初始化窗口
     */
    protected  void initWindows() {

    }

    /**
     * 初始化相关参数
     * @param bundle 参数
     * @return 如果参数正确返回True,否则False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 获得当前界面的资源文件id
     * 一定要复写
     * @return 资源文件id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 当点起界面导航返回时，finish当前界面
     * @return 返回一个布尔值
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //noinspection ConstantConditions
        if (fragments!=null&&fragments.size()>0){
            for (Fragment fragment : fragments) {
                //判断是不是自己能处理的fragment类型
                if (fragment instanceof com.huaying.common.app.Fragment){
                    //判断是否拦截了返回键
                    if (((com.huaying.common.app.Fragment)fragment).onBackPressed()){
                        //拦截了返回事件，走fragment处理逻辑,不会走下面的finish
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}
