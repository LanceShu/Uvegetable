package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.RequestFuture;
import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.camera.PhotoActivity;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.fragment.MeFragment;
import com.ucai.uvegetable.fragment.OrderFragment;
import com.ucai.uvegetable.utils.Constant;
import com.ucai.uvegetable.utils.FragmentUtils;
import com.ucai.uvegetable.utils.Utils;
import com.ucai.uvegetable.volley.MultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import yan.guoqi.palm.LibPalmNative;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{
    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private final int parentGroupId = R.id.content_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init data;
        initData();
        // init Wight();
        initWight();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
    }

    private void initWight() {
        if (!isLogined) {
            showLoginDialog(this, MAINACTIVITY);
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        FragmentUtils.addFragment(fragmentManager, new HomeFragment(), parentGroupId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                FragmentUtils.replaceFragment(fragmentManager,
                        new HomeFragment(), parentGroupId);
                return true;
            case R.id.navigation_order:
                FragmentUtils.replaceFragment(fragmentManager,
                        new OrderFragment(), parentGroupId);
                return true;
            case R.id.navigation_me:
                FragmentUtils.replaceFragment(fragmentManager,
                        new MeFragment(), parentGroupId);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoActivity.REQUEST_CERTIFY) {
                // 获取掌纹在本地存储的路径;
                String palmImagePath = data.getStringExtra(PhotoActivity.KEY_IMAGE_PATH);
                // 验证掌纹;
                cerltifyPalm(palmImagePath);
            }
        }
    }

    /**
     * 验证掌纹;
     * @param path
     * */
    private void cerltifyPalm(String path){
        new CertifyPalmTask(this, loginBean).execute(path);
    }

    // 验证掌纹，开启AsyncTask异步操作;
    @SuppressLint("StaticFieldLeak")
    class CertifyPalmTask extends AsyncTask<String, Void, String> {
        private long mStartTime;
        private Context context;
        private LoginBean loginBean;
        private ProgressDialog mProgressDialog;

        CertifyPalmTask(Context context, LoginBean loginBean) {
            this.context = context;
            this.loginBean = loginBean;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = showProDialog(context, "正在认证");
            mStartTime = System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProDialog(mProgressDialog);
            Log.e("certity_plam", "success: " + s);
            long curr = System.currentTimeMillis();
            showCertifyResult(s, (curr - mStartTime));
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            String roi = LibPalmNative.getRoi(path); //生成roi
//            String roi = Constant.ROI_PATH + "2883.jpg";
            File f = new File(roi);
            if(!f.exists()){
                return null;
            }
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("userId", loginBean.getPhone());
            urlParams.put("type", "certify");

            RequestFuture<String> future = RequestFuture.newFuture();
            MultipartRequest request = new MultipartRequest(Constant.UPLOAD, future,
                    future, "file", f, urlParams);
            getRequestQueue().add(request);
            String result = "";
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "roi = " + roi);
            return result;
        }
    }

    // 展示认证的结果;
    private void showCertifyResult(String s, long timeCost){
        try {
            JSONObject jsonObject = new JSONObject(s);
            double diff = jsonObject.getDouble("data");
            float dd = Utils.formatDouble(diff);
            String title, content;
            if(Utils.isCertifySuccess(dd)){
                title = getString(R.string.certify_success_alert_title);
                content = getString(R.string.certify_success_alert_content, timeCost);
            }else{
                title = getString(R.string.certify_fail_alert_title);
                content = getString(R.string.certify_fail_alert_content, timeCost);
            }
            showAlertDialog(title, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 弹出dialog提示框;
    private void showAlertDialog(String title, String msg){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {

    }
}
