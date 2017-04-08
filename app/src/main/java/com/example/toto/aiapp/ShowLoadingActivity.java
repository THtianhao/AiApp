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

import com.example.toto.download.DownloadUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.CubeGrid;

import static com.example.toto.download.DownloadUtils.DOWNLOAD_FOLDER_NAME;

/**
 * Created by toto on 17/4/8.
 */

public class ShowLoadingActivity extends Activity {
    private static final int MSG_SHOP = 0x10;
    private static final int MSG_DOWNLOAD = 0x20;

    private SpinKitView mLoadingView;
    private TextView textView;
    private DownloadReceiver2 mRecevier;
    private DownloadUtils mDownloadUtils;
    private SharedPreferences prefs;
    private static final String DL_ID = "downloadId";
    private ShopHandler mHandler;
    private boolean mStartDownload = false;
    private int mDownloadState = -1;

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
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view);
        mRecevier = new DownloadReceiver2();
        mHandler = new ShopHandler();
        mHandler.sendEmptyMessage(MSG_SHOP);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction("android.intent.action.DOWNLOAD_FAIL");
        filter.addAction("android.intent.action.DOWNLOAD_SUCCESS");
        registerReceiver(mRecevier, filter);
        initView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDownloadUtils = new DownloadUtils(this);
        mHandler.sendEmptyMessage(MSG_DOWNLOAD);
        mStartDownload = true;
        Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
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
                if (mDownloadState == 3) {
                    textView.setText("下载已完成");
                }
            } else if (intent.getAction().equals("android.intent.action.DOWNLOAD_FAIL")) {
                Toast.makeText(context, "下载失败！", Toast.LENGTH_SHORT).show();
                Log.d("tianhao", "下载失败");
                mDownloadState = 4;
                mHandler.removeMessages(MSG_SHOP);
                mHandler.sendEmptyMessage(MSG_DOWNLOAD);
            } else if (intent.getAction().equals("android.intent.action.DOWNLOAD_SUCCESS")) {
                Toast.makeText(context, "下载成功！", Toast.LENGTH_SHORT).show();
                Log.d("tianhao", "下载成功");
                mDownloadState = 3;
                mHandler.removeMessages(MSG_SHOP);
            }
        }

    }
}
