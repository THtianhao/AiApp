package com.example.toto.aiapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.toto.download.DownloadUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.HashMap;
import java.util.Map;

import static com.example.toto.download.DownloadUtils.DOWNLOAD_FOLDER_NAME;

/**
 * Created by toto on 17/4/8.
 */

public class ShowLoadingActivity extends Activity {
    private static final int MSG_SHOP = 0x10;
    private static final int MSG_DOWNLOAD = 0x20;
    private static final int MSG_CHANGETEXT = 0x40;
    private static final int MSG_BUILD = 0x80;

    private SpinKitView mLoadingView;
    private TextView textView;
    private DownloadReceiver2 mRecevier;
    private DownloadUtils mDownloadUtils;
    private SharedPreferences prefs;
    private static final String DL_ID = "downloadId";
    private ShopHandler mHandler;
    private boolean mStartDownload = false;
    private int mDownloadState = -1;
    private String mUrl = "http://13.124.90.99:8082/";
    private boolean isFinished = false;
    private String mShopType;

    class ShopHandler extends Handler {
        ShopHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOP:
                    mDownloadUtils.queryDownloadStatus();
                    this.sendEmptyMessageDelayed(MSG_SHOP, 10);
                    break;
                case MSG_DOWNLOAD:
                    mDownloadUtils.downLoad("http://13.124.90.99:8081/app-release.apk");
                    this.sendEmptyMessage(MSG_SHOP);
                    break;
                case MSG_CHANGETEXT:
                    textView.setText("正在下载");
                    break;
                case MSG_BUILD:
                    VolleyUtils.getInstance(ShowLoadingActivity.this).sendRequest(stringRequest2);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mShopType = bundle.getString("shoptype");
        if (!isFinished) {
            setContentView(R.layout.loading_view);
            mRecevier = new DownloadReceiver2();
            mHandler = new ShopHandler();
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            filter.addAction("android.intent.action.DOWNLOAD_FAIL");
            filter.addAction("android.intent.action.DOWNLOAD_SUCCESS");
            registerReceiver(mRecevier, filter);
            initView();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mDownloadUtils = new DownloadUtils(this);
            mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD, 600);
            mHandler.sendEmptyMessageDelayed(MSG_BUILD, 500);
            mStartDownload = true;
            Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
            mHandler.sendEmptyMessageDelayed(MSG_CHANGETEXT, 10000);
        }
    }

    private void initView() {
        mLoadingView = (SpinKitView) findViewById(R.id.loading_view);
        mLoadingView.setBackgroundColor(Color.BLACK);
        mLoadingView.setIndeterminateDrawable(new CubeGrid());
        textView = (TextView) findViewById(R.id.loading_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);
    }

    public class DownloadReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                mDownloadState = mDownloadUtils.queryDownloadStatus();
                Log.d("tianhao", "下载 ACTION_DOWNLOAD_COMPLETE");
                if (mDownloadState == 3) {
                    textView.setText("下载已完成");
                    mHandler.removeMessages(MSG_SHOP);
                    VolleyUtils.getInstance(ShowLoadingActivity.this).sendRequest(stringRequest);
                    isFinished=true;
                }
            } else if (intent.getAction().equals("android.intent.action.DOWNLOAD_FAIL")) {
                Log.d("tianhao", "下载失败");
                mDownloadState = 4;
                mHandler.removeMessages(MSG_SHOP);
                mHandler.sendEmptyMessage(MSG_DOWNLOAD);
            } else if (intent.getAction().equals("android.intent.action.DOWNLOAD_SUCCESS")) {
//                Toast.makeText(context, "下载成功！", Toast.LENGTH_SHORT).show();
//                Log.d("tianhao", "下载成功");
                mHandler.removeMessages(MSG_SHOP);
                mDownloadState = 3;

            }
        }
    }

    StringRequest stringRequest = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("tianhao", "success");
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("tianhao", "fail");
        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            HashMap<String, String> mMap = new HashMap<>();
            mMap.put("param", "delete");
            return mMap;
        }
    };

    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("tianhao", "构建请求成功");
            Toast.makeText(ShowLoadingActivity.this, "构建请求成功", Toast.LENGTH_SHORT).show();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("tianhao", "构建请求失败");
            Toast.makeText(ShowLoadingActivity.this, "构建请求失败", Toast.LENGTH_SHORT).show();
        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            HashMap<String, String> mMap = new HashMap<>();
            mMap.put("param", mShopType);
            return mMap;
        }
    };
}
