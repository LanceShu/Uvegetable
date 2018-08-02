package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.httputils.DeliverHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
        BaseActivity.sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.SUCCESS_GET_DELIVER_DATE_AND_STATE:
                        break;
                    case BaseActivity.FAILURE_GET_DELIVER_DATE_AND_STATE:
                        String errMsg = (String) msg.obj;
                        BaseActivity.showReminderDialog(MeDeliverActivity.this, errMsg);
                        break;
                }
            }
        };
    }

    private void initWight() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        deliverList.setLayoutManager(manager);
    }

    private void initData() {
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
