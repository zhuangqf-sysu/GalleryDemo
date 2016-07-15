package com.example.zhuangqf.gallerydemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by zhuangqf on 7/15/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<RemoteImageInfo>mList;

    public ViewPagerAdapter(FragmentManager fm,List<RemoteImageInfo>list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.init(mList.get(position).url);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
