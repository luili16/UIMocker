package com.llx278.uimockerdemo;

import android.app.Application;
import android.util.Log;

/**
 * Created by llx on 2018/2/22.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

       // new SoloThread(getApplicationContext()).start();
    }
}
