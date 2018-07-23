package com.ucai.uvegetable.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.httputils.UserHttps;
import com.ucai.uvegetable.utils.EditorUtil;
import com.ucai.uvegetable.utils.ResourceUtil;
import com.ucai.uvegetable.utils.ToastUtil;
import com.ucai.uvegetable.view.BaseActivity;
import com.ucai.uvegetable.view.MeInforActivity;
import com.ucai.uvegetable.view.RegisterActivity;

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
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class MeFragment extends Fragment {

    @BindView(R.id.me_name)
    TextView mName;

    @BindView(R.id.me_phone)
    TextView mPhone;

    @BindView(R.id.me_loginOrRegister)
    TextView mLoginOrRegister;

    @BindView(R.id.me_information)
    LinearLayout mInformation;

    @BindView(R.id.me_order)
    LinearLayout mOrder;

    @BindView(R.id.me_driver)
    LinearLayout mDriver;

    @BindView(R.id.me_setting)
    LinearLayout mSetting;

    @BindView(R.id.me_btn_exit)
    Button mBtnExit;

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!BaseActivity.isLogined) {
            invisibleNameAndPhone();
        } else {
            visibleNameAndPhone(BaseActivity.loginBean.getName()
                    , BaseActivity.loginBean.getPhone());
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.sendHandler = new Handler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BaseActivity.ME_INFORMATION_CHANGED:
                        mName.setText("单位名称：" + BaseActivity.loginBean.getName());
                        mPhone.setText("手机号：" + BaseActivity.loginBean.getPhone());
                        break;
                }
            }
        };
    }

    // display name and phone;
    @SuppressLint("SetTextI18n")
    private void visibleNameAndPhone(String name, String phone) {
        mName.setVisibility(View.VISIBLE);
        mPhone.setVisibility(View.VISIBLE);
        mLoginOrRegister.setVisibility(View.GONE);
        Log.e("mefegament", name + "----" + phone);
        mName.setText("单位名称：" + name);
        mPhone.setText("手机号：" + phone);
    }

    // no display name and phone;
    private void invisibleNameAndPhone() {
        mName.setVisibility(View.GONE);
        mPhone.setVisibility(View.GONE);
        mLoginOrRegister.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.me_loginOrRegister)
    void mLoginOrRegister() {
        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.login_layout);
        initDialogWight(dialog);
        dialog.setCancelable(false);
        dialog.show();
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
//            BaseActivity.showProgressDialog(getContext(), "登录中，请稍后...");
            loginUser(getContext(), loginPhone.getText().toString(), loginPass.getText().toString());
        }));
        loginRegister.setOnClickListener((view -> startActivity(new Intent(getContext(), RegisterActivity.class))));
        loginNext.setOnClickListener((view -> {
            dialog.dismiss();
            BaseActivity.editor.putBoolean("isLogined", false);
            BaseActivity.editor.apply();
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
                            BaseActivity.postHandler.post(() -> {
//                                BaseActivity.displayProgressDialog();
                                ToastUtil.show(context, "登录成功");
                                dialog.dismiss();
                                visibleNameAndPhone(BaseActivity.loginBean.getName()
                                        , BaseActivity.loginBean.getPhone());
                            });
                        } else {
                            BaseActivity.postHandler.post(() -> {
                                ToastUtil.show(context, msg);
//                                BaseActivity.displayProgressDialog();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @OnClick(R.id.me_information)
    void meInformation() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "请先登录，谢谢~");
        } else {
            Intent toMeInforActivity = new Intent(getContext(), MeInforActivity.class);
            startActivity(toMeInforActivity);
        }
    }

    @OnClick(R.id.me_order)
    void meOrder() {

    }

    @OnClick(R.id.me_driver)
    void meDriver() {

    }

    @OnClick(R.id.me_setting)
    void meSetting() {

    }

    @OnClick(R.id.me_btn_exit)
    void meExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("温馨提示：");
        builder.setMessage("是否确认注销当前用户？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseActivity.isLogined = false;
                BaseActivity.editor.putString("isLogined", "false");
                BaseActivity.editor.putString("phone", "");
                BaseActivity.editor.putString("pwd", "");
                BaseActivity.postHandler.post(() -> {
                    invisibleNameAndPhone();
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
