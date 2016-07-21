package com.example.zhuangqf.gallerydemo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {

    Handler mHandler;
    Context mContext;
    PullToRefreshListView mPullToRefreshListView;
    ListView mListView;
    LocalAdapter mLocalAdapter;
    int page = 1;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doWithIntent(getIntent());

        mContext = this;
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.listView);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLocalAdapter = new LocalAdapter(this,
                LocalImageInfo.find(LocalImageInfo.class,null,null,null,null,"10"));

        mListView.setAdapter(mLocalAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Message msg = new Message();
                msg.what = MyApplication.LOCAL_ADD_ID;
                mHandler.sendMessage(msg);
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==MyApplication.LOCAL_ADD_ID){
                    mLocalAdapter.addAll(LocalImageInfo.find(
                            LocalImageInfo.class, null, null, null, null, (page++ * 10) + ",10"));
                    mLocalAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                    if (page * 10 > LocalImageInfo.count(LocalImageInfo.class)){
                        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                }else if(msg.what==MyApplication.LOCAL_UPDATE_ID){
                    mLocalAdapter.clear();
                    mLocalAdapter.addAll(LocalImageInfo.find(
                            LocalImageInfo.class, null, null, null, null,String.valueOf(page * 10)));
                    mLocalAdapter.notifyDataSetChanged();
                }
            }
        };

        registerForContextMenu(mListView);
    }

    @Override
    public void onResume(){
        super.onResume();
        timer = new Timer();
        timer.schedule(new UpdateTask(),1000,1000);
    }

    @Override
    public void onPause(){
        timer.cancel();
        super.onPause();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_local, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LocalImageInfo mInfo = mLocalAdapter.getItem(menuInfo.position-1);

        switch (item.getItemId()){
            case R.id.delete:
                File file  = new File(String.valueOf(mInfo.createAt));
                if(file.exists()) file.delete();
                LocalImageInfo.delete(mInfo);
                return true;
            case R.id.begin:
                Intent intent1 = new Intent(Main2Activity.this,DownloadService.class);
                intent1.setAction(DownloadService.ACTION_DOWNLOAD);
                intent1.putExtra(DownloadService.PARAM_ID, mInfo.getId());
                startService(intent1);
                return true;
            case R.id.stop:
                Intent intent = new Intent(Main2Activity.this,DownloadService.class);
                intent.setAction(DownloadService.ACTION_DOWNLOAD_STOP);
                intent.putExtra(DownloadService.PARAM_ID, mInfo.getId());
                startService(intent);
                return true;
            default:return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(!doWithIntent(intent)) super.onNewIntent(intent);
    }

    private boolean doWithIntent(Intent intent){
        if (intent.getAction()==null) return false;
        if(intent.getAction().equals(MyApplication.ACTION_DOWNLOAD)){
            Long mID = intent.getLongExtra("id",-1);
            if(mID!=-1){
                RemoteImageInfo mRemoteInfo =  RemoteImageInfo.findById(RemoteImageInfo.class, mID);
                if(LocalImageInfo.findById(LocalImageInfo.class,mID)!=null) {
                    Toast.makeText(this,mRemoteInfo.title+" had downloaded!",Toast.LENGTH_LONG).show();
                    return true;
                }
                LocalImageInfo mLocalInfo = new LocalImageInfo(mRemoteInfo);
                mLocalInfo.save();
                Intent intent1 = new Intent(Main2Activity.this,DownloadService.class);
                intent1.setAction(DownloadService.ACTION_DOWNLOAD);
                intent1.putExtra(DownloadService.PARAM_ID, mID);
                startService(intent1);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_local:
                Intent intent1 = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class UpdateTask extends TimerTask{

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = MyApplication.LOCAL_UPDATE_ID;
            mHandler.sendMessage(msg);
        }
    }

}
