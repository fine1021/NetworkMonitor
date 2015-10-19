package com.fine.networkmonitor.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * MobileDataControl
 */
public class MobileDataControl {

    private static String TAG = "MobileDataControl";

    public static void setMobileDataStatus(Context context, boolean enabled) {
        boolean isOpen = getMobileDataStatus(context);
        if (enabled == isOpen) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setMobileDataEnabled(context, enabled);
        } else {
            setDataEnabled(context, enabled);
        }
    }

    public static boolean getMobileDataStatus(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getMobileDataEnabled(context);
        } else {
            return getDataEnabled(context);
        }
    }

    private static void setMobileDataEnabled(Context context, boolean enabled) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            method.setAccessible(true);
            method.invoke(manager, enabled);
            Log.d(TAG, "setMobileDataEnabled success");
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
            Log.d(TAG, "setDataEnabled success");
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

