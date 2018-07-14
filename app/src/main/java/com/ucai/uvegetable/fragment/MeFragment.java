package com.ucai.uvegetable.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.view.BaseActivity;
import com.ucai.uvegetable.view.RegisterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
            visibleNameAndPhone();
        }
    }

    // display name and phone;
    private void visibleNameAndPhone() {
        mName.setVisibility(View.VISIBLE);
        mPhone.setVisibility(View.VISIBLE);
        mLoginOrRegister.setVisibility(View.GONE);
    }

    // no display name and phone;
    private void invisibleNameAndPhone() {
        mName.setVisibility(View.GONE);
        mPhone.setVisibility(View.GONE);
        mLoginOrRegister.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.me_loginOrRegister)
    void mLoginOrRegister() {
        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.login_layout);
        initDialogWight(dialog);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void initDialogWight(Dialog dialog) {
        EditText loginName = dialog.findViewById(R.id.login_name);
        EditText loginPass = dialog.findViewById(R.id.login_pass);
        ImageView clearName = dialog.findViewById(R.id.login_clear_name);
        ImageView clearPass = dialog.findViewById(R.id.login_clear_pass);
        Button loginIn = dialog.findViewById(R.id.login_in);
        Button loginRegister = dialog.findViewById(R.id.login_register);
        ImageView loginNext = dialog.findViewById(R.id.login_next);
        clearName.setOnClickListener((view -> loginName.setText("")));
        clearPass.setOnClickListener((view -> loginPass.setText("")));
        loginIn.setOnClickListener((view -> {
//            dialog.dismiss();
//            editor.putBoolean("isLogined", true);
//            editor.apply();
        }));
        loginRegister.setOnClickListener((view -> {
            startActivity(new Intent(getContext(), RegisterActivity.class));
        }));
        loginNext.setOnClickListener((view -> {
            dialog.dismiss();
            BaseActivity.editor.putBoolean("isLogined", false);
            BaseActivity.editor.apply();
        }));
    }
}
