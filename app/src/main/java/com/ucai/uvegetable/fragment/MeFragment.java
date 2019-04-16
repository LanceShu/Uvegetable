package com.ucai.uvegetable.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.httputils.UserHttpUtil;
import com.ucai.uvegetable.utils.EditorUtils;
import com.ucai.uvegetable.utils.ToastUtils;
import com.ucai.uvegetable.view.BaseActivity;
import com.ucai.uvegetable.view.MeDeliverActivity;
import com.ucai.uvegetable.view.MeInforActivity;
import com.ucai.uvegetable.view.MeOrderActivity;
import com.ucai.uvegetable.view.MePalmListActivity;

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

    // 用户的昵称;
    @BindView(R.id.me_name)
    TextView mName;

    // 用户的手机号;
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

    @BindView(R.id.me_palm_list)
    LinearLayout mPalmList;

    @BindView(R.id.me_palm_identify)
    LinearLayout mPalmIdentify;

    @BindView(R.id.me_btn_exit)
    Button mBtnExit;

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
        mBtnExit.setVisibility(BaseActivity.isLogined ? View.VISIBLE : View.GONE);
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
                    case BaseActivity.UPDATE_MEFRAGMENT:
                        ToastUtils.show(getContext(), "登录成功");
                        BaseActivity.loginDialog.dismiss();
                        visibleNameAndPhone(BaseActivity.loginBean.getName()
                                , BaseActivity.loginBean.getPhone());
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
        BaseActivity.showLoginDialog(getActivity(), BaseActivity.MEFRAGMENT);
    }

    // 查看个人信息;
    @OnClick(R.id.me_information)
    void meInformation() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "请先登录，谢谢~");
        } else {
            Intent toMeInforActivity = new Intent(getContext(), MeInforActivity.class);
            startActivity(toMeInforActivity);
        }
    }

    // 采购单信息;
    @OnClick(R.id.me_order)
    void meOrder() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "暂无采购单信息，请先登录，谢谢~");
        } else {
            getContext().startActivity(new Intent(getContext(), MeOrderActivity.class));
        }
    }

    // 送货单信息;
    @OnClick(R.id.me_driver)
    void meDriver() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "暂无送货单信息，请先登录，谢谢~");
        } else {
            getContext().startActivity(new Intent(getContext(), MeDeliverActivity.class));
        }

    }

    // 设置功能,暂时没有;
    @OnClick(R.id.me_setting)
    void meSetting() {

    }

    // 掌纹管理;
    @OnClick(R.id.me_palm_list)
    void mePalmList() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "暂无掌纹信息，请先登录，谢谢~");
        } else {
            startActivity(new Intent(getContext(), MePalmListActivity.class));
        }
    }

    // 掌纹认证;
    @OnClick(R.id.me_palm_identify)
    void mePalmIdentify() {
        if (!BaseActivity.isLogined) {
            BaseActivity.showHintDialog(getContext(), "无法识别掌纹，请先登录，谢谢~");
        } else {
//            Intent toMeInforActivity = new Intent(getContext(), MeInforActivity.class);
//            startActivity(toMeInforActivity);
        }
    }

    @OnClick(R.id.me_btn_exit)
    void meExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("温馨提示：");
        builder.setMessage("是否确认注销当前用户？");
        builder.setPositiveButton("注销", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loginoutUser();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void loginoutUser() {
        EditorUtils.saveEditorData(BaseActivity.editor, false, "", "");
        BaseActivity.isLogined = false;
        BaseActivity.resp = null;
        BaseActivity.isHas = false;
        BaseActivity.cookie = "";
        BaseActivity.currentProducts = null;
        BaseActivity.orderedProductBeans = null;
        BaseActivity.showProgressDialog(getContext(), "正在注销...");
        UserHttpUtil.logoutUser(BaseActivity.cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.e("syuban", resp);
                try {
                    JSONObject res = new JSONObject(resp);
                    String msg = res.getString("msg");
                    if (msg.equals("注销成功")) {
                        BaseActivity.postHandler.post(() -> {
                            BaseActivity.displayProgressDialog();
                            BaseActivity.showReminderDialog(getActivity(), msg);
                            invisibleNameAndPhone();
                            mBtnExit.setVisibility(View.GONE);
                        });
                    } else {
                        BaseActivity.postHandler.post(() -> {
                            BaseActivity.displayProgressDialog();
                            BaseActivity.showReminderDialog(getActivity(), msg);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
