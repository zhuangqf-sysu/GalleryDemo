package com.example.zhuangqf.gallerydemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DownloadReceiver extends BroadcastReceiver {

    public final static String FILE_NAME = "com.example.zhuangqf.gallery.file_name";
    public final static String FLAG = "com.example.zhuangqf.gallery.flag";
    public final static String DOWNLOAD = "com.example.zhuangqf.gallerydemo.download";

    private final static int NOTIFICATION_BEGIN_ID = 0x1001;

    public DownloadReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent==null) return;
        final String action = intent.getAction();
        if(action.equals(DOWNLOAD)){
            String fileName = intent.getStringExtra(FILE_NAME);
            Boolean flag = intent.getBooleanExtra(FLAG, true);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText(fileName);
            if(flag) mBuilder.setContentTitle("Download Begin");
            else mBuilder.setContentTitle("Download Finish");
            NotificationManager mManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.notify(NOTIFICATION_BEGIN_ID,mBuilder.build());
         }
    }
}
