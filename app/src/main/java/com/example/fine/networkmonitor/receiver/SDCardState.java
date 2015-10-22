package com.example.fine.networkmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fine.networkmonitor.MonitorApplication;

/**
 * SDCardState
 */
public class SDCardState extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            MonitorApplication.getInstance().configureLog(true);
        } else {
            MonitorApplication.getInstance().configureLog(false);
        }
    }
}
