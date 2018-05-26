package com.ucai.uvegetable.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.fragment.MeFragment;
import com.ucai.uvegetable.fragment.OrderFragment;
import com.ucai.uvegetable.utils.FragmentUtil;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private FragmentManager fragmentManager;
    private final int parentGroupId = R.id.content_frag;

    private Dialog dialog;
    private EditText loginName;
    private EditText loginPass;

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
        loginName = dialog.findViewById(R.id.login_name);
        loginPass = dialog.findViewById(R.id.login_pass);
        ImageView clearName = dialog.findViewById(R.id.clear_name);
        ImageView clearPass = dialog.findViewById(R.id.clear_pass);
        Button loginIn = dialog.findViewById(R.id.login_in);
        Button loginRegister = dialog.findViewById(R.id.login_register);
        Button loginNext = dialog.findViewById(R.id.login_next);
        clearName.setOnClickListener(this);
        clearPass.setOnClickListener(this);
        loginIn.setOnClickListener(this);
        loginRegister.setOnClickListener(this);
        loginNext.setOnClickListener(this);
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
        switch (view.getId()) {
            case R.id.clear_name:
                loginName.setText("");
                break;
            case R.id.clear_pass:
                loginPass.setText("");
                break;
            case R.id.login_in:
//                dialog.dismiss();
//                editor.putBoolean("isLogined", true);
//                editor.apply();
                break;
            case R.id.login_register:
                break;
            case R.id.login_next:
                dialog.dismiss();
                editor.putBoolean("isLogined", false);
                editor.apply();
                break;
        }
    }
}
