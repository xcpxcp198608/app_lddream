package com.wiatec.lddream;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.px.common.constant.CommonApplication;
import com.px.common.constant.CommonConstant;
import com.px.common.entities.ImageInfo;
import com.px.common.http.HttpMaster;
import com.px.common.http.listener.DownloadListener;
import com.px.common.http.listener.StringListener;
import com.px.common.http.pojo.DownloadInfo;
import com.px.common.http.pojo.Result;
import com.px.common.provider.AgentProvider;
import com.px.common.utils.Logger;
import com.px.common.utils.NetUtil;

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
                    if(NetUtil.isConnected()) {
                        loadImage();
                    }
                } while (!isDownload);
            }
        }).start();

    }

    private void loadImage() {
        isDownload = true;
        HttpMaster.get(CommonConstant.url.ad_images)
                .param(CommonConstant.key.type, AgentProvider.getType())
                .param(CommonConstant.key.agentId, AgentProvider.getAgentId())
                .param(CommonConstant.key.platform, AgentProvider.getPlatform())
                .param(CommonConstant.key.location, "36")
                .enqueue(new StringListener() {
                    @Override
                    public void onSuccess(String s) throws IOException {
                        Result<List<ImageInfo>> result = new Gson().fromJson(s,
                                new TypeToken<Result<List<ImageInfo>>>(){}.getType());
                        if(result == null)return;
                        List<ImageInfo> imageList = result.getData();
                        if(imageList == null || imageList.size() <= 0) return;
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
                .startDownload(new DownloadListener() {
                    @Override
                    public void onPending(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onStart(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onPause(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onProgress(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onFinished(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onCancel(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void onError(DownloadInfo downloadInfo) {

                    }
                });
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
