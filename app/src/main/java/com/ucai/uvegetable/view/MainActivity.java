package com.ucai.uvegetable.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.fragment.HomeFragment;
import com.ucai.uvegetable.fragment.MeFragment;
import com.ucai.uvegetable.fragment.OrderFragment;
import com.ucai.uvegetable.utils.FragmentUtils;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private FragmentManager fragmentManager;
    private final int parentGroupId = R.id.content_frag;

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
    }

    private void initData() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
    }

    private void initWight() {
        if (!isLogined) {
            showLoginDialog(this, MAINACTIVITY);
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        FragmentUtils.addFragment(fragmentManager, new HomeFragment(), parentGroupId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                FragmentUtils.replaceFragment(fragmentManager, new HomeFragment(), parentGroupId);
                return true;
            case R.id.navigation_order:
                FragmentUtils.replaceFragment(fragmentManager, new OrderFragment(), parentGroupId);
                return true;
            case R.id.navigation_me:
                FragmentUtils.replaceFragment(fragmentManager, new MeFragment(), parentGroupId);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}
