package com.example.toto.download;

/**
 * Created by toto on 17/4/6.
 */

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * 下载更新的util 主要是负责 版本的相关更新工作
 * 实际 下载更新的具体步骤：
 * 1.将自己应用的版本号传递给服务器  服务器与自己最新的app版本号对比(文件命名添加版本号的后缀)
 * 如果服务器版本号>本地所传递过去的版本号  服务器传递版本号和URL地址过来 本地下载更新
 * 将下载返回的ID存放在sharedPreference中
 * 2.如果用户的不正当操作使得下载终止：A:检查数据库中下载的文件状态是否为200(成功)
 * 如果成功就直接跳转到安装界面
 * 如果不成功  就将remove(long... ids)当前下载任务remove掉  文件也删除   sp中也数据 清零开启新的下载任务
 */
public class DownloadUtils {
    private Context context;
    private String url;
    private String notificationTitle;
    private String notificationDescription;
    private DownloadManager downLoadManager;
    private SharedPreferences prefs;
    private static final String DL_ID = "downloadId";
    public static final String DOWNLOAD_FOLDER_NAME = "app/apk/download";
    public static final String DOWNLOAD_FILE_NAME = "test.apk";


    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public DownloadUtils(Context context) {
        this.context = context;
        downLoadManager = (DownloadManager) this.context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //得到当前应用的版本号
    public int getVersionName() throws Exception {
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                0);
        return packInfo.versionCode;
    }

    /**
     * 服务端的版本号与客户端的版本号对比
     *
     * @param localVersion  本地版本号
     * @param serverVersion 服务器版本号
     * @return true 可以下载更新  false 不能下载更新
     */
    public boolean canUpdate(int localVersion, int serverVersion) {
        if (localVersion <= 0 || serverVersion <= 0)
            return false;
        if (localVersion >= serverVersion) {
            return false;
        }
        return true;
    }

    public long downLoad(String url) {
        long id = -1;
        prefs.edit().clear().apply();
        if (!prefs.contains(DL_ID)) {
            Request request = new Request(Uri.parse(url));
            //设置状态栏中显示Notification
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (!TextUtils.isEmpty(getNotificationTitle())) {
                request.setTitle(getNotificationTitle());
            }
            if (!TextUtils.isEmpty(getNotificationDescription())) {
                request.setDescription(getNotificationDescription());
            }
            //设置可用的网络类型
            request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
            //不显示下载界面
            request.setVisibleInDownloadsUi(false);

            //创建文件的下载路径
            File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
            //指定下载的路径为和上面创建的路径相同
            request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);

            //设置文件类型
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setMimeType(mimeString);
            //将请求加入请求队列会 downLoadManager会自动调用对应的服务执行者个请求
            id = downLoadManager.enqueue(request);
            prefs.edit().putLong(DL_ID, id).commit();

        }
        return id;
    }

    //文件的安装 方法
    public static boolean install(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public int queryDownloadStatus() {
        int state = -1;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(prefs.getLong(DL_ID, 0));
        Cursor c = downLoadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.v("tianhao", "STATUS_PAUSED");
                    state = 0;
                    break;
                case DownloadManager.STATUS_PENDING:
                    Log.v("tianhao", "STATUS_PENDING");
                    state = 1;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                    Log.v("tianhao", "STATUS_RUNNING");
                    state = 2;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //完成
                    Log.v("tianhao", "STATUS_SUCCESSFUL");
                    state = 3;
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.DOWNLOAD_SUCCESS");
                    context.sendBroadcast(intent);
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    Log.v("tianhao", "STATUS_FAILED");
                    downLoadManager.remove(prefs.getLong(DL_ID, 0));
                    prefs.edit().clear().commit();
                    state = 4;
                    Intent intent2 = new Intent();
                    intent2.setAction("android.intent.action.DOWNLOAD_FAIL");
                    context.sendBroadcast(intent2);
                    break;
            }

        }
        return state;
    }
}