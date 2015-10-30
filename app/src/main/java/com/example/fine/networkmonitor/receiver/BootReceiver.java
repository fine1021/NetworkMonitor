package com.example.fine.networkmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fine.networkmonitor.service.MonitorService;
import com.yxkang.android.provider.Settings;

/**
 * BootReceiver
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int isMonitor = Settings.Global.getInt(context.getContentResolver(), "monitor_service", 0);
        if (isMonitor == 1) {
            MonitorService.setServiceEnabled(context, true);
        }
    }
}
