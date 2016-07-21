package com.example.zhuangqf.gallerydemo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadService extends IntentService {

    public static String ACTION_DOWNLOAD = "com.example.zhuangqf.gallerydemo.download";
    public static String ACTION_DOWNLOAD_STOP = "com.example.zhuangqf.gallerydemo.download_stop";
    public static String PARAM_ID = "com.example.zhuangqf.gallerydemo.param_id";

    private static ExecutorService mPool;
    private static HashMap<Long,Future<?>> mTaskMap;

    public DownloadService() {
        super("DownloadService");
        mPool = Executors.newFixedThreadPool(3);
        mTaskMap = new HashMap<>();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                handleActionDownload(intent);
            }else if(ACTION_DOWNLOAD_STOP.equals(action)){
                handleActionStop(intent);
            }
        }
    }

    private void handleActionStop(Intent intent){
        Long mID = intent.getLongExtra(PARAM_ID, -1);
        if(mID!=-1) {
            Future<?> future = mTaskMap.get(mID);
            future.cancel(true);
        }
    }

    private void handleActionDownload(Intent intent){
        Long mID = intent.getLongExtra(PARAM_ID, -1);
        if(mID!=-1) {
            RemoteImageInfo mRemoteInfo = RemoteImageInfo.findById(RemoteImageInfo.class, mID);
            broadcast(true,mRemoteInfo.title);
            DownloadRunnable runnable = new DownloadRunnable(this,mID);
            Future<?> future = mPool.submit(runnable);
            mTaskMap.put(mID,future);
        }
    }

    private void broadcast(boolean flag,String title){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadReceiver.DOWNLOAD);
        broadcastIntent.putExtra(DownloadReceiver.FLAG,flag);
        broadcastIntent.putExtra(DownloadReceiver.FILE_NAME,title);
        sendBroadcast(broadcastIntent);
    }

    private class DownloadRunnable implements Runnable{

        Context mContext;
        long mID;

        public DownloadRunnable(Context context, long ID){
            mContext = context;
            mID = ID;
        }

        @Override
        public void run() {
            LocalImageInfo mInfo = LocalImageInfo.findById(LocalImageInfo.class,mID);
            if(mInfo==null) {
                RemoteImageInfo remoteImageInfo = RemoteImageInfo.findById(RemoteImageInfo.class,mID);
                mInfo = new LocalImageInfo(remoteImageInfo);
            }
            try {
                URL mURL = new URL(mInfo.url);
                InputStream in = mURL.openStream();
                OutputStream out = mContext.openFileOutput(String.valueOf(mInfo.createAt),MODE_PRIVATE);
                long hasRead = 0;
                byte[] buff = new byte[1024];

                in.skip(mInfo.progress);
                while((hasRead = in.read(buff))>0){
                    out.write(buff, 0, (int) hasRead);
                    out.flush();
                    mInfo.progress+=hasRead;
                    mInfo.save();
                }
                in.close();
                out.close();
                broadcast(false, mInfo.title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
