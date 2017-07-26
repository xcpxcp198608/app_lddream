package com.wiatec.lddream;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.px.common.http.HttpMaster;
import com.px.common.http.Listener.StringListener;
import com.px.common.utils.CommonApplication;
import com.px.common.utils.Logger;
import com.px.common.utils.NetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 22/07/2017.
 * create time : 10:59 AM
 */

public class ImageDownloadService extends IntentService {

    public ImageDownloadService() {
        super("ImageDownloadService");
    }
    private boolean isDownload = false;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if(NetUtils.isConnected(CommonApplication.context)) {
                        loadImage();
                    }
                } while (!isDownload);
            }
        }).start();

    }

    private void loadImage() {
        isDownload = true;
        HttpMaster.get(F.url.ad_image)
                .enqueue(new StringListener() {
                    @Override
                    public void onSuccess(String s) throws IOException {
                        List<ImageInfo> imageList = new Gson().fromJson(s,
                                new TypeToken<List<ImageInfo>>(){}.getType());
                        Logger.d(imageList.toString());
                        List<String> nameList = new ArrayList<>();
                        if(imageList.size() > 0){
                            for(ImageInfo i: imageList){
                                nameList.add(i.getName());
                                download(i);
                            }
                            deleteOldImage(nameList);
                        }
                    }

                    @Override
                    public void onFailure(String e) {
                        Logger.d(e);
                    }
                });
    }



    private void download(ImageInfo i) {
        HttpMaster.download(CommonApplication.context)
                .name(i.getName())
                .url(i.getUrl())
                .path(Application.AD_IMAGE_PATH)
                .startDownload(null);
    }

    private void deleteOldImage(List<String> nameList){
        File dir = new File(Application.AD_IMAGE_PATH);
        if(dir.exists()){
            File[] files = dir.listFiles();
            if(files != null && files.length >0){
                for(File file: files){
                    if(!nameList.contains(file.getName())){
                        file.delete();
                    }
                }
            }
        }
    }

}
