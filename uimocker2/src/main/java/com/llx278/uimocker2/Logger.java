package com.llx278.uimocker2;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by llx on 05/01/2018.
 */

public class Logger {
    private static final String TAG = "uimocker";
    private static final boolean DEBUG = true;
    private static final boolean XPOSED_ENABLE = false;
    private static final String SPILT = "  ";

    public static void d(String tag, String msg) {
        if(DEBUG) {
            if (XPOSED_ENABLE) {
                XposedBridge.log(tag + SPILT + timeStamp2DateStr(System.currentTimeMillis()) + SPILT  + msg);
            } else {
                Log.d(tag,msg);
            }
        }
    }

    public static void d(String msg) {
        d(TAG,msg);
    }

    public static void i(String tag,String msg) {
        if (DEBUG){
            if (XPOSED_ENABLE){
                XposedBridge.log(tag + SPILT + timeStamp2DateStr(System.currentTimeMillis()) +  SPILT  +msg);
            } else {
                Log.i(TAG,msg);
            }
        }
    }

    public static void i(String msg){
        i(TAG,msg);
    }

    public static void e(String tag,String msg,Throwable e){
        if (XPOSED_ENABLE) {
            XposedBridge.log(tag + SPILT + timeStamp2DateStr(System.currentTimeMillis()) +  SPILT  +msg);
            if (e != null) {
                XposedBridge.log(e);
            }
        } else {
            if (e != null) {
                Log.e(tag,msg,e);
            }
        }
    }

    public static void e(String msg,Throwable e) {
        e(TAG,msg,e);
    }

    private static String timeStamp2DateStr(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp));
    }
}
