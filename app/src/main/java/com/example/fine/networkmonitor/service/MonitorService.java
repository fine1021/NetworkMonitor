package com.example.fine.networkmonitor.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.fine.networkmonitor.util.MobileDataControl;

/**
 * MonitorService
 */
public class MonitorService extends Service {

    private static final int FLAG_ON = 0x01;

    private static final int FLAG_OFF = 0x02;

    public static final String TAG = "MonitorService";

    public static final String ServiceName = MonitorService.class.getName();

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        private boolean isWifi = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, action);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int type = networkInfo.getType();
                switch (type) {
                    case ConnectivityManager.TYPE_WIFI:
                        Log.d(TAG, "TYPE_WIFI");
                        boolean isOpen = MobileDataControl.getMobileDataStatus(context);
                        if (isOpen) {
                            MobileDataControl.setMobileDataStatus(context, false);
                        }
                        isWifi = true;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        Log.d(TAG, "TYPE_MOBILE");
                        isWifi = false;
                        break;
                }
            }
            if (isWifi) return;
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    Log.d(TAG, "NETWORK_TYPE_UNKNOWN");
                    break;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    Log.d(TAG, "NETWORK_TYPE_GPRS-----2G");
                    boolean isOpen = MobileDataControl.getMobileDataStatus(context);
                    if (isOpen) {
                        MobileDataControl.setMobileDataStatus(context, false);
                    }
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    Log.d(TAG, "NETWORK_TYPE_EDGE-----2G");
                    isOpen = MobileDataControl.getMobileDataStatus(context);
                    if (isOpen) {
                        MobileDataControl.setMobileDataStatus(context, false);
                    }
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    Log.d(TAG, "NETWORK_TYPE_UMTS-----Unicom 3G");
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    Log.d(TAG, "NETWORK_TYPE_CDMA-----Telecom 3G");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_0");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_A");
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    Log.d(TAG, "NETWORK_TYPE_1xRTT");
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    Log.d(TAG, "NETWORK_TYPE_HSDPA-----Mobile 3G");
                    isOpen = MobileDataControl.getMobileDataStatus(context);
                    if (!isOpen) {
                        MobileDataControl.setMobileDataStatus(context, true);
                    }
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    Log.d(TAG, "NETWORK_TYPE_HSUPA");
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    Log.d(TAG, "NETWORK_TYPE_IDEN");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_B");
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    Log.d(TAG, "NETWORK_TYPE_LTE");
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    Log.d(TAG, "NETWORK_TYPE_EHRPD");
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    Log.d(TAG, "NETWORK_TYPE_HSPAP");
                    break;

            }
        }
    };

    private IntentFilter intentFilter = new IntentFilter();

    private ConnectivityManager connectivityManager;

    private TelephonyManager telephonyManager;

    public static void setServiceEnabled(Context context, boolean enabled) {
        Intent intent = new Intent(context, MonitorService.class);
        intent.putExtra("flag", enabled ? FLAG_ON : FLAG_OFF);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        intentFilter.addAction(MobileDataControl.CONNECTIVITY_CHANGED);
        registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int flag = intent.getIntExtra("flag", FLAG_OFF);
            Log.d(TAG, "flag = " + flag);
            switch (flag) {
                case FLAG_OFF:
                    stopSelf(startId);
                    break;
                case FLAG_ON:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(networkReceiver);
    }
}
