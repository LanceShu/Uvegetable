package com.ucai.uvegetable.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.httputils.OrderHttps;
import com.ucai.uvegetable.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class OrderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        OrderHttps.getAllUserOrder(BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.e("orderList", resp);
                try {
                    JSONObject res = new JSONObject(resp);
                    JSONArray data = res.getJSONArray("data");
                    Log.e("orderList", data.toString());
                    String date = data.getString(0);
                    Log.e("orderList", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
