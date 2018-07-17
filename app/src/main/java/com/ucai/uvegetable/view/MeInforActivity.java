package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.httputils.UserHttps;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2018/7/17.
 */

public class MeInforActivity extends AppCompatActivity{

    @BindView(R.id.me_infor_close)
    ImageView meInforCancel;

    @BindView(R.id.me_infor_id)
    EditText meInforId;

    @BindView(R.id.me_infor_name)
    EditText meInforName;

    @BindView(R.id.me_infor_addr)
    EditText meInforAddr;

    @BindView(R.id.me_infor_phone)
    EditText meInforPhone;

    @BindView(R.id.me_infor_button)
    Button meInforBtn;

    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_infor_activity);
        ButterKnife.bind(this);
        initWight();
    }

    private void initWight() {
        diseditable();
        meInforId.setText(BaseActivity.loginBean.getId());
        meInforName.setText(BaseActivity.loginBean.getName());
        meInforAddr.setText(BaseActivity.loginBean.getAddr());
        meInforPhone.setText(BaseActivity.loginBean.getPhone());
    }

    @OnClick(R.id.me_infor_close)
    void inforClose() {
        finish();
    }

    @OnClick(R.id.me_infor_button)
    void inforBn() {
        if (isEdit) {
            String id = meInforId.getText().toString();
            String name = meInforName.getText().toString();
            String addr = meInforAddr.getText().toString();
            String phone = meInforPhone.getText().toString();
            httpRequestUpdate(id, name, addr, phone);
        } else {
            editable();
            isEdit = true;
        }
    }

    private void httpRequestUpdate(String id, String name, String addr, String phone) {
        UserHttps.requestUpdateInfor(id, name, addr, phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.e("success", resp);
            }
        });
    }

    private void editable() {
//        meInforId.setEnabled(true);
        meInforName.setEnabled(true);
        meInforAddr.setEnabled(true);
        meInforPhone.setEnabled(true);
        meInforBtn.setText("保存提交");
    }

    private void diseditable() {
        meInforId.setEnabled(false);
        meInforName.setEnabled(false);
        meInforAddr.setEnabled(false);
        meInforPhone.setEnabled(false);
        meInforBtn.setText("编辑信息");
    }
}
