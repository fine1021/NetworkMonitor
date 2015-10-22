package com.example.fine.networkmonitor;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.yxkang.android.exception.CrashHandler;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * MonitorApplication
 */
public class MonitorApplication extends Application {

    private static final String TAG = "MonitorApplication";
    private static MonitorApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configureLog(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
        CrashHandler.getInstance().init(this);
    }

    public void configureLog(boolean SDCardExist) {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                getPackageName() + File.separator + "log" + File.separator + "track.log";
        try {
            LogConfigurator logConfigurator = new LogConfigurator();
            logConfigurator.setResetConfiguration(true);
            logConfigurator.setFileName(fileName);
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setLevel("org.apache", Level.ERROR);
            logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
            logConfigurator.setMaxFileSize(1024 * 1024 * 5);
            logConfigurator.setUseFileAppender(SDCardExist);
            logConfigurator.setImmediateFlush(true);
            logConfigurator.configure();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public static MonitorApplication getInstance() {
        if (instance == null) {
            instance = new MonitorApplication();
        }
        return instance;
    }
}
