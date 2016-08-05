package com.example.zhuangqf.gallerydemo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends AppCompatActivity {

    Handler mHandler;
    Context mContext;
    PullToRefreshListView mPullToRefreshListView;
    ListView mListView;
    RemoteAdapter mRemoteAdapter;
    int page = 1;
    NetworkReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new NetworkReceiver();

        mContext = this;
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.listView);
        mListView = mPullToRefreshListView.getRefreshableView();
        mRemoteAdapter = new RemoteAdapter(this,
                RemoteImageInfo.find(RemoteImageInfo.class,null,null,null,null,"10"));

        mListView.setAdapter(mRemoteAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetRemoteDataTask(mHandler).execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Message msg = new Message();
                msg.what = MyApplication.REMOTE_ADD_ID;
                mHandler.sendMessage(msg);
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==MyApplication.REMOTE_UPDATE_ID){
                    page = 1;
                    mRemoteAdapter.clear();
                    mRemoteAdapter.addAll(
                            RemoteImageInfo.find(RemoteImageInfo.class, null, null, null, null, "10"));
                    mRemoteAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }else if(msg.what==MyApplication.REMOTE_ADD_ID){
                    mRemoteAdapter.addAll(RemoteImageInfo.find(
                                    RemoteImageInfo.class,null,null,null,null,(page++ * 10) + ",10"));
                    mRemoteAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                    if (page * 10 > LocalImageInfo.count(LocalImageInfo.class)){
                        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
            }
        };

        registerForContextMenu(mListView);
        new GetRemoteDataTask(mHandler).execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_remote, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.download) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent intent = new Intent(this,Main2Activity.class);
            intent.putExtra("id", mRemoteAdapter.getItem(info.position-1).getId());
            intent.setAction(MyApplication.ACTION_DOWNLOAD);
            startActivity(intent);
            return true;
        }else return super.onContextItemSelected(item);
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
                Intent intent1 = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        mReceiver = new NetworkReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        unregisterForContextMenu(mListView);
        super.onDestroy();
    }

}
