package com.ucai.uvegetable.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.CategoryBean;
import com.ucai.uvegetable.beans.ProductBean;
import com.ucai.uvegetable.httputils.ProductHttps;
import com.ucai.uvegetable.utils.ProductUtil;
import com.ucai.uvegetable.view.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        if (BaseActivity.categories.size() == 0) {
            ProductHttps.findCategoryList(BaseActivity.cookie, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    BaseActivity.categories = ProductUtil.getCategoryList(resp);
                    BaseActivity.vegetableProducts = ProductUtil.getVegetableProducts(resp);
                    BaseActivity.meatProducts = ProductUtil.getMeatProducts(resp);
                    BaseActivity.fishProducts = ProductUtil.getFishProducts(resp);
                    BaseActivity.oilProducts = ProductUtil.getOilProducts(resp);
                    BaseActivity.goodProducts = ProductUtil.getGoodProducts(resp);
                }
            });
        }
    }
}
