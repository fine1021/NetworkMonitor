package com.example.fine.networkmonitor;

import android.app.Application;

import com.yxkang.android.exception.CrashHandler;

/**
 * MonitorApplication
 */
public class MonitorApplication extends Application {

    private static MonitorApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
    }

    public static MonitorApplication getInstance() {
        if (instance == null) {
            instance = new MonitorApplication();
        }
        return instance;
    }
}
