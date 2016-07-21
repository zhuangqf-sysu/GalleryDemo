package com.example.zhuangqf.gallerydemo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zhuangqf on 7/14/16.
 */
public class GetRemoteDataTask extends AsyncTask<Void,Void,Void> {

    Handler mHandler;

    public GetRemoteDataTask(Handler handler){
        this.mHandler = handler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        RemoteImageInfo.deleteAll(RemoteImageInfo.class);
        try {
            URL url = new URL(MyApplication.REMOTE_URL);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(url.openStream()));
            jsonReader.beginArray();
            while (jsonReader.hasNext()){
                jsonReader.beginObject();
                while (jsonReader.hasNext()){
                    if(jsonReader.nextName().equals("url")){
                        RemoteImageInfo mInfo = new RemoteImageInfo();
                        URL url1 = new URL(jsonReader.nextString());
                        JsonReader jsonReader1 = new JsonReader(new InputStreamReader(url1.openStream()));
                        jsonReader1.beginObject();
                        while(jsonReader1.hasNext()){
                            switch (jsonReader1.nextName()){
                                case "id":
                                    mInfo.setId(jsonReader1.nextLong());
                                    break;
                                case "title":
                                    mInfo.title = jsonReader1.nextString();
                                    break;
                                case "url":
                                    mInfo.url = jsonReader1.nextString();
                                    break;
                                case "total_size":
                                    mInfo.size = jsonReader1.nextLong();
                                    break;
                                default:
                                    jsonReader1.skipValue();
                                    break;
                            }
                        }
                        URL url2 = new URL(mInfo.url);
                        URLConnection uc = url2.openConnection();
                        mInfo.size = uc.getContentLength();
                        mInfo.save();
                        jsonReader1.endObject();
                        jsonReader1.close();
                    }else jsonReader.skipValue();
                }
                jsonReader.endObject();
            }
            jsonReader.endArray();
            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void p) {
        Message msg = new Message();
        msg.what = MyApplication.REMOTE_UPDATE_ID;
        mHandler.sendMessage(msg);
    }
}
