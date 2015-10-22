package com.example.fine.networkmonitor;

import android.app.Application;
import android.os.Environment;

import com.yxkang.android.exception.CrashHandler;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * MonitorApplication
 */
public class MonitorApplication extends Application {

    private static MonitorApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLog4j();
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }

    private void initLog4j() {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                getPackageName() + File.separator + "log" + File.separator + "track.log";
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(fileName);
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
    }

    public static MonitorApplication getInstance() {
        if (instance == null) {
            instance = new MonitorApplication();
        }
        return instance;
    }
}
