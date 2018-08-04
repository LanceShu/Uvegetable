package com.ucai.uvegetable.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.RegisterBean;
import com.ucai.uvegetable.httputils.UserHttpUtil;
import com.ucai.uvegetable.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2018/5/28.
 */

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.title_cancel)
    ImageView cancel;

    @BindView(R.id.title_content)
    TextView title;

    @BindView(R.id.register_phone)
    EditText register_phone;

    @BindView(R.id.register_pass)
    EditText register_pass;

    @BindView(R.id.register_again_pass)
    EditText register_again_pass;

    @BindView(R.id.clear_again_pass)
    ImageView clear_again_pass;

    @BindView(R.id.clear_phone)
    ImageView clear_phone;

    @BindView(R.id.clear_pass)
    ImageView clear_pass;

    @BindView(R.id.register_user)
    Button register_user;

    @BindView(R.id.register_name)
    EditText register_name;

    @BindView(R.id.clear_name)
    ImageView clear_name;

    @BindView(R.id.register_addr)
    EditText register_addr;

    @BindView(R.id.clear_addr)
    ImageView clear_addr;

    private Handler postHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        ButterKnife.bind(this);
        initWight();
        postHandler = new Handler();
    }

    private void initWight() {
        title.setText(R.string.register);
    }

    @OnClick(R.id.clear_phone)
    void clearPhone() {
        register_phone.setText("");
    }

    @OnClick(R.id.clear_pass)
    void clearPass() {
        register_pass.setText("");
    }

    @OnClick(R.id.clear_again_pass)
    void clearAgainPass() {
        register_again_pass.setText("");
    }

    @OnClick(R.id.register_user)
    void registerUser() {
        String phone = register_phone.getText().toString();
        String pwd = register_pass.getText().toString();
        String repwd = register_again_pass.getText().toString();
        String name = register_name.getText().toString();
        String addr = register_addr.getText().toString();
        if (phone.equals("") || pwd.equals("") || repwd.equals("")
                || name.equals("") || addr.equals("")) {
            BaseActivity.showReminderDialog(this, "注册信息不能为空！");
        } else if (phone.length() != 11) {
            BaseActivity.showReminderDialog(this, "手机号格式不正确！");
        } else if (!pwd.equals(repwd)) {
            BaseActivity.showReminderDialog(this, "两次密码不匹配！");
        } else {
            RegisterBean registerBean = new RegisterBean();
            registerBean.setPhone(phone);
            registerBean.setPwd(pwd);
            registerBean.setName(name);
            registerBean.setAddr(addr);
            UserHttpUtil.requestRegister(registerBean, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Log.e("success", resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        String msg = jsonObject.getString("msg");
                        if (msg.equals("成功")) {
                            postHandler.post(() -> {
                                Log.e("success", msg);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("温馨提示：");
                                builder.setMessage("注册成功！");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                builder.show();
                            });
                        } else {
                            postHandler.post(() -> {
                                BaseActivity.showReminderDialog(RegisterActivity.this, msg);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @OnClick(R.id.title_cancel)
    void titleCancel() {
        this.finish();
    }

    @OnClick(R.id.clear_name)
    void clearName() {
        register_name.setText("");
    }

    @OnClick(R.id.clear_addr)
    void clearAddr() {
        register_addr.setText("");
    }
}
