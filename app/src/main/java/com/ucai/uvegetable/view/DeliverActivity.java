package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.DriverBean;
import com.ucai.uvegetable.httputils.DeliverHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2018/8/2.
 */

public class DeliverActivity extends AppCompatActivity {
    private String date;
    private String state;
    private DriverBean driverBean;

    @BindView(R.id.title_cancel)
    ImageView dback;

    @BindView(R.id.title_content)
    TextView content;

    @BindView(R.id.deliver_name)
    TextView dname;

    @BindView(R.id.deliver_id)
    TextView did;

    @BindView(R.id.deliver_mobile)
    TextView dmobile;

    @BindView(R.id.deliver_note)
    TextView dnote;

    @BindView(R.id.deliver_state)
    TextView dstate;

    @BindView(R.id.order_date)
    TextView dorderDate;

    @BindView(R.id.deliver_date)
    TextView ddeliverDate;

    @BindView(R.id.deliver_look)
    Button dlook;

    @BindView(R.id.deliver_correct_btn)
    Button dcorrct;

    @BindView(R.id.deliver_receive_btn)
    Button dreceive;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliver_layout);
        ButterKnife.bind(this);
        // initData()
        initData();
        // initWight();
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
                    case BaseActivity.SUCCESS_GET_DRIVER:
                        dname.setText(driverBean.getName());
                        did.setText(driverBean.getCardid());
                        dmobile.setText(driverBean.getMobile());
                        dnote.setText(driverBean.getNote());
                        if (state.equals("1")) {
                            dstate.setTextColor(Color.parseColor("#c79145"));
                            dstate.setText("送货中");
                        } else if(state.equals("2")) {
                            dstate.setTextColor(Color.parseColor("#3abf93"));
                            dstate.setText("已收货");
                        }
                        dorderDate.setText(driverBean.getOrderDate());
                        ddeliverDate.setText(driverBean.getDeliverDate());
                        BaseActivity.displayProgressDialog();
                        break;
                    case BaseActivity.FAILURE_GET_DRIVER:
                        BaseActivity.displayProgressDialog();
                        String err = (String) msg.obj;
                        BaseActivity.showReminderDialog(DeliverActivity.this, err);
                        break;
                    case BaseActivity.FAILURE_RECEIVE:
                        BaseActivity.displayProgressDialog();
                        String errMsg = (String) msg.obj;
                        BaseActivity.showReminderDialog(DeliverActivity.this, errMsg);
                        break;
                    case BaseActivity.SUCCESS_RECEIVE:
                        BaseActivity.displayProgressDialog();
                        BaseActivity.showReminderDialog(DeliverActivity.this, "确认收货成功!");
                        dreceive.setVisibility(View.GONE);
                        dstate.setTextColor(Color.parseColor("#3abf93"));
                        dstate.setText("已收货");
                        break;
                }
            }
        };
    }

    private void initWight() {
        content.setText("送货单");
        dreceive.setVisibility(state.equals("1") ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        if (driverBean == null)
            driverBean = new DriverBean();
        date = getIntent().getBundleExtra("data").getString("date");
        state = getIntent().getBundleExtra("data").getString("state");
        BaseActivity.showProgressDialog(this, "获取司机信息中...");
        getOneByDate(date, BaseActivity.cookie);
    }

    private void getOneByDate(String date, String cookie) {
        DeliverHttpUtil.getOneWithCategoryByDate(date, cookie, new Callback() {
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
                        JSONObject datas = res.getJSONObject("data");
                        driverBean.setDeliverDate(datas.getString("deliverDate"));
                        driverBean.setOrderDate(datas.getString("orderDate"));
                        JSONObject driver = datas.getJSONObject("driver");
                        driverBean.setId(driver.getInt("id"));
                        driverBean.setName(driver.getString("name"));
                        driverBean.setCardid(driver.getString("cardid"));
                        driverBean.setMobile(driver.getString("mobile"));
                        driverBean.setNote(driver.getString("note"));
                        BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.SUCCESS_GET_DRIVER);
                    } else {
                        Message message = Message.obtain();
                        message.obj = msg;
                        message.what = BaseActivity.FAILURE_GET_DRIVER;
                        BaseActivity.sendHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.title_cancel)
    void back() {
        finish();
    }

    @OnClick(R.id.deliver_look)
    void look() {
        Intent intent = new Intent(this, PurchaseInforActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        if (state.equals("1")) {
            state = "4";
        } else if(state.equals("2")) {
            state = "4";
        }
        bundle.putString("state", state);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    @OnClick(R.id.deliver_correct_btn)
    void correct() {
        finish();
    }

    @OnClick(R.id.deliver_receive_btn)
    void receive() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否确认收货？");
        builder.setNegativeButton("再看看", null);
        builder.setPositiveButton("确认收货", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.showProgressDialog(DeliverActivity.this, "正在确认收货...");
                DeliverHttpUtil.receive(date, BaseActivity.cookie, new Callback() {
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
                            Message message = Message.obtain();
                            message.obj = msg;
                            if (msg.equals("确认收货成功")) {
                                message.what = BaseActivity.SUCCESS_RECEIVE;
                            } else {
                                message.what = BaseActivity.FAILURE_RECEIVE;
                            }
                            BaseActivity.sendHandler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        builder.show();
    }
}
