package com.llx278.uimocker2;

import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Logger 工具类
 * Created by llx on 05/01/2018.
 */

class Logger {
    private static final String TAG = "uimocker";
    private static final boolean DEBUG = true;

    public static void d(String tag, String msg) {
        if(DEBUG) {
            Log.d(tag,msg);
        }
    }

    public static void d(String msg) {
        d(TAG,msg);
    }

    public static void i(String tag,String msg) {
        if (DEBUG){
            Log.i(TAG,msg);
        }
    }

    public static void i(String msg){
        i(TAG,msg);
    }

    public static void e(String tag,String msg,Throwable e){
        if (e != null) {
            Log.e(tag,msg,e);
        }
    }

    public static void e(Throwable e) {
        e("",e);
    }

    public static void e(String msg,Throwable e) {
        e(TAG,msg,e);
    }
}
