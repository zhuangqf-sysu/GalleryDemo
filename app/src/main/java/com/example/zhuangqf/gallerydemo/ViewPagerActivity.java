package com.example.zhuangqf.gallerydemo;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.List;
import java.util.MissingResourceException;

public class ViewPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    UnderlinePageIndicator mIndicator;
    List<? extends RemoteImageInfo>mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        if(intent.getAction().equals(MyApplication.ACTION_LOCAL)) {
            mList = LocalImageInfo.find(LocalImageInfo.class, null);
        }else mList = RemoteImageInfo.find(RemoteImageInfo.class,null);

        viewPager = (ViewPager)findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),mList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setFades(false);
        mIndicator.setViewPager(viewPager);
    }
}
