package com.ucai.uvegetable.httputils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/7/18.
 */

public class ProductHttps {
    public static void findCategoryList(String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/category/findListWithProducts")
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getUserPrice(String cookie, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/pricelist/findLatest")
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
