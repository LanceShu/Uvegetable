package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/7/28.
 */

public class OrderHttpUtil {
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

    public static void getAllUserOrderDates(String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/order/findDatesAndStates")
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getAllStateByDate(String cookie, String date, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://123.206.13.129:8060/guest/order/findStatesByDate?date="+date;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getLatestPricelistWithNumAndCategory(String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://123.206.13.129:8060/guest/order/findLatestPricelistWithNumAndCategory";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
