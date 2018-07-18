package com.ucai.uvegetable.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.fragment.MeFragment;
import com.ucai.uvegetable.fragment.OrderFragment;
import com.ucai.uvegetable.httputils.UserHttps;
import com.ucai.uvegetable.utils.EditorUtil;
import com.ucai.uvegetable.utils.FragmentUtil;
import com.ucai.uvegetable.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private FragmentManager fragmentManager;
    private final int parentGroupId = R.id.content_frag;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init data;
        initData();
        // init Wight();
        initWight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogined) {
            dialog = new Dialog(this, R.style.DialogTheme);
            dialog.setContentView(R.layout.login_layout);
            initDialogWight(dialog);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void initDialogWight(Dialog dialog) {
        EditText loginPhone = dialog.findViewById(R.id.login_phone);
        EditText loginPass = dialog.findViewById(R.id.login_pass);
        ImageView clearName = dialog.findViewById(R.id.login_clear_name);
        ImageView clearPass = dialog.findViewById(R.id.login_clear_pass);
        Button loginIn = dialog.findViewById(R.id.login_in);
        Button loginRegister = dialog.findViewById(R.id.login_register);
        ImageView loginNext = dialog.findViewById(R.id.login_next);
        clearName.setOnClickListener((view -> loginPhone.setText("")));
        clearPass.setOnClickListener((view -> loginPass.setText("")));
        loginIn.setOnClickListener((view -> {
            BaseActivity.showProgressDialog(this, "登录中，请稍后...");
            loginUser(this, loginPhone.getText().toString(), loginPass.getText().toString());
        }));
        loginRegister.setOnClickListener((view -> startActivity(new Intent(this, RegisterActivity.class))));
        loginNext.setOnClickListener((view -> {
            dialog.dismiss();
            editor.putBoolean("isLogined", false);
            editor.apply();
        }));
    }

    private void loginUser(Context context, String phone, String pwd) {
        if (phone.equals("") || pwd.equals("")) {
            ToastUtil.show(context, "手机号或密码不能为空");
        } else {
            UserHttps.requestLogin(phone, pwd, new Callback() {
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            BaseActivity.loginBean.setId(data.getString("id"));
                            BaseActivity.loginBean.setName(data.getString("name"));
                            BaseActivity.loginBean.setAddr(data.getString("addr"));
                            BaseActivity.loginBean.setPhone(data.getString("phone"));
                            BaseActivity.isLogined = true;
                            EditorUtil.saveEditorData(true, phone, pwd);
                            postHandler.post(() -> {
                                BaseActivity.displayProgressDialog();
                                ToastUtil.show(context, "登录成功");
                                dialog.dismiss();
                            });
                        } else {
                            postHandler.post(() -> {
                                ToastUtil.show(context, msg);
                                BaseActivity.displayProgressDialog();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void initData() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
    }

    private void initWight() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        FragmentUtil.addFragment(fragmentManager, new HomeFragment(), parentGroupId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                FragmentUtil.replaceFragment(fragmentManager, new HomeFragment(), parentGroupId);
                return true;
            case R.id.navigation_order:
                FragmentUtil.replaceFragment(fragmentManager, new OrderFragment(), parentGroupId);
                return true;
            case R.id.navigation_me:
                FragmentUtil.replaceFragment(fragmentManager, new MeFragment(), parentGroupId);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}
