package com.example.zhuangqf.gallerydemo;

/**
 * Created by zhuangqf on 7/16/16.
 */
public class LocalImageInfo extends RemoteImageInfo {
    long createAt;
    int state = 0; //0==未开始，1==已开始，2==已结束,3==暂停
    long progress = 0;

    public LocalImageInfo(){
        super();
    }

    public LocalImageInfo(Long id,String title,String url,Long size,
                          Long createAt,int state, Long progress){
        super(id,title,url,size);
        this.createAt = createAt;
        this.state = state;
        this.progress = progress;
    }

    public  LocalImageInfo(RemoteImageInfo remoteImageInfo){
        super(remoteImageInfo.getId(),remoteImageInfo.title,remoteImageInfo.url,remoteImageInfo.size);
        this.progress = 0;
        this.state = 0;
        this.createAt = System.currentTimeMillis();
    }
}
