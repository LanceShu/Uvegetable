package com.ucai.uvegetable.httputils;

import android.content.Context;
import android.util.Log;

import com.ucai.uvegetable.beans.LoginBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/4/16.
 */

public class PalmHttpUtils {
    private static final String TAG = "PalmHttpUtils";

    public static void requestToGetAllPalmList(LoginBean loginBean, String cookie, Callback callback) {
        if (loginBean != null) {
            String httpUrl = "http://123.206.13.129:8080/palm/list?userId=" + loginBean.getPhone();
            Log.e(TAG, httpUrl);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("Cookie", cookie)
                    .build();
            client.newCall(request).enqueue(callback);
        }
    }
}
