package com.example.zhuangqf.gallerydemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by zhuangqf on 7/14/16.
 */
public class LocalAdapter extends ArrayAdapter<LocalImageInfo> {

    private LayoutInflater mInflater;
    private Context mContext;

    public LocalAdapter(Context context, List<LocalImageInfo> list) {
        super(context,R.layout.item_local,list);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) convertView = mInflater.inflate(R.layout.item_local,null);
        final LocalImageInfo mInfo = getItem(position);

        TextView mText = (TextView)convertView.findViewById(R.id.titleView);
        ImageView mImage = (ImageView)convertView.findViewById(R.id.imageView);
        ProgressBar mBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

        mText.setText(mInfo.title);

        Glide.with(mContext)
                .load(mInfo.url)
                .error(R.mipmap.image_error)
                .placeholder(R.mipmap.preloader)
                .into(mImage);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewPagerActivity.class);
                intent.setAction(MyApplication.ACTION_LOCAL);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });

        double progress = (double)mInfo.progress/(double)mInfo.size;
        mBar.setProgress((int)(progress*100));

        convertView.setLongClickable(true);

        return convertView;
    }
}
