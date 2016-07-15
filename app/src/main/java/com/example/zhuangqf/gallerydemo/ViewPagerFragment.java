package com.example.zhuangqf.gallerydemo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewPagerFragment extends Fragment {

    String mUrl;

    public static ViewPagerFragment init(String url) {
        ViewPagerFragment myViewPagerFragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putString("url",url);
        myViewPagerFragment.setArguments(args);
        return myViewPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layoutView = inflater.inflate(R.layout.fragment_view_pager, container,
                false);
        ImageView imageView = (ImageView)layoutView.findViewById(R.id.pager_view);
        Glide.with(this).load(mUrl)
                .placeholder(R.mipmap.preloader)
                .error(R.mipmap.image_error)
                .into(imageView);
        return layoutView;
    }

}
