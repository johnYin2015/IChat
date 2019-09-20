package com.huaying.italker.activities;

import android.content.Context;
import android.content.Intent;

import com.huaying.common.app.Activity;
import com.huaying.common.app.Fragment;
import com.huaying.italker.R;
import com.huaying.italker.frags.account.UpdateInfoFragment;

import androidx.annotation.Nullable;

public class AccountActivity extends Activity {

    private Fragment mCurrentFragment;

    /**
     * 账户activity显示入口
     * @param context Context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mCurrentFragment = new UpdateInfoFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container,mCurrentFragment)
                .commit();
    }

    /**
     * 收到剪切图片后成功的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        mCurrentFragment.onActivityResult(requestCode,resultCode,data);
    }
}
