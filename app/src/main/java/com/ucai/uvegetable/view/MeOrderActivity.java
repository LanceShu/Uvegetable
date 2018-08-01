package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ucai.uvegetable.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lance
 * on 2018/8/1.
 */

public class MeOrderActivity extends AppCompatActivity {

    @BindView(R.id.mo_back)
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_order_layout);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mo_back)
    void back() {
        finish();
    }
}
