package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.RequestFuture;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.OrderAdapter;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.beans.OrderBean;
import com.ucai.uvegetable.beans.OrderedProductBean;
import com.ucai.uvegetable.camera.PhotoActivity;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.httputils.OrderHttpUtil;
import com.ucai.uvegetable.utils.Constant;
import com.ucai.uvegetable.utils.Utils;
import com.ucai.uvegetable.volley.MultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import yan.guoqi.palm.LibPalmNative;

/**
 * Created by Lance
 * on 2018/7/25.
 */

public class HomeToOrderActivity extends BaseActivity {
    private static final String TAG = "HomeToOrderActivity";

    @BindView(R.id.hto_recycler_view)
    RecyclerView orderList;

    @BindView(R.id.hto_btn)
    Button orderBtn;

    @BindView(R.id.title_cancel)
    ImageView orderBack;

    @BindView(R.id.title_content)
    TextView content;

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
                    case SCROLL_TO_TOP:
                        orderList.smoothScrollToPosition(0);
                        break;
                }
            }
        };
    }

    private void initWight() {
        content.setText("采购单");
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, BaseActivity.orderedProductBeans);
        orderAdapter.addFooterView(addFooterView(this, R.layout.footer_view));
        orderList.setAdapter(orderAdapter);
        orderList.setItemViewCacheSize(orderedProductBeans.size());
        showTotalPrice(BaseActivity.orderedProductBeans);
    }

    private View addFooterView(Context context, int viewId) {
        return LayoutInflater.from(context).inflate(viewId, null);
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
                    orderedProductBean.setImgfile(HomeFragment.imageUrl
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
        DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> {
            PhotoActivity.goToPhotoActivity(this,
                    PhotoActivity.Source.CERTIFY_PALM, PhotoActivity.REQUEST_CERTIFY);
        };
        showDialog(this, "提示：", "是否提交当前的采购单？",
                "掌纹认证", "再逛逛",
                positiveListener, null);
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
        DialogInterface.OnClickListener posiListener = (((dialogInterface, i) -> {
            BaseActivity.orderedProductBeans.clear();
            BaseActivity.orderedProductBeans.addAll(BaseActivity.saveOrderedProductBeans);
            finish();
        }));
        showDialog(this, "提示：", "是否保存当前的采购单信息？",
                "保存", "取消",
                posiListener, null);
    }

    @OnClick(R.id.title_cancel)
    void orderBack() {
        backToHome();
    }

    private void backToHome() {
        DialogInterface.OnClickListener positiveListener = ((dialogInterface, i) -> {
            BaseActivity.orderedProductBeans.clear();
            BaseActivity.saveOrderedProductBeans.clear();
            finish();
        });
        showDialog(this, "提示：", "退出则当前的采购单信息不保存？",
                "不保存", "再想想",
                positiveListener, null);
    }

    @Override
    public void onBackPressed() {
        backToHome();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoActivity.REQUEST_CERTIFY) {
                // 获取掌纹在本地存储的路径;
                String palmImagePath = data.getStringExtra(PhotoActivity.KEY_IMAGE_PATH);
                // 验证掌纹;
                cerltifyPalm(palmImagePath);
            }
        }
    }

    /**
     * 验证掌纹;
     * @param path
     * */
    private void cerltifyPalm(String path){
        new CertifyPalmTask(this, loginBean).execute(path);
    }

    // 验证掌纹，开启AsyncTask异步操作;
    @SuppressLint("StaticFieldLeak")
    class CertifyPalmTask extends AsyncTask<String, Void, String> {
        private long mStartTime;
        private Context context;
        private LoginBean loginBean;
        private ProgressDialog mProgressDialog;

        CertifyPalmTask(Context context, LoginBean loginBean) {
            this.context = context;
            this.loginBean = loginBean;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = showProDialog(context, "正在认证");
            mStartTime = System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProDialog(mProgressDialog);
            Log.e("certity_plam", "success: " + s);
            long curr = System.currentTimeMillis();
            showCertifyResult(s, (curr - mStartTime));
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            String roi = LibPalmNative.getRoi(path); //生成roi
//            String roi = Constant.ROI_PATH + "2883.jpg";
            File f = new File(roi);
            if(!f.exists()){
                return null;
            }
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("userId", loginBean.getPhone());
            urlParams.put("type", "certify");

            RequestFuture<String> future = RequestFuture.newFuture();
            MultipartRequest request = new MultipartRequest(Constant.UPLOAD, future,
                    future, "file", f, urlParams);
            getRequestQueue().add(request);
            String result = "";
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "roi = " + roi);
            return result;
        }
    }

    // 展示认证的结果;
    private void showCertifyResult(String s, long timeCost){
        try {
            JSONObject jsonObject = new JSONObject(s);
            double diff = jsonObject.getDouble("data");
            float dd = Utils.formatDouble(diff);
            // title为认证结果;
            String title;
            if(Utils.isCertifySuccess(dd)){
                title = getString(R.string.certify_success_alert_title);
                showCertifySuccess(title);
            }else{
                title = getString(R.string.certify_fail_alert_title);
                DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> {
                    PhotoActivity.goToPhotoActivity(this,
                            PhotoActivity.Source.CERTIFY_PALM, PhotoActivity.REQUEST_CERTIFY);
                };
                showDialog(this, "提示：", title + "，采购单提交失败...",
                        "再试一次", "取消",
                        positiveListener, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 认证成功后，处理的逻辑;
    private void showCertifySuccess(String content) {
        showProgressDialog(HomeToOrderActivity.this, content + "，正在提交采购单中...");
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
}
