package com.ucai.uvegetable.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ucai.uvegetable.beans.LoginBean;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class BaseActivity extends AppCompatActivity {
    public static boolean isLogined;
    public SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static Handler postHandler;
    public static LoginBean loginBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postHandler = new Handler();
        if (loginBean == null) {
            loginBean = new LoginBean();
        }
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = sharedPreferences.edit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
