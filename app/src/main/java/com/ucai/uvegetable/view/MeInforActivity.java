package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.httputils.UserHttpUtil;

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

    @BindView(R.id.title_cancel)
    ImageView meInforCancel;

    @BindView(R.id.title_content)
    TextView content;

    @BindView(R.id.me_infor_id)
    TextView meInforId;

    @BindView(R.id.me_infor_name)
    EditText meInforName;

    @BindView(R.id.me_infor_addr)
    EditText meInforAddr;

    @BindView(R.id.me_infor_phone)
    TextView meInforPhone;

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
        content.setText("个人信息");
        diseditable();
        meInforId.setText(BaseActivity.loginBean.getId());
        meInforName.setText(BaseActivity.loginBean.getName());
        meInforAddr.setText(BaseActivity.loginBean.getAddr());
        meInforPhone.setText(BaseActivity.loginBean.getPhone());
    }

    @OnClick(R.id.title_cancel)
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
            Log.e("syuban", addr.length() + "  " +phone.length());
            if (name.equals("")) {
                BaseActivity.showReminderDialog(this, "单位名称不能为空！");
            } else if (addr.equals("")) {
                BaseActivity.showReminderDialog(this, "单位地址不能为空！");
            } else {
                httpRequestUpdate(id, name, addr, phone);
                diseditable();
                isEdit = false;
            }
        } else {
            editable();
            isEdit = true;
        }
    }

    private void httpRequestUpdate(String id, String name, String addr, String phone) {
        UserHttpUtil.requestUpdateInfor(id, name, addr, phone, BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
//                Log.e("success", resp);
                BaseActivity.postHandler.post(() -> {
                   BaseActivity.loginBean.setName(meInforName.getText().toString());
                   BaseActivity.loginBean.setAddr(meInforAddr.getText().toString());
                   BaseActivity.loginBean.setPhone(meInforPhone.getText().toString());
                   diseditable();
                   BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.ME_INFORMATION_CHANGED);
                });
            }
        });
    }

    private void editable() {
        meInforName.setEnabled(true);
        meInforAddr.setEnabled(true);
        meInforBtn.setText("保存提交");
    }

    private void diseditable() {
        meInforName.setEnabled(false);
        meInforAddr.setEnabled(false);
        meInforBtn.setText("编辑信息");
    }
}
