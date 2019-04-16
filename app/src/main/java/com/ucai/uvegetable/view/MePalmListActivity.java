package com.ucai.uvegetable.view;

import android.annotation.SuppressLint;
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

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.adapter.PalmListAdapter;
import com.ucai.uvegetable.httputils.PalmHttpUtils;
import com.ucai.uvegetable.utils.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(0);
    }
}
