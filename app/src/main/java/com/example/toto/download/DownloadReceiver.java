package com.example.toto.download;

/**
 * Created by toto on 17/4/6.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 运用downloadManager下载完成之后 通知系统我下载完成
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
        }
    }

}