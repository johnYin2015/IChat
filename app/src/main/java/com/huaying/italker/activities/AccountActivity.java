package com.huaying.italker.activities;

import android.content.Context;
import android.content.Intent;

import com.huaying.common.app.Activity;
import com.huaying.italker.R;
import com.huaying.italker.frags.account.UpdateInfoFragment;

public class AccountActivity extends Activity {

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
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container,new UpdateInfoFragment())
                .commit();
    }
}
