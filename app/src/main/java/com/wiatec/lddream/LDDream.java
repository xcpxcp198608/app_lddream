package com.wiatec.lddream;

import android.content.Intent;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by patrick on 11/07/2017.
 * create time : 5:07 PM
 */

public class LDDream extends DreamService{

    private ImageView ivDream;
    private Subscription subscription;
    private boolean show;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(true);
        setFullscreen(true);
        setContentView(R.layout.ld_dream);
        ivDream = (ImageView) findViewById(R.id.ivDream);
        show = true;
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        startService(new Intent(this, ImageDownloadService.class));
        subscription = Observable.interval(0, 8000, TimeUnit.MILLISECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(show) {
                            showImage();
                        }
                    }
                });
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(subscription != null) {
            show = false;
            subscription.unsubscribe();
        }
    }

    private void showImage(){
        String path = getImagePath();
        if(path != null){
            Glide.with(this).load(getImagePath()).placeholder(R.drawable.img_hold4)
                    .error(R.drawable.img_hold4).dontAnimate().into(ivDream);
        }
    }

    private String getImagePath(){
        File file = new File(Application.AD_IMAGE_PATH);
        if(!file.exists()){
            return null;
        }
        File [] files = file.listFiles();
        if(files == null || files.length <= 0){
            return null;
        }
        int i = new Random().nextInt(files.length);
        File file1 = files[i];
        return file1.getAbsolutePath();
    }
}
