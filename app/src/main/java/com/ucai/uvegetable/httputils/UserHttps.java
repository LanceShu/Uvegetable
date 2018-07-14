package com.ucai.uvegetable.httputils;

import com.ucai.uvegetable.beans.RegisterBean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class UserHttps {
    public static void requestRegister(RegisterBean registerBean, Callback callback){
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("pwd", registerBean.getPwd())
                .add("repwd", registerBean.getPwd())
                .add("name", registerBean.getName())
                .add("addr", registerBean.getAddr())
                .add("phone", registerBean.getPhone())
                .build();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/user/register")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void requestLogin(String phone, String pwd, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("phone", phone)
                .add("pwd", pwd)
                .build();
        Request request = new Request.Builder()
                .url("http://123.206.13.129:8060/guest/user/login")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
