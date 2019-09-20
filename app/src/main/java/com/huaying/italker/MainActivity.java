package com.huaying.italker;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.OnClick;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huaying.common.app.Activity;
import com.huaying.common.widget.PortraitView;
import com.huaying.italker.activities.AccountActivity;
import com.huaying.italker.frags.main.ActiveFragment;
import com.huaying.italker.frags.main.ContactFragment;
import com.huaying.italker.frags.main.GroupFragment;
import com.huaying.italker.helper.NavHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;


public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangeListener<Integer> {

    private static final String TAG = MainActivity.class.getSimpleName();

    //private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.pv_portrait)
    PortraitView mPortrait;

    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.fl_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    private NavHelper mNavHelper;

    @BindView(R.id.fab_action)
    FloatActionButton mAction;

    @OnClick(R.id.iv_search)
    void onSearchMenuClick() {
    }

    @OnClick(R.id.fab_action)
    void onActionClick() {
        AccountActivity.show(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mNavHelper = new NavHelper<>(this,
                getSupportFragmentManager(), R.id.fl_container, this);
        //noinspection unchecked
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));

        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //this:viewtarget
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();

        Log.e(TAG, "mainAct initData");
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Log.e(TAG, "mainAct 授予了权限");
            //以后每次重新初始化时，如果之前是权限是点击允许的，那么调用frag setupCallery;
            // 反之之前是拒绝的，则会弹出申请框。用户点允许后，还是会在act中setup Callery
        }*/

        Menu menu = mNavigation.getMenu();//触发第一次点击
        //触发选中home,触发时会调用onNavigationItemSelected
        menu.performIdentifierAction(R.id.action_home, 0);
    }

    boolean isFirst = true;

    /**
     * 当底部导航被点击触发
     *
     * @param item tab
     * @return true 表示自己处理点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //转嫁事件流到工具中
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper处理后的回调方法
     *
     * @param newTab 旧的tab
     * @param oldTab 新的tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTitle.setText(newTab.extra);//从额外字段中取出title资源id

        //对浮动按钮隐藏显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            //主界面隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            //transY 默认为0则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        //动画
        // 旋转，y轴位移 弹性插值器
        mAction.animate().
                rotation(rotation)
                .setInterpolator(new AnticipateOvershootInterpolator(1)).
                translationY(transY).
                setDuration(480).
                start();
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "mainAct onRequestPermissionsResult");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "mainAct agree");

                actFragmentSetupCallery();
            } else {
                Log.e(TAG, "mainAct refuse");
            }
        }
    }*/

/*
    private void actFragmentSetupCallery() {
        Log.e(TAG, "actFragmentSetupCallery");

        NavHelper.Tab currentTab = mNavHelper.getCurrentTab();
        if (currentTab != null) {
            Fragment currentFragment = currentTab.fragment;

            if (currentFragment!=null) {
                Log.e(TAG, "mainAct currentTab : " + currentFragment.getClass().getName());
                //当前的fragment是ActiveFragment
                if (currentFragment instanceof ActiveFragment) {
                    ActiveFragment activeFragment = (ActiveFragment) currentFragment;
                    //第一次出现权限申请提示框，点击允许，调用以下代码，初始化ActiveFragment画廊
                    activeFragment.setupGallery();
                }
            }
        }
    }
*/
}
