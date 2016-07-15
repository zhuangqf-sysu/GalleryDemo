package com.example.zhuangqf.gallerydemo;

import com.orm.SugarRecord;

/**
 * Created by zhuangqf on 7/14/16.
 */

public class RemoteImageInfo extends SugarRecord {
    String title;
    String url;

    public RemoteImageInfo(){}

    public RemoteImageInfo(Long id,String title,String url){
        this.setId(id);
        this.title = title;
        this.url = url;
    }
}
