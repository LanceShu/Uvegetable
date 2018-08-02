package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.FormBody;
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

    public static void getOneWithCategoryByDate(String date, String cookie, Callback callback) {
        String url = "http://123.206.13.129:8060/guest/deliver/findOneWithCategoryByDate?date=";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + date)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void receive(String date, String cookie, Callback callback) {
        String url = "http://123.206.13.129:8060/guest/deliver/receive";
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
