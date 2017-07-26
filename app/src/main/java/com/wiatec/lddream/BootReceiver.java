package com.wiatec.lddream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.px.common.utils.EmojiToast;
import com.px.common.utils.Logger;

/**
 * Created by patrick on 22/07/2017.
 * create time : 11:16 AM
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, ImageDownloadService.class));
    }
}
