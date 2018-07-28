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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.OrderAdapter;
import com.ucai.uvegetable.beans.OrderBean;
import com.ucai.uvegetable.beans.ProductPriceBean;
import com.ucai.uvegetable.httputils.OrderHttps;
import com.ucai.uvegetable.utils.ToastUtil;

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
                    case UPDATE_TOTAL_PRICE:
                        showTotalPrice(BaseActivity.productPriceBeans);
                        break;
                    case SUCCESS_SEND_ORDER:
                        displayProgressDialog();
                        ToastUtil.show(HomeToOrderActivity.this, "采购单提交成功");
                        break;
                    case FAILURE_SEND_ORDER:
                        displayProgressDialog();
                        String errorContent = (String) msg.obj;
                        ToastUtil.show(HomeToOrderActivity.this, errorContent);
                        break;
                }
            }
        };
    }

    private void initWight() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, BaseActivity.productPriceBeans);
        orderList.setAdapter(orderAdapter);
        orderList.setItemViewCacheSize(100);
        showTotalPrice(BaseActivity.productPriceBeans);
    }

    @SuppressLint("SetTextI18n")
    private void showTotalPrice(List<ProductPriceBean> productPriceBeans) {
        double total_price = 0.0;
        for (ProductPriceBean productPriceBean : productPriceBeans) {
            total_price += productPriceBean.getTotalPrice();
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
        if (BaseActivity.resp != null && BaseActivity.productPriceBeans.size() == 0) {
            getAllProductList(BaseActivity.resp);
        }
        if (BaseActivity.saveProductPriceBeans == null) {
            BaseActivity.saveProductPriceBeans = new ArrayList<>();
        }
        if (BaseActivity.saveProductPriceBeans.size() == 0) {
            BaseActivity.saveProductPriceBeans.addAll(BaseActivity.productPriceBeans);
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
                    ProductPriceBean productPriceBean = new ProductPriceBean();
                    productPriceBean.setProductId(product.getString("id"));
                    productPriceBean.setPrice(product.getDouble("guestPrice"));
                    productPriceBean.setPcode(product.getString("pcode"));
                    productPriceBean.setNum(0);
                    productPriceBean.setNote(product.getString("note"));
                    productPriceBean.setName(product.getString("name"));
                    productPriceBean.setImgfile("http://123.206.13.129:8080/manage/"
                            + product.getString("imgfile"));
                    productPriceBean.setUnit(product.getString("unit"));
                    productPriceBean.setTotalPrice(0.0);
                    productPriceBeans.add(productPriceBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.hto_btn)
    void orderBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否提交当前的采购单？");
        builder.setPositiveButton("下单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog(HomeToOrderActivity.this, "正在提交采购单中...");
                String orderListJson = objectToJson(saveProductPriceBeans);
                Log.e("json", orderListJson);
                OrderHttps.submitNewOrderList(orderListJson, cookie, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.e("json", resp);
                        try {
                            JSONObject res = new JSONObject(resp);
                            String msg = res.getString("msg");
                            if (msg.equals("创建采购单成功")) {
                                sendHandler.sendEmptyMessage(SUCCESS_SEND_ORDER);
                            } else {
                                Message message = Message.obtain();
                                message.obj = msg;
                                message.what = FAILURE_SEND_ORDER;
                                sendHandler.sendMessage(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("再逛逛", null);
        builder.show();
    }

    private String objectToJson(List<ProductPriceBean> saveProductPriceBeans) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        for (ProductPriceBean productPriceBean : saveProductPriceBeans) {
            OrderBean orderBean = new OrderBean();
            orderBean.setNote(productPriceBean.getNote());
            orderBean.setNum(productPriceBean.getNum());
            orderBean.setPrice(productPriceBean.getPrice());
            orderBean.setProductId(productPriceBean.getProductId());
            orderBeanList.add(orderBean);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(orderBeanList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @OnClick(R.id.hto_save)
    void saveBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否保存当前的采购单信息？");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.productPriceBeans.clear();
                BaseActivity.productPriceBeans.addAll(BaseActivity.saveProductPriceBeans);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @OnClick(R.id.hto_back)
    void orderBack() {
        backToHome();
    }

    private void backToHome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("退出则当前的采购单信息不保存？");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.productPriceBeans.clear();
                BaseActivity.saveProductPriceBeans.clear();
                finish();
            }
        });
        builder.setNegativeButton("再想想", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        backToHome();
    }
}
