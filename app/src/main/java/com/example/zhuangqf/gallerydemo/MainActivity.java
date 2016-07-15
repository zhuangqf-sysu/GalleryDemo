package com.example.zhuangqf.gallerydemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        registerForContextMenu(mListView);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==MyApplication.REMOTE_UPDATE_ID){
                    mRemoteAdapter.clear();
                    mRemoteAdapter.addAll(
                            RemoteImageInfo.find(RemoteImageInfo.class, null, null, null, null, "10"));
                    mRemoteAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                }else if(msg.what==MyApplication.REMOTE_ADD_ID){
                    mRemoteAdapter.addAll(RemoteImageInfo.find(
                                    RemoteImageInfo.class,null,null,null,null,(page++ * 10) + ",10"));
                    mRemoteAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                }
            }
        };

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
            Toast.makeText(this,mRemoteAdapter.getItem(info.position).title,Toast.LENGTH_LONG).show();
            return true;
        }else return super.onContextItemSelected(item);
    }

}
