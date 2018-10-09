package com.llx278.uimockerdemo;

import android.app.Application;

import com.llx278.uimocker2.Solo;

/**
 * Created by llx on 2018/2/22.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Solo solo = new Solo(this, null);
        new SoloThread(solo).start();
    }
}
