package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.PalmListAdapter;
import com.ucai.uvegetable.beans.LoginBean;
import com.ucai.uvegetable.camera.PhotoActivity;
import com.ucai.uvegetable.httputils.PalmHttpUtils;
import com.ucai.uvegetable.utils.Constant;
import com.ucai.uvegetable.utils.ResourceUtils;
import com.ucai.uvegetable.utils.ToastUtils;
import com.ucai.uvegetable.utils.Utils;
import com.ucai.uvegetable.volley.MultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import yan.guoqi.palm.LibPalmNative;

/**
 * Created by Lance
 * on 2019/4/16.
 */

public class MePalmListActivity extends AppCompatActivity {
    private static final String TAG = "MePalmListActivity";
    private static final int GET_NO_PLAM = 0;
    private static final int GET_PALM_FAILURE = 1;
    private static final int GET_PALM_SUCCESS = 2;

    @BindView(R.id.title_content)
    TextView palmContent;

    @BindView(R.id.title_cancel)
    ImageView palmCancel;

    @BindView(R.id.palm_list_recycler_view)
    RecyclerView palmRecyclerView;

    @BindView(R.id.palm_list_refresh)
    SwipeRefreshLayout palmRefresh;

    @BindView(R.id.palm_list_add)
    TextView palmAdd;

    @BindView(R.id.failure_msg)
    TextView failureMsg;

    private List<String> palmListUrls;
    private PalmListAdapter adapter;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palm_list_layout);
        ButterKnife.bind(this);
        // init data;
        initData(savedInstanceState);
        // init wights;
        initWights(savedInstanceState);
    }

    @SuppressLint("HandlerLeak")
    private void initData(Bundle savedInstanceState) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (palmRefresh.isRefreshing()) {
                    palmRefresh.setRefreshing(false);
                }
                switch (msg.what) {
                    case GET_NO_PLAM:
                        failureMsg.setVisibility(View.VISIBLE);
                        failureMsg.setText(ResourceUtils.getStringFromResource(MePalmListActivity.this, R.string.palm_list_no_palm));
                        break;
                    case GET_PALM_FAILURE:
                        failureMsg.setVisibility(View.VISIBLE);
                        failureMsg.setText(ResourceUtils.getStringFromResource(MePalmListActivity.this, R.string.palm_list_failure_msg));
                        break;
                    case GET_PALM_SUCCESS:
                        try {
                            JSONArray datas = (JSONArray) msg.obj;
                            List<String> urls = new ArrayList<>();
                            for (int i = 0; i < datas.length(); i++) {
                                urls.add(datas.getString(i));
                            }
                            palmListUrls.clear();
                            palmListUrls.addAll(urls);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        if (palmListUrls == null) {
            palmListUrls = new ArrayList<>();
        }
    }

    private void initWights(Bundle savedInstanceState) {
        palmContent.setText(ResourceUtils.getStringFromResource(this, R.string.me_palm_list));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        palmRecyclerView.setLayoutManager(manager);
        adapter = new PalmListAdapter(this, palmListUrls);
        palmRecyclerView.setAdapter(adapter);
        // 加载掌纹列表;
        loadAllPalmList();
        palmRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAllPalmList();
            }
        });
    }

    // 从服务器上获取用户的全部掌纹信息;
    private void loadAllPalmList() {
        PalmHttpUtils.requestToGetAllPalmList(BaseActivity.loginBean, BaseActivity.cookie,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "connect exception");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.e(TAG, resp);
                        try {
                            JSONObject res = new JSONObject(resp);
                            int code = res.getInt("code");
                            if (code == 0) {
                                JSONArray datas = res.getJSONArray("data");
                                if (datas.length() == 0) {
                                    handler.sendEmptyMessage(GET_NO_PLAM);
                                } else {
                                    Message message = Message.obtain();
                                    message.obj = datas;
                                    message.what = GET_PALM_SUCCESS;
                                    handler.sendMessage(message);
                                }
                            } else {
                                handler.sendEmptyMessage(GET_PALM_FAILURE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.title_cancel)
    void setPalmCancel() {
        finish();
    }

    @OnClick(R.id.palm_list_add)
    void setPalmAdd() {
        PhotoActivity.goToPhotoActivity(this,
                PhotoActivity.Source.ADD_PALM, PhotoActivity.REQUEST_ADD);
    }

    // 当拍完掌纹后，将保存掌纹的路径返回到该Activity中;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoActivity.REQUEST_ADD) {
                // 获取掌纹保存的路径;
                String palmImagePath = data.getStringExtra(PhotoActivity.KEY_IMAGE_PATH);
                Log.e(TAG, palmImagePath);
                // 上传掌纹到服务器中;
                uploadPalm(palmImagePath);
            }
        }
    }

    /**
     * 上传掌纹图片到服务器中;
     * @param palmImagePath
     * */
    private void uploadPalm(String palmImagePath) {
        new UploadPalmTask(this, BaseActivity.loginBean).execute(palmImagePath);
    }

    // 进行上传掌纹的异步操作;
    @SuppressLint("StaticFieldLeak")
    class UploadPalmTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mProgressDialog = null;
        private Context context;
        private LoginBean loginBean;

        UploadPalmTask(Context context, LoginBean loginBean) {
            this.context = context;
            this.loginBean = loginBean;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = BaseActivity.showProDialog(context, "正在上传...");
        }

        @Override
        protected void onPostExecute(String result) {
            BaseActivity.dismissProDialog(mProgressDialog);
            ToastUtils.show(context, "上传完毕!");
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            String roi = LibPalmNative.getRoi(path); //生成roi
            File f = new File(roi);
            if(!f.exists()){
                return null;
            }
            /**
             * 上传掌纹图片
             * @params
             * String userId
             * String type
             * File palmFile
             * */
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("userId", loginBean.getPhone());
            urlParams.put("type", "add");
            RequestFuture<String> future = RequestFuture.newFuture();
            MultipartRequest request = new MultipartRequest(Constant.UPLOAD, future,
                    future, "file", f, urlParams);
            BaseActivity.getRequestQueue().add(request);
            String result = "";
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "roi = " + roi);
            return result;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(0);
    }
}
