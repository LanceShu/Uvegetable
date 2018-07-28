package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.OrderAdapter;
import com.ucai.uvegetable.beans.OrderBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lance
 * on 2018/7/25.
 */

public class HomeToOrderActivity extends BaseActivity {
    @BindView(R.id.hto_recycler_view)
    RecyclerView orderList;

    @BindView(R.id.hto_btn)
    Button orderBtn;

    @BindView(R.id.hto_back)
    ImageView orderBack;

    @BindView(R.id.hto_total_price)
    TextView totalPrice;

    @BindView(R.id.hto_save)
    Button save;

    LinearLayoutManager layoutManager;
    OrderAdapter orderAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_to_order_layout);
        ButterKnife.bind(this);
        // init the data;
        initData();
        // init the wight();
        initWight();
        BaseActivity.sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.UPDATE_TOTAL_PRICE:
                        showTotalPrice(BaseActivity.orderBeans);
                        break;
                }
            }
        };
    }

    private void initWight() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, BaseActivity.orderBeans);
        orderList.setAdapter(orderAdapter);
        orderList.setItemViewCacheSize(100);
        showTotalPrice(BaseActivity.orderBeans);
    }

    @SuppressLint("SetTextI18n")
    private void showTotalPrice(List<OrderBean> orderBeans) {
        double total_price = 0.0;
        for (OrderBean orderBean : orderBeans) {
            total_price += orderBean.getTotalPrice();
        }
        total_price = Math.round(total_price * 100.0) / 100.0;
        String total = String.valueOf(total_price);
        switch (total.length() / 10) {
            case 0:
                totalPrice.setTextSize(18);
                break;
            case 1:
                totalPrice.setTextSize(16);
                break;
            case 2:
                totalPrice.setTextSize(14);
                break;
            case 3:
                totalPrice.setTextSize(12);
                break;
            default:
                break;
        }
        totalPrice.setText(String.valueOf(total) + " 元");
    }

    private void initData() {
        if (BaseActivity.resp != null && BaseActivity.orderBeans.size() == 0) {
            getAllProductList(BaseActivity.resp);
        }
        if (BaseActivity.saveOrderBeans == null) {
            BaseActivity.saveOrderBeans = new ArrayList<>();
        }
        if (BaseActivity.saveOrderBeans.size() == 0) {
            BaseActivity.saveOrderBeans.addAll(BaseActivity.orderBeans);
        }
    }

    private void getAllProductList(String resp) {
        try {
            JSONObject res = new JSONObject(resp);
            JSONArray categories = res.getJSONObject("data").getJSONArray("categories");
            for (int i = 0; i < categories.length(); i++) {
                JSONArray products = categories.getJSONObject(i).getJSONArray("products");
                for (int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);
                    OrderBean orderBean = new OrderBean();
                    orderBean.setProductId(product.getString("id"));
                    orderBean.setPrice(product.getDouble("guestPrice"));
                    orderBean.setPcode(product.getString("pcode"));
                    orderBean.setNum(0);
                    orderBean.setNote(product.getString("note"));
                    orderBean.setName(product.getString("name"));
                    orderBean.setImgfile("http://123.206.13.129:8080/manage/"
                            + product.getString("imgfile"));
                    orderBean.setUnit(product.getString("unit"));
                    orderBean.setTotalPrice(0.0);
                    orderBeans.add(orderBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.hto_btn)
    void orderBtn() {

    }

    @OnClick(R.id.hto_save)
    void saveBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否保存当前的采购单信息？");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.orderBeans.clear();
                BaseActivity.orderBeans.addAll(BaseActivity.saveOrderBeans);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @OnClick(R.id.hto_back)
    void orderBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("退出则当前的采购单信息不保存？");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.orderBeans.clear();
                BaseActivity.saveOrderBeans.clear();
                finish();
            }
        });
        builder.setNegativeButton("再想想", null);
        builder.show();
    }
}
