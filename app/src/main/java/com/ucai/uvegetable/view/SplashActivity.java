package com.ucai.uvegetable.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.httputils.UserHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

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
        // 检查权限;
        CheckPermission();
    }

    private void CheckPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (permissionList.isEmpty()) {
            // 如果不需要申请权限就直接执行操作;
            new Thread(this::initData).start();
        } else {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permission, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        // 如果权限没有全部通过，则结束程序;
                        finish();
                        break;
                    }
                }
                // 全部权限通过后，执行操作；
                new Thread(this::initData).start();
            } else {
                finish();
            }
        }
    }

    private void initData() {
        long startTime = System.currentTimeMillis();
        isLogined = sharedPreferences.getBoolean("isLogined", false);
        String phone = sharedPreferences.getString("phone", "");
        String pwd = sharedPreferences.getString("pwd", "");
        if (isLogined && !phone.equals("") && !pwd.equals("") && rsaUtils != null) {
            phone = rsaUtils.decodeMessage(phone, rsaUtils.getPriKey());
            pwd = rsaUtils.decodeMessage(pwd, rsaUtils.getPriKey());
            UserHttpUtil.requestLogin(phone, pwd, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    if (cookies.size() > 0) {
                        String session = cookies.get(0);
                        String result = session.substring(0, session.indexOf(";"));
                        Log.e("header", result);
                        cookie = result;
                    }
//                    Log.e("response", resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONObject data = jsonObject.getJSONObject("data");
                        loginBean.setId(data.getString("id"));
                        loginBean.setName(data.getString("name"));
                        loginBean.setAddr(data.getString("addr"));
                        loginBean.setPhone(data.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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
