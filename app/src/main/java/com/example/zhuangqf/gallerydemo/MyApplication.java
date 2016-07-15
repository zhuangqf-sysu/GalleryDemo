package com.example.zhuangqf.gallerydemo;

import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by zhuangqf on 7/14/16.
 */
public class MyApplication extends Application {

    public static final String REMOTE_URL = "http://112.74.22.182:3000/images.json";
    public static final int REMOTE_UPDATE_ID = 0x1001;
    public static final int REMOTE_ADD_ID = 0x1002;

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}
