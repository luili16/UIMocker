package com.llx278.uimockerdemo;

import android.app.Application;
import android.util.Log;

import com.llx278.uimocker2.Solo;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by llx on 2018/2/22.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Solo solo = new Solo(getApplicationContext());
        new SoloThread(solo).start();
    }
}
