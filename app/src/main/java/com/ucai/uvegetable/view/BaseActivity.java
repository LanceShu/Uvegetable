package com.ucai.uvegetable.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class BaseActivity extends AppCompatActivity {
    public static boolean isLogined;
    public SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
