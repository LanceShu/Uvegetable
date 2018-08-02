package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.DeliverAdapter;
import com.ucai.uvegetable.beans.DeliverBean;
import com.ucai.uvegetable.httputils.DeliverHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2018/8/1.
 */

public class MeDeliverActivity extends AppCompatActivity {
    @BindView(R.id.md_back)
    ImageView back;

    @BindView(R.id.md_recycler)
    RecyclerView deliverList;

    @BindView(R.id.md_refresh)
    SwipeRefreshLayout refresh;

    private DeliverAdapter adapter;
    private List<DeliverBean> deliverBeanList;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_deliver_layout);
        ButterKnife.bind(this);
        // init data;
        initData();
        // init Wight();
        initWight();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onStart() {
        super.onStart();
        BaseActivity.sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.SUCCESS_GET_DELIVER_DATE_AND_STATE:
                        adapter.notifyDataSetChanged();
                        if (BaseActivity.progressDialog.isShowing()) {
                            BaseActivity.displayProgressDialog();
                        }
                        if (refresh.isRefreshing()) {
                            refresh.setRefreshing(false);
                        }
                        break;
                    case BaseActivity.FAILURE_GET_DELIVER_DATE_AND_STATE:
                        String errMsg = (String) msg.obj;
                        BaseActivity.showReminderDialog(MeDeliverActivity.this, errMsg);
                        break;
                    case BaseActivity.SUCCESS_RECEIVE:
                        getAllDeliverDateByUser();
                        break;
                }
            }
        };
    }

    private void initWight() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        deliverList.setLayoutManager(manager);
        adapter = new DeliverAdapter(MeDeliverActivity.this, deliverBeanList);
        deliverList.setAdapter(adapter);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                deliverBeanList.clear();
                getAllDeliverDateByUser();
            }
        });
    }

    private void initData() {
        if (deliverBeanList == null) {
            deliverBeanList = new ArrayList<>();
        }
        deliverBeanList.clear();
        BaseActivity.showProgressDialog(this, "正在获取送货单...");
        // get deliver list;
        getAllDeliverDateByUser();
    }

    private void getAllDeliverDateByUser() {
        DeliverHttpUtil.getAllDates(BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.e("syuban", resp);
                try {
                    JSONObject res = new JSONObject(resp);
                    String msg = res.getString("msg");
                    if (msg.equals("成功")) {
                        JSONArray data = res.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            DeliverBean deliverBean = new DeliverBean();
                            deliverBean.setDate(item.getString("date"));
                            deliverBean.setState(item.getString("state"));
                            deliverBeanList.add(deliverBean);
                        }
                        BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.SUCCESS_GET_DELIVER_DATE_AND_STATE);
                    } else {
                        Message message = Message.obtain();
                        message.obj = msg;
                        message.what = BaseActivity.FAILURE_GET_DELIVER_DATE_AND_STATE;
                        BaseActivity.sendHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.md_back)
    void back() {
        finish();
    }
}
