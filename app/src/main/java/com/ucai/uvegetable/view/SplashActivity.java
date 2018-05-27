package com.ucai.uvegetable.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.ucai.uvegetable.R;

/**
 * Created by Lance
 * on 2018/5/26.
 */

public class SplashActivity extends BaseActivity {

    private static final int timeMin = 1500;
    private Handler postHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.splash_layout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }

    private void initData() {
        long startTime = System.currentTimeMillis();
        isLogined = sharedPreferences.getBoolean("isLogined", false);
        long loadingTime = System.currentTimeMillis() - startTime;
        if (loadingTime < timeMin) {
            postHandler.postDelayed(startMainActivity, timeMin - loadingTime);
        } else {
            postHandler.post(startMainActivity);
        }
    }

    private Runnable startMainActivity = new Runnable() {
        @Override
        public void run() {
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };
}
