package com.ucai.uvegetable.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.CategoryBean;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.beans.OrderedProductBean;
import com.ucai.uvegetable.beans.ProductBean;
import com.ucai.uvegetable.httputils.UserHttps;
import com.ucai.uvegetable.utils.EditorUtil;
import com.ucai.uvegetable.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

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
    private static ProgressDialog progressDialog;
    public static Dialog loginDialog;

    // HomeFragment;
    public static List<CategoryBean> categories = new ArrayList<>();
    public static List<ProductBean> currentProducts;
    public static String resp;
    public static boolean isHas;

    // HomeToOrderActivity;
    public static List<OrderedProductBean> orderedProductBeans;
    public static List<OrderedProductBean> saveOrderedProductBeans;

    public final static int ME_INFORMATION_CHANGED = 0;
    public final static int GET_RESPONSE_FROM_SERVER = 1;
    public final static int MAINACTIVITY = 2;
    public final static int HOMEFRAGMENT = 3;
    public final static int MEFRAGMENT = 4;
    public final static int UPDATE_MEFRAGMENT = 5;
    public final static int NO_GET_USER_PRICE = 6;
    public static final int GET_USER_PRICE = 7;
    public final static int UPDATE_HOMEFRAGMENT = 8;
    public final static int UPDATE_TOTAL_PRICE = 9;
    public final static int SUCCESS_SEND_ORDER = 10;
    public final static int FAILURE_SEND_ORDER = 11;
    public final static int SUCCESS_GET_DATE = 12;
    public final static int FAILURE_GET_DATE = 13;
    public final static int SUCCESS_GET_DATE_AND_STATE = 14;

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
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("温馨提示:");
        progressDialog.setMessage(hint);
        progressDialog.show();
    }

    public static void displayProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void showLoginDialog(Context context, int originType) {
        if (!isLogined) {
            loginDialog = new Dialog(context, R.style.DialogTheme);
            loginDialog.setContentView(R.layout.login_layout);
            initDialogWight(context, loginDialog, originType);
            loginDialog.setCancelable(false);
            loginDialog.show();
        }
    }

    private static void initDialogWight(Context context, Dialog dialog, int originType) {
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
//            BaseActivity.showProgressDialog(this, "登录中，请稍后...");
            loginUser(context, loginPhone.getText().toString(), loginPass.getText().toString(), originType);
        }));
        loginRegister.setOnClickListener((view -> {
            context.startActivity(new Intent(context, RegisterActivity.class));
            dialog.dismiss();
        }));
        loginNext.setOnClickListener((view -> {
            dialog.dismiss();
            editor.putBoolean("isLogined", false);
            editor.commit();
        }));
    }

    private static void loginUser(Context context, String phone, String pwd, int originType) {
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
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    if (cookies.size() > 0) {
                        String session = cookies.get(0);
                        String result = session.substring(0, session.indexOf(";"));
                        Log.e("header", result);
                        cookie = result;
                    }
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
                            if (originType == MEFRAGMENT) {
                                sendHandler.sendEmptyMessage(UPDATE_MEFRAGMENT);
                            } else if(originType == HOMEFRAGMENT) {
                                sendHandler.sendEmptyMessage(UPDATE_HOMEFRAGMENT);
                            } else if (originType == MAINACTIVITY) {
                                sendHandler.sendEmptyMessage(UPDATE_HOMEFRAGMENT);
                            }
                        } else {
                            postHandler.post(() -> {
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

    public static void showReminderDialog(Context context, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示：");
        builder.setTitle(content);
        builder.setPositiveButton("好的", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否退出优菜网？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }
}
