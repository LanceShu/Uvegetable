package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ucai.uvegetable.R;

/**
 * Created by Lance
 * on 2018/7/25.
 */

public class HomeToOrderActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_to_order_layout);
        Log.e("ordersize", BaseActivity.orderBeans.size() + "");
    }
}
