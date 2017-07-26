package com.wiatec.lddream;

import com.px.common.utils.CommonApplication;

/**
 * Created by patrick on 22/07/2017.
 * create time : 11:10 AM
 */

public class Application extends CommonApplication {

    public static String AD_IMAGE_PATH;

    @Override
    public void onCreate() {
        super.onCreate();
        AD_IMAGE_PATH = getExternalFilesDir("ad_images").getAbsolutePath();
    }
}
