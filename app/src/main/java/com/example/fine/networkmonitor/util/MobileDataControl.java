package com.example.fine.networkmonitor.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * MobileDataControl
 */
public class MobileDataControl {

    private static String TAG = "MobileDataControl";
    public static String MOBILE_DATA_CHANGED = "android.intent.action.ANY_DATA_STATE";        // mobile data TelephonyIntents
    public static String CONNECTIVITY_CHANGED = ConnectivityManager.CONNECTIVITY_ACTION;

    public static void setMobileDataStatus(Context context, boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setMobileDataEnabled(context, enabled);
        } else {
            // setDataEnabled(context, enabled);
            Log.w(TAG, "not supported yet !");
        }
    }

    public static boolean getMobileDataStatus(Context context) {

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getMobileDataEnabled(context);
        } else {
            return getDataEnabled(context);
        }*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
        } else {
            return Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
        }
    }

    private static void setMobileDataEnabled(Context context, boolean enabled) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            method.setAccessible(true);
            method.invoke(manager, enabled);
            Log.d(TAG, "setMobileDataEnabled = " + enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean getMobileDataEnabled(Context context) {
        boolean isOpen = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("getMobileDataEnabled", (Class[]) null);
            method.setAccessible(true);
            isOpen = (boolean) method.invoke(manager, (Object[]) null);
            Log.d(TAG, "getMobileDataEnabled = " + isOpen);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    private static void setDataEnabled(Context context, boolean enabled) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("setDataEnabled", boolean.class);
            method.setAccessible(true);
            method.invoke(manager, enabled);
            Log.d(TAG, "setDataEnabled = " + enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean getDataEnabled(Context context) {
        boolean isOpen = false;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("getDataEnabled", (Class[]) null);
            method.setAccessible(true);
            isOpen = (boolean) method.invoke(manager, (Object[]) null);
            Log.d(TAG, "getDataEnabled = " + isOpen);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isOpen;
    }
}

