package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/8/1.
 */

public class DeliverHttpUtil {
    public static void getAllDates(String cookie, Callback callback) {
        String url = "http://123.206.13.129:8060/guest/deliver/findDatesAndState";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
