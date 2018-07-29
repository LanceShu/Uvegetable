package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/7/28.
 */

public class OrderHttps {
    public static void submitNewOrderList(String orderListJson, String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("products", orderListJson)
                .build();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/order/new")
                .addHeader("Cookie", cookie)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getAllUserOrder(String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/order/findDates")
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
