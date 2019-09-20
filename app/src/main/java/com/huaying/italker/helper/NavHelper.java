package com.huaying.italker.helper;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 完成对fragment调度和重用问题，达到最优fragment切换
 *
 * @authoer johnyin2015
 */
@SuppressWarnings("JavaDoc")
public class NavHelper<T> {

    private final FragmentManager fragmentManager;

    private final int containerId;

    @SuppressWarnings("unchecked")
    private final SparseArray<Tab<T>> mTabs = new SparseArray();

    //初始化必需
    private Context mContext;
    private OnTabChangeListener<T> mListener;
    private Tab<T> currentTab;//选中的tab

    public NavHelper(Context mContext, FragmentManager fragmentManager, int containerId, OnTabChangeListener listener) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        //noinspection unchecked
        this.mListener = listener;
    }

    /**
     * 添加tab
     *
     * @param menuId tab对应的menuId
     * @param tab
     */
    public NavHelper add(int menuId, Tab<T> tab) {
        mTabs.put(menuId, tab);
        return this;
    }

    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行点击菜单操作
     *
     * @param menuId
     * @return 是否能处理
     */
    public boolean performClickMenu(int menuId) {
        //找菜单对应的tab,有处理
        Tab<T> tab = mTabs.get(menuId);
        if (tab != null){
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的tab(菜单)选择操作
     *
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;

        if (currentTab != null) {
            oldTab = currentTab;
            //当前tab是点击的tab
            if (oldTab == tab) {
                notifyTabReselect( tab);
                return;
            }
        }
        currentTab = tab;
         onTabChanged(currentTab,oldTab);
    }

    /**
     * 真实的fragment调度操作
     * @param newTab
     * @param oldTab
     */
    private void onTabChanged(Tab<T> newTab,Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (oldTab!=null){
            if (oldTab.fragment!=null){
                //界面移除，还在fragment缓存空 间中
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab!=null){
            if (newTab.fragment==null){
                //第一次新建
                @SuppressWarnings("deprecation")
                Fragment fragment = Fragment.instantiate(mContext, newTab.clx.getName(), null);
                //缓存起来
                newTab.fragment = fragment;
                ft.add(containerId,fragment,newTab.clx.getName());//提交到fragmentmanager
            }else {
                //从fragment缓存空间中重加载到界面中
                ft.attach(newTab.fragment);
            }
            ft.commit();
            notifyTabSelect(newTab,oldTab);//通知回调
        }
    }

    /**
     * 回调监听器
     * @param newTab 新tab
     * @param oldTab 旧tab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab){
        if (mListener!=null){
            mListener.onTabChanged(newTab,oldTab);
        }
    }
    private void notifyTabReselect(Tab<T> tab){
        //TODO 二次点击tab所做操作
        currentTab = tab;
        Log.e("aa",tab.clx.getName());
    }


    /**
     * 所有Tab基础属性
     *
     * @param <T>
     */
    public static class Tab<T> {
        @SuppressWarnings("WeakerAccess")
        public Class<?> clx;
        //额外的字段，自己设置需要使用
        public T extra;

        @SuppressWarnings("WeakerAccess")
        public Fragment fragment;//内部缓存对应的fragment,外部不能用.package权限

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
    }

    /**
     * 事件处理完回调接口
     *
     * @param <T>
     */
    public interface OnTabChangeListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
