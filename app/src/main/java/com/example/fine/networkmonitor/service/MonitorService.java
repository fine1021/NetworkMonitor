package com.example.fine.networkmonitor.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.fine.networkmonitor.util.MobileDataControl;
import com.example.fine.networkmonitor.util.NetworkConstants;
import com.yxkang.android.os.WeakReferenceHandler;

/**
 * MonitorService
 */
public class MonitorService extends Service {

    private static final int FLAG_ON = 0x01;

    private static final int FLAG_OFF = 0x02;

    public static final String TAG = "MonitorService";

    private static final long sInterval = 3000;

    public static final String ServiceName = MonitorService.class.getName();

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        private boolean isWifiConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case NetworkConstants.ACTION_CONNECTIVITY_CHANGED:
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                        int type = networkInfo.getType();
                        switch (type) {
                            case ConnectivityManager.TYPE_WIFI:
                                Log.d(TAG, "TYPE_WIFI");
                                setMobileDataDisable(context);
                                isWifiConnected = true;
                                break;
                            case ConnectivityManager.TYPE_MOBILE:
                                Log.d(TAG, "TYPE_MOBILE");
                                setMobileData(context);
                                isWifiConnected = false;
                                break;
                        }
                    } else {
                        isWifiConnected = false;
                        mHandler.postDelayed(runnable, sInterval);
                    }
                    break;
                case NetworkConstants.ACTION_PRECISE_DATA_CONNECTION_STATE_CHANGED:
                    Log.i(TAG, "PRECISE_DATA_CONNECTION_STATE_CHANGED");
                    int dataStatus = intent.getIntExtra(NetworkConstants.EXTRA_DATA_STATE, NetworkConstants.DATA_UNKNOWN);
                    int networkType = intent.getIntExtra(NetworkConstants.EXTRA_DATA_NETWORK_TYPE, NetworkConstants.NETWORK_TYPE_UNKNOWN);
                    String reason = intent.getStringExtra(NetworkConstants.EXTRA_DATA_CHANGE_REASON);
                    Log.i(TAG, "dataStatus = " + dataStatus);
                    Log.i(TAG, "networkType = " + networkType);
                    Log.i(TAG, "reason = " + reason);
                    break;
                case NetworkConstants.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED:
                    Log.i(TAG, "ANY_DATA_STATE :  isWifiConnected = " + isWifiConnected);
                    if (isWifiConnected) break;
                    setMobileData(context);
                    break;
            }
        }
    };

    private IntentFilter intentFilter = new IntentFilter();

    private ConnectivityManager connectivityManager;

    private TelephonyManager telephonyManager;

    private final MainHandler mHandler = new MainHandler(this);

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    };


    public static void setServiceEnabled(Context context, boolean enabled) {
        Intent intent = new Intent(context, MonitorService.class);
        intent.putExtra("flag", enabled ? FLAG_ON : FLAG_OFF);
        context.startService(intent);
    }

    private void checkNetworkState() {
        Log.i(TAG, "checkNetworkState----------> wait for 3g net");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            setMobileData(this);
            mHandler.postDelayed(runnable, sInterval);
        }
    }

    private void adjustNetworkState() {
        Log.i(TAG, "adjustNetworkState----------> modify mobile data if necessary");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return;
        }
        setMobileData(this);
    }

    private synchronized void setMobileData(Context context) {
        int networkType = telephonyManager.getNetworkType();
        int networkClass = getNetworkClass(networkType);
        Log.i(TAG, "adjust networkType = " + networkType + " | " + "networkClass = " + networkClass);
        if (networkClass < NetworkConstants.NETWORK_CLASS_3_G) {
            setMobileDataDisable(context);
        } else {
            setMobileDataEnable(context);
        }
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

    private int getNetworkClass(int networkType) {
        switch (networkType) {
            case NetworkConstants.NETWORK_TYPE_GPRS:
            case NetworkConstants.NETWORK_TYPE_GSM:
            case NetworkConstants.NETWORK_TYPE_EDGE:
            case NetworkConstants.NETWORK_TYPE_CDMA:
            case NetworkConstants.NETWORK_TYPE_1xRTT:
            case NetworkConstants.NETWORK_TYPE_IDEN:
                return NetworkConstants.NETWORK_CLASS_2_G;
            case NetworkConstants.NETWORK_TYPE_UMTS:
            case NetworkConstants.NETWORK_TYPE_EVDO_0:
            case NetworkConstants.NETWORK_TYPE_EVDO_A:
            case NetworkConstants.NETWORK_TYPE_HSDPA:
            case NetworkConstants.NETWORK_TYPE_HSUPA:
            case NetworkConstants.NETWORK_TYPE_HSPA:
            case NetworkConstants.NETWORK_TYPE_EVDO_B:
            case NetworkConstants.NETWORK_TYPE_EHRPD:
            case NetworkConstants.NETWORK_TYPE_HSPAP:
            case NetworkConstants.NETWORK_TYPE_TD_SCDMA:
                return NetworkConstants.NETWORK_CLASS_3_G;
            case NetworkConstants.NETWORK_TYPE_LTE:
            case NetworkConstants.NETWORK_TYPE_IWLAN:
                return NetworkConstants.NETWORK_CLASS_4_G;
            default:
                return NetworkConstants.NETWORK_CLASS_UNKNOWN;
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
        intentFilter.addAction(NetworkConstants.ACTION_CONNECTIVITY_CHANGED);
        //intentFilter.addAction(NetworkConstants.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        //intentFilter.addAction(NetworkConstants.ACTION_PRECISE_DATA_CONNECTION_STATE_CHANGED);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(networkReceiver, intentFilter);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);
                Log.i(TAG, "listen state = " + state + " | " + "networkType = " + networkType);
                adjustNetworkState();
            }
        }, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int flag = intent.getIntExtra("flag", FLAG_OFF);
            // Log.d(TAG, "flag = " + flag);
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
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(networkReceiver);
    }

    private static class MainHandler extends WeakReferenceHandler<MonitorService> {

        public MainHandler(MonitorService reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(MonitorService reference, Message msg) {
            switch (msg.what) {
                case 0:
                    reference.checkNetworkState();
                    break;
            }
        }
    }
}
