package com.xmrbi.unware.module.main.braodcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xmrbi.unware.module.main.activity.MainActivity;

/**
 * 开机自启广播
 * Created by wzn on 2018/5/16.
 */
public class RestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent bootIntent = new Intent(context, MainActivity.class);
        bootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootIntent);
    }
}
