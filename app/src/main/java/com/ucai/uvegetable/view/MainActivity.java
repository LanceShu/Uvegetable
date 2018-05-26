package com.ucai.uvegetable.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.fragment.MeFragment;
import com.ucai.uvegetable.fragment.OrderFragment;
import com.ucai.uvegetable.utils.FragmentUtil;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FragmentManager fragmentManager;
    private final int parentGroupId = R.id.content_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isFirstLogin) {
            Dialog dialog = new Dialog(this, R.style.DialogTheme);
            dialog.setContentView(R.layout.login_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        // init data;
        initData();
        // init Wight();
        initWight();
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
}
