package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.PurchaseInforAdapter;
import com.ucai.uvegetable.beans.ProductBean;
import com.ucai.uvegetable.httputils.PurchaseHttpUtil;

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
 * on 2018/7/30.
 */

public class PurchaseInforActivity extends AppCompatActivity {
    @BindView(R.id.pi_back)
    ImageView back;

    @BindView(R.id.pi_infor_layout)
    RelativeLayout inforLayout;

    @BindView(R.id.pi_date)
    TextView date;

    @BindView(R.id.pi_state)
    TextView state;

    @BindView(R.id.pi_total_price)
    TextView total;

    @BindView(R.id.pi_correct_btn)
    Button correct;

    @BindView(R.id.pi_cancel_btn)
    Button cancel;

    @BindView(R.id.pi_recycler)
    RecyclerView recyclerView;

    private List<ProductBean> productBeans;
    private PurchaseInforAdapter adapter;
    private String pdate;
    private String pstate;
    private double totalPrice = 0.0;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_infor_layout);
        ButterKnife.bind(this);
        // init data;
        initData();
        // init wight;
        initWight();
        BaseActivity.sendHandler = new Handler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.SUCCESS_GET_PI_INFOR:
                        totalPrice = Math.round(totalPrice * 100.0) / 100.0;
                        total.setText("总价: " + String.valueOf(totalPrice) + " 元");
                        adapter.notifyDataSetChanged();
                        BaseActivity.displayProgressDialog();
                        break;
                    case BaseActivity.FAILURE_GET_PI_INFOR:
                        BaseActivity.displayProgressDialog();
                        String err = (String) msg.obj;
                        BaseActivity.showReminderDialog(PurchaseInforActivity.this, err);
                        break;
                    case BaseActivity.BACK_ORDER_MSG:
                        Bundle bundle = (Bundle) msg.obj;
                        String content = bundle.getString("msg");
                        BaseActivity.showReminderDialog(PurchaseInforActivity.this, content);
                        inforLayout.setBackgroundColor(Color.parseColor("#896e00"));
                        state.setText("待退回");
                        cancel.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    private void initWight() {
        recyclerView = findViewById(R.id.pi_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new PurchaseInforAdapter(this, productBeans);
        recyclerView.setAdapter(adapter);
        date.setText(pdate);
        switch (pstate.charAt(0)) {
            case '1':
                inforLayout.setBackgroundColor(Color.parseColor("#001b89"));
                state.setText("新采购单");
                break;
            case '2':
                inforLayout.setBackgroundColor(Color.parseColor("#896e00"));
                state.setText("待退回");
                break;
            case '3':
                inforLayout.setBackgroundColor(Color.parseColor("#89000e"));
                state.setText("已退回");
                break;
            case '4':
                inforLayout.setBackgroundColor(Color.parseColor("#178900"));
                state.setText("已发货");
                break;
        }
        cancel.setVisibility(pstate.charAt(0) == '1' ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        if (productBeans == null)
            productBeans = new ArrayList<>();
        productBeans.clear();
        Bundle bundle = getIntent().getBundleExtra("data");
        pdate = bundle.getString("date");
        pstate = bundle.getString("state");
        Log.e("syuban", pdate + "  " + pstate);
        BaseActivity.showProgressDialog(this, "正在加载订单数据...");
        getOneInforByDateAndState(BaseActivity.cookie, pdate, pstate);
    }

    private void getOneInforByDateAndState(String cookie, String date, String state) {
        PurchaseHttpUtil.getOneWithCategoryByDateAndState(cookie, date, state, new Callback() {
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
                    try {
                        JSONObject res = new JSONObject(resp);
                        String msg = res.getString("msg");
                        if (msg.equals("成功")) {
                            JSONArray categories = res.getJSONObject("data").getJSONArray("categories");
                            for (int i = 0; i < categories.length(); i++) {
                                JSONArray products = categories.getJSONObject(i).getJSONArray("products");
                                for (int j = 0; j < products.length(); j++) {
                                    ProductBean productBean = new ProductBean();
                                    JSONObject product = products.getJSONObject(j);
                                    productBean.setId(product.getString("id"));
                                    productBean.setName(product.getString("name"));
                                    productBean.setUnit(product.getString("unit"));
                                    productBean.setUser_price(product.getDouble("price"));
                                    productBean.setNum(product.getDouble("num"));
                                    double amount = product.getDouble("amount");
                                    totalPrice += amount;
                                    productBean.setPrice(amount);
                                    productBean.setImgfile(product.getString("imgfile"));
                                    productBean.setNote(product.getString("note"));
                                    productBean.setPcode(product.getString("pcode"));
                                    productBeans.add(productBean);
                                }
                            }
                            BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.SUCCESS_GET_PI_INFOR);
                        } else {
                            Message message = Message.obtain();
                            message.obj = msg;
                            message.what = BaseActivity.FAILURE_GET_PI_INFOR;
                            BaseActivity.sendHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @OnClick(R.id.pi_back)
    void back() {
        finish();
    }

    @OnClick(R.id.pi_correct_btn)
    void correct() {
        finish();
    }

    @OnClick(R.id.pi_cancel_btn)
    void cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否取消当前订单？");
        builder.setNegativeButton("再想想", null);
        builder.setPositiveButton("取消订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backOrderByDate(BaseActivity.cookie, pdate);
            }
        });
        builder.show();
    }

    private void backOrderByDate(String cookie, String pdate) {
        PurchaseHttpUtil.backOrder(cookie, pdate, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, @Nullable Response response) throws IOException {
                if (response != null) {
                    String resp = response.body().string();
                    Log.e("syuban", resp);
                    try {
                        JSONObject res = new JSONObject(resp);
                        String msg = res.getString("msg");
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", msg);
                        bundle.putString("date", pdate);
                        message.obj = bundle;
                        message.what = BaseActivity.BACK_ORDER_MSG;
                        BaseActivity.sendHandler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
