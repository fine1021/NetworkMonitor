package com.example.fine.networkmonitor.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Context Util
 */
public class ContextUtil {

    public static boolean isServiceAlive(Context context, String className) {
        boolean isAlive = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(100);
        if (serviceInfos.isEmpty()) {
            isAlive = false;
        } else {
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                if (serviceInfo.service.getClassName().equals(className)) {
                    isAlive = true;
                    break;
                }
            }
        }
        return isAlive;
    }
}
