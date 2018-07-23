package com.ucai.uvegetable.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ucai.uvegetable.beans.CategoryBean;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.beans.ProductBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SyubanLiu
 * on 2018/5/26.
 */

public class BaseActivity extends AppCompatActivity {
    public static boolean isLogined;
    public static String cookie = "";
    public SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static Handler postHandler;
    public static Handler sendHandler;
    public static LoginBean loginBean;
    private static ProgressDialog dialog;

    // HomeFragment;
    public static List<CategoryBean> categories = new ArrayList<>();
    public static List<ProductBean> currentProducts;

    public final static int ME_INFORMATION_CHANGED = 0;
    public final static int GET_RESPONSE_FROM_SERVER = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (postHandler == null) {
            postHandler = new Handler();
        }
        if (sendHandler == null) {
            sendHandler = new Handler();
        }
        if (loginBean == null) {
            loginBean = new LoginBean();
        }
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = sharedPreferences.edit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void showHintDialog(Context context, String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("温馨提示：");
        builder.setMessage(hint);
        builder.setPositiveButton("好的", null);
        builder.show();
    }

    public static void showProgressDialog(Context context, String hint) {
        dialog = new ProgressDialog(context);
        dialog.setTitle("温馨提示:");
        dialog.setMessage(hint);
        dialog.show();
    }

    public static void displayProgressDialog() {
        dialog.dismiss();
    }
}
