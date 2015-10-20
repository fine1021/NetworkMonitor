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

        private boolean isWifiConnected = false;

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
                        setMobileDataDisable(context);
                        isWifiConnected = true;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        Log.d(TAG, "TYPE_MOBILE");
                        isWifiConnected = false;
                        break;
                }
            } else {
                isWifiConnected = false;
            }
            if (isWifiConnected) return;
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    Log.d(TAG, "NETWORK_TYPE_UNKNOWN");
                    setMobileDataDisable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    Log.d(TAG, "NETWORK_TYPE_GPRS-----2G");
                    setMobileDataDisable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    Log.d(TAG, "NETWORK_TYPE_EDGE-----2G");
                    setMobileDataDisable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    Log.d(TAG, "NETWORK_TYPE_UMTS-----WCDMA 3G");
                    setMobileDataEnable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    Log.d(TAG, "NETWORK_TYPE_CDMA-----CDMA 2G");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_0-----CDMA2000 1xEV-DO 3G");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_A-----3G");
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    Log.d(TAG, "NETWORK_TYPE_1xRTT-----CDMA2000 1xRTT 2G");
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    Log.d(TAG, "NETWORK_TYPE_HSDPA-----3.5G");
                    setMobileDataEnable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    Log.d(TAG, "NETWORK_TYPE_HSUPA-----3.5G");
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    Log.d(TAG, "NETWORK_TYPE_HSPA-----WCDMA 3G");
                    setMobileDataEnable(context);
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    Log.d(TAG, "NETWORK_TYPE_IDEN-----2G");
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    Log.d(TAG, "NETWORK_TYPE_EVDO_B-----EV-DO Rev.B 3G");
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    Log.d(TAG, "NETWORK_TYPE_LTE-----4G");
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    Log.d(TAG, "NETWORK_TYPE_EHRPD-----3G CDMA2000->LTE");
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    Log.d(TAG, "NETWORK_TYPE_HSPAP-----HSPA+ 3G");
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

    private void setMobileDataEnable(Context context) {
        boolean isOpen = MobileDataControl.getMobileDataStatus(context);
        if (!isOpen) {
            MobileDataControl.setMobileDataStatus(context, true);
        }
    }

    private void setMobileDataDisable(Context context) {
        boolean isOpen = MobileDataControl.getMobileDataStatus(context);
        if (isOpen) {
            MobileDataControl.setMobileDataStatus(context, false);
        }
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
