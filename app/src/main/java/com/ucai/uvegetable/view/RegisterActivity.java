package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        ButterKnife.bind(this);
        initWight();
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
        String pass = register_pass.getText().toString();
        String pass_again = register_again_pass.getText().toString();
        String name = register_name.getText().toString();
        String addr = register_addr.getText().toString();
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
