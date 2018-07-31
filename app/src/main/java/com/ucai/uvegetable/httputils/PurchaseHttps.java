package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/7/31.
 */

public class PurchaseHttps {
    public static void getOneWithCategoryByDateAndState(String cookie, String date
            , String state, Callback callback) {
        String url = "http://123.206.13.129:8060/guest/order/findOneWithCategoryByDateAndState";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + "?date=" + date + "&state=" + state)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void backOrder(String cookie, String date, Callback callback) {
        String url = "http://123.206.13.129:8060/guest/order/back";
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
