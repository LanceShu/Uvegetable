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
import com.ucai.uvegetable.beans.OrderedProductBean;
import com.ucai.uvegetable.httputils.OrderHttpUtil;
import com.ucai.uvegetable.utils.ProductUtil;

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
    private String priceResp;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hto_layout);
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
                        showTotalPrice(BaseActivity.orderedProductBeans);
                        break;
                    case SUCCESS_SEND_ORDER:
                        displayProgressDialog();
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeToOrderActivity.this);
                        builder.setTitle("提示：");
                        builder.setMessage("采购单提交成功!");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                        break;
                    case FAILURE_SEND_ORDER:
                        displayProgressDialog();
                        String errorContent = (String) msg.obj;
                        showReminderDialog(HomeToOrderActivity.this, errorContent);
                        break;
                    case GET_USER_PRICELIST:
                        break;
                }
            }
        };
    }

    private void initWight() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, BaseActivity.orderedProductBeans);
        orderList.setAdapter(orderAdapter);
        orderList.setItemViewCacheSize(100);
        showTotalPrice(BaseActivity.orderedProductBeans);
    }

    @SuppressLint("SetTextI18n")
    private void showTotalPrice(List<OrderedProductBean> orderedProductBeans) {
        double total_price = 0.0;
        for (OrderedProductBean orderedProductBean : orderedProductBeans) {
            total_price += orderedProductBean.getTotalPrice();
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
        if (resp != null && orderedProductBeans.size() == 0) {
//            getLatestPricelist(cookie);
            getAllProductList(resp);
        }
        if (saveOrderedProductBeans == null) {
            saveOrderedProductBeans = new ArrayList<>();
        }
        if (saveOrderedProductBeans.size() == 0) {
            saveOrderedProductBeans.addAll(orderedProductBeans);
        }
    }

    private void getLatestPricelist(String cookie) {
        OrderHttpUtil.getLatestPricelistWithNumAndCategory(cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.e("syubanlatest", resp);
                Message message = Message.obtain();
                message.obj = resp;
                message.what = GET_USER_PRICELIST;
                sendHandler.sendMessage(message);
            }
        });
    }

    private void getAllProductList(String resp) {
        try {
            JSONObject res = new JSONObject(resp);
            JSONArray categories = res.getJSONObject("data").getJSONArray("categories");
            for (int i = 0; i < categories.length(); i++) {
                JSONArray products = categories.getJSONObject(i).getJSONArray("products");
                for (int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);
                    OrderedProductBean orderedProductBean = new OrderedProductBean();
                    orderedProductBean.setProductId(product.getString("id"));
                    double price = product.getDouble("guestPrice");
                    orderedProductBean.setPrice(price);
                    orderedProductBean.setPcode(product.getString("pcode"));
                    double num = product.getDouble("num");
                    orderedProductBean.setNum(num);
                    orderedProductBean.setNote(product.getString("note"));
                    orderedProductBean.setName(product.getString("name"));
                    orderedProductBean.setImgfile("http://123.206.13.129:8080/manage/"
                            + product.getString("imgfile"));
                    orderedProductBean.setUnit(product.getString("unit"));
                    double total = Math.round(price * num * 100.0) / 100.0;
                    orderedProductBean.setTotalPrice(total);
                    orderedProductBeans.add(orderedProductBean);
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
                String orderListJson = objectToJson(saveOrderedProductBeans);
                Log.e("json", orderListJson);
                OrderHttpUtil.submitNewOrderList(orderListJson, cookie, new Callback() {
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

    private String objectToJson(List<OrderedProductBean> saveOrderedProductBeans) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        for (OrderedProductBean orderedProductBean : saveOrderedProductBeans) {
            OrderBean orderBean = new OrderBean();
            orderBean.setNote(orderedProductBean.getNote());
            orderBean.setNum(orderedProductBean.getNum());
            orderBean.setPrice(orderedProductBean.getPrice());
            orderBean.setProductId(orderedProductBean.getProductId());
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
                BaseActivity.orderedProductBeans.clear();
                BaseActivity.orderedProductBeans.addAll(BaseActivity.saveOrderedProductBeans);
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
        builder.setPositiveButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.orderedProductBeans.clear();
                BaseActivity.saveOrderedProductBeans.clear();
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
