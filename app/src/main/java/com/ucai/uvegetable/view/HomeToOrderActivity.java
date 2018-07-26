package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.OrderAdapter;
import com.ucai.uvegetable.beans.OrderBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lance
 * on 2018/7/25.
 */

public class HomeToOrderActivity extends BaseActivity {
    @BindView(R.id.hto_recycler_view)
    RecyclerView orderList;

    @BindView(R.id.hto_btn)
    Button orderBtn;

    LinearLayoutManager layoutManager;
    OrderAdapter orderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_to_order_layout);
        ButterKnife.bind(this);
        // init the data;
        initData();
        // init the wight();
        initWight();
    }

    private void initWight() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, BaseActivity.orderBeans);
        orderList.setAdapter(orderAdapter);
    }

    private void initData() {
        if (BaseActivity.resp != null) {
            getAllProductList(BaseActivity.resp);
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
}
