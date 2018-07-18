package com.ucai.uvegetable.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class FragmentUtil {
    public static void replaceFragment(FragmentManager fragmentManager, Fragment targetFragment, int parentGroupId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(parentGroupId, targetFragment);
        transaction.commit();
    }

    public static void hideFragment(FragmentManager fragmentManager, Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(targetFragment);
        transaction.commit();
    }

    public static void addFragment(FragmentManager fragmentManager, Fragment targetFragment, int parentGroupId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(parentGroupId, targetFragment);
        transaction.commit();
    }
}
