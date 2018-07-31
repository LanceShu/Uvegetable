package com.ucai.uvegetable.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.PurchaseAdapter;
import com.ucai.uvegetable.beans.PurchaseBean;
import com.ucai.uvegetable.httputils.OrderHttps;
import com.ucai.uvegetable.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class OrderFragment extends Fragment {
    private List<PurchaseBean> purchaseBeanList;
    private int count = 0;
    private PurchaseAdapter adapter;

    @BindView(R.id.of_recycler)
    RecyclerView purchaseRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        ButterKnife.bind(this, view);
        // init data;
        initData();
        // init Wight();
        initWight();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initWight() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        purchaseRecycler.setLayoutManager(manager);
        adapter = new PurchaseAdapter(getContext(), purchaseBeanList);
        purchaseRecycler.setAdapter(adapter);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity.sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.FAILURE_GET_DATE:
                        String errMsg = (String) msg.obj;
                        BaseActivity.showReminderDialog(getContext(), errMsg);
                        break;
                    case BaseActivity.SUCCESS_GET_DATE_AND_STATE:
                        adapter.notifyDataSetChanged();
                        BaseActivity.displayProgressDialog();
                        break;
                }
            }
        };
    }

    private void initData() {
        if (purchaseBeanList == null) {
            purchaseBeanList = new ArrayList<>();
        }
        purchaseBeanList.clear();
        getAllOrderDate();
    }

    private void getAllOrderDate() {
        BaseActivity.showProgressDialog(getContext(), "正在加载订单...");
        OrderHttps.getAllUserOrderDates(BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(@Nullable Call call, @Nullable IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(@Nullable Call call, @Nullable Response response) throws IOException {
                if (response != null) {
                    String resp = response.body().string();
                    Log.e("syuban", resp);
                    if (resp != null) {
                        try {
                            JSONObject res = new JSONObject(resp);
                            count = res.getInt("count");
                            String msg = res.getString("msg");
                            if (msg.equals("成功")) {
                                JSONArray data = res.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject dateData = data.getJSONObject(i);
                                    String date = dateData.getString("date");
                                    JSONArray states = dateData.getJSONArray("states");
                                    for (int j = 0; j < states.length(); j++) {
                                        PurchaseBean purchaseBean = new PurchaseBean();
                                        purchaseBean.setDate(date);
                                        purchaseBean.setState(states.getString(j));
                                        purchaseBeanList.add(purchaseBean);
                                    }
                                }
                                BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.SUCCESS_GET_DATE_AND_STATE);
                            } else {
                                Message message = Message.obtain();
                                message.obj = msg;
                                message.what = BaseActivity.FAILURE_GET_DATE;
                                BaseActivity.sendHandler.sendMessage(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
