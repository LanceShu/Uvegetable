package com.ucai.uvegetable.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.utils.ApkUtil;
import com.ucai.uvegetable.utils.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/5/5.
 */

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "AboutFragment";
    private static final String VERSION_CODE_URL = "http://123.207.145.251:8080/UvegetableServer/checkout_code";
    private static final String GET_APK_UPGRADE_URL = "http://123.207.145.251:8080/UvegetableServer/get_upgrade_url";

    @BindView(R.id.title_content)
    TextView titleContent;

    @BindView(R.id.title_cancel)
    ImageView titleCancel;

    @BindView(R.id.about_app_version)
    TextView apkVersion;

    @BindView(R.id.about_checkout_upgrade)
    ImageView checkUpgrade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_about_activity);
        ButterKnife.bind(this);
        // 初始化控件;
        initWights(savedInstanceState);
    }

    private void initWights(Bundle savedInstanceState) {
        titleContent.setText(ResourceUtils.getStringFromResource(this, R.string.me_about));
        String version = ApkUtil.getAPKVersionName(this, getPackageName());
        if (version != null) {
            apkVersion.setText("版本号：" + version);
        }
    }

    @OnClick(R.id.title_cancel)
    void titleCancel() {
        finish();
    }

    @OnClick(R.id.about_checkout_upgrade)
    void aboutCheckUpgrade() {
        int code = ApkUtil.getAPKVersionCode(this, getPackageName());
        if (code != -1) {
            new CheckoutVersionCodeTask(this, code).execute(VERSION_CODE_URL);
        }
    }

    // 检查更新任务；
    static class CheckoutVersionCodeTask extends AsyncTask<String, Void, Integer> {
        private WeakReference<Context> c;
        private int localVersionCode;
        private ProgressDialog progressDialog;

        CheckoutVersionCodeTask(Context context, int localVersionCode) {
            c = new WeakReference<>(context);
            this.localVersionCode = localVersionCode;
        }

        @Override
        protected void onPreExecute() {
            if (c != null) {
                Context context = c.get();
                progressDialog = createProgressDialog(context);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String url = strings[0];
            int versionCode = -1;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response != null) {
                    String resp = response.body().string();
                    if (!resp.contains("file is not exists!")) {
                        JSONObject jsonObject = new JSONArray(resp).getJSONObject(0);
                        JSONObject apkInfo = jsonObject.getJSONObject("apkInfo");
                        versionCode = apkInfo.getInt("versionCode");
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return versionCode;
        }

        @Override
        protected void onPostExecute(Integer currentVersionCode) {
            if (c != null) {
                Context context = c.get();
                Log.e(TAG, "currentCode:" + localVersionCode
                        + ", newVersionCode:" + currentVersionCode);
                if (currentVersionCode > localVersionCode) {
                    // 展示下载询问框;
                    showAlertDialog(context);
                } else {
                    Toast.makeText(context,
                            ResourceUtils.getStringFromResource(context, R.string.app_name) + "已是最新",
                            Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }

        private ProgressDialog createProgressDialog(Context context) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(ResourceUtils.getStringFromResource(context, R.string.dialog_title));
            progressDialog.setMessage("正在检查更新...");
            progressDialog.create();
            return progressDialog;
        }

        private void showAlertDialog(final Context context) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(ResourceUtils.getStringFromResource(context, R.string.dialog_title));
            alertDialog.setMessage("有新版本，是否立即更新?");
            alertDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadApkFromServerTask(context).execute(GET_APK_UPGRADE_URL);
                    Toast.makeText(context, "后台开始下载", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("以后再说", null);
            alertDialog.show();
        }
    }

    // apk下载任务；
    static class DownloadApkFromServerTask extends AsyncTask<String, Void, Void> {
        private WeakReference<Context> c;

        DownloadApkFromServerTask(Context context) {
            c = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (c != null) {
                Context context = c.get();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null) {
                        String resp = response.body().string();
                        if (!resp.contains("file is not exists!")) {
                            ApkUtil.downloadAPK(context, resp);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
