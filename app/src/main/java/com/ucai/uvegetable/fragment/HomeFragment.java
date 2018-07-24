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
import android.widget.Button;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.HomeProductAdapter;
import com.ucai.uvegetable.httputils.ProductHttps;
import com.ucai.uvegetable.utils.ProductUtil;
import com.ucai.uvegetable.utils.ToastUtil;
import com.ucai.uvegetable.view.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class HomeFragment extends Fragment {
    @BindView(R.id.home_product_list)
    RecyclerView productListView;

    @BindView(R.id.home_order_btn)
    Button order;

    @BindView(R.id.home_category_vegetable)
    TextView categoryVegetable;

    @BindView(R.id.home_category_meat)
    TextView categoryMeat;

    @BindView(R.id.home_category_fish)
    TextView categoryFish;

    @BindView(R.id.home_category_oil)
    TextView categoryOil;

    @BindView(R.id.home_category_goods)
    TextView categoryGoods;

    @BindView(R.id.home_notify_content)
    TextView notifyContent;

    @BindView(R.id.home_notify_login)
    TextView notifyLogin;

    LinearLayoutManager manager;
    HomeProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        if (BaseActivity.currentProducts == null) {
            BaseActivity.currentProducts = new ArrayList<>();
        }
        initWight();
        return view;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.GET_RESPONSE_FROM_SERVER:
                        Log.e("categoryGET", "success   " + BaseActivity.isHas);
                        updateAdapterData(1, BaseActivity.isHas);
                        BaseActivity.postHandler.postDelayed(BaseActivity::displayProgressDialog, 1000);
                        break;
                    case BaseActivity.NO_GET_USER_PRICE:
                        getMarketPrice();
                        break;
                    case BaseActivity.GET_USER_PRICE:
                        updateAdapterData(1, BaseActivity.isHas);
                        BaseActivity.postHandler.postDelayed(BaseActivity::displayProgressDialog, 1000);
                        break;
                    case BaseActivity.UPDATE_HOMEFRAGMENT:
                        ToastUtil.show(getContext(), "登录成功");
                        BaseActivity.loginDialog.dismiss();
                        initWight();
                        break;
                }
            }
        };
    }

    private void initWight() {
        selectedItem(categoryMeat);
        notifyLogin.setVisibility(BaseActivity.isLogined ? View.GONE : View.VISIBLE);
        notifyContent.setVisibility(BaseActivity.isLogined ? View.GONE : View.VISIBLE);
        productListView.setVisibility(BaseActivity.isLogined ? View.VISIBLE : View.GONE);
        if (BaseActivity.isLogined) {
            loginedToLoadData();
        }
    }

    private void loginedToLoadData() {
        loadProductList();
        if (BaseActivity.currentProducts.size() == 0) {
            BaseActivity.showProgressDialog(getContext(), "正在加载数据，请稍后...");
            initData();
        } else {
            updateAdapterData(1, BaseActivity.isHas);
        }
    }

    private void selectedItem(TextView tv) {
        tv.setBackgroundColor(getResources().getColor(R.color.blue_1));
        tv.setTextColor(getResources().getColor(R.color.white_2));
    }

    private void unselectedItem(TextView tv) {
        tv.setBackgroundColor(getResources().getColor(R.color.white_2));
        tv.setTextColor(getResources().getColor(R.color.black_1));
    }

    private void loadProductList() {
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        productListView.setLayoutManager(manager);
        adapter = new HomeProductAdapter(getContext(), BaseActivity.currentProducts);
        productListView.setAdapter(adapter);
    }

    // when user login in the HomeFragment, get user's price list firstly;
    private void initData() {
        if (BaseActivity.categories.size() == 0) {
            ProductHttps.getUserPrice(BaseActivity.cookie, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Log.e("categoryLatest", resp);
                    try {
                        JSONObject res = new JSONObject(resp);
                        String msg = res.getString("msg");
                        if (msg.equals("暂无报价单")) {
                            BaseActivity.isHas = false;
                            BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.NO_GET_USER_PRICE);
                        } else {
                            BaseActivity.resp = resp;
                            BaseActivity.isHas = true;
                            BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.GET_USER_PRICE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // if not get user's price list, get market's price list;
    private void getMarketPrice() {
        ProductHttps.findCategoryList(BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BaseActivity.resp = response.body().string();
                Log.e("category", BaseActivity.resp);
                BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.GET_RESPONSE_FROM_SERVER);
            }
        });
    }

    @OnClick(R.id.home_category_vegetable)
    void vegetable() {
        selectedItem(categoryVegetable);
        unselectedItem(categoryMeat);
        unselectedItem(categoryFish);
        unselectedItem(categoryOil);
        unselectedItem(categoryGoods);
        updateAdapterData(0, BaseActivity.isHas);
    }

    @OnClick(R.id.home_category_meat)
    void meat() {
        selectedItem(categoryMeat);
        unselectedItem(categoryVegetable);
        unselectedItem(categoryFish);
        unselectedItem(categoryOil);
        unselectedItem(categoryGoods);
        updateAdapterData(1, BaseActivity.isHas);
    }

    @OnClick(R.id.home_category_fish)
    void fish() {
        unselectedItem(categoryVegetable);
        unselectedItem(categoryMeat);
        selectedItem(categoryFish);
        unselectedItem(categoryOil);
        unselectedItem(categoryGoods);
        updateAdapterData(2, BaseActivity.isHas);
    }

    @OnClick(R.id.home_category_oil)
    void oil() {
        unselectedItem(categoryVegetable);
        unselectedItem(categoryMeat);
        unselectedItem(categoryFish);
        selectedItem(categoryOil);
        unselectedItem(categoryGoods);
        updateAdapterData(3, BaseActivity.isHas);
    }

    @OnClick(R.id.home_category_goods)
    void goods() {
        unselectedItem(categoryVegetable);
        unselectedItem(categoryMeat);
        unselectedItem(categoryFish);
        unselectedItem(categoryOil);
        selectedItem(categoryGoods);
        updateAdapterData(4, BaseActivity.isHas);
    }

    private void updateAdapterData(int index, boolean isHas) {
        BaseActivity.currentProducts.clear();
        BaseActivity.currentProducts.addAll(ProductUtil.getProducts(BaseActivity.resp, index, isHas));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.home_notify_login)
    void homeNotifyLogin() {
        BaseActivity.showLoginDialog(getContext(), BaseActivity.HOMEFRAGMENT);
    }
}
