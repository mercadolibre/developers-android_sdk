package com.mercadolibre.android.sdk;

import android.util.Log;

/**
 * Utility class used to log events (for the moment, only on the Logcat)
 */
public final class MeliLogger {


    public static boolean DEBUG = false;

    public static final String TAG = "meli_sdk";

    public static void log(String tag, String msg) {
        if (DEBUG) {
            if (msg != null) {
                Log.d(tag, msg);
            }
        }
    }

    public static void log(String msg) {
        log(TAG, msg);
    }

    public static void error(String tag, String msg) {
        if (DEBUG) {
            if (msg != null) {
                Log.e(tag, msg);
            }
        }
    }

    public static void error(String msg) {
        error(TAG, msg);
    }

    public static void warn(String tag, String msg) {
        if (DEBUG) {
            if (msg != null) {
                Log.w(tag, msg);
            }
        }
    }

    public static void warn(String msg) {
        warn(TAG, msg);
    }

    public static void info(String tag, String msg) {
        if (DEBUG) {
            if (msg != null) {
                Log.i(tag, msg);
            }
        }
    }

    public static void info(String msg) {
        info(TAG, msg);
    }

    public static void logException(Exception e) {
        if (DEBUG) {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
}
