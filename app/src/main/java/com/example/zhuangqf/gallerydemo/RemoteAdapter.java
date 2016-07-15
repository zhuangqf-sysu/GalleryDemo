package com.example.zhuangqf.gallerydemo;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by zhuangqf on 7/14/16.
 */
public class RemoteAdapter extends ArrayAdapter<RemoteImageInfo> {

    private LayoutInflater mInflater;
    private Context mContext;
    private ActionMode mActionMode;


    public RemoteAdapter(Context context,List<RemoteImageInfo>list) {
        super(context,R.layout.item_remote,list);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) convertView = mInflater.inflate(R.layout.item_remote,null);
        final RemoteImageInfo mInfo = getItem(position);

        TextView mText = (TextView)convertView.findViewById(R.id.titleView);
        ImageView mImage = (ImageView)convertView.findViewById(R.id.imageView);

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
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });

        convertView.setLongClickable(true);

        return convertView;
    }
}
