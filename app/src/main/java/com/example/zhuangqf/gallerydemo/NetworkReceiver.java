package com.example.zhuangqf.gallerydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    public NetworkReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            ConnectivityManager manager =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo==null||!networkInfo.isAvailable()){
                Toast.makeText(context,"无法连接服务器",Toast.LENGTH_LONG).show();
            }
        }
    }
}
