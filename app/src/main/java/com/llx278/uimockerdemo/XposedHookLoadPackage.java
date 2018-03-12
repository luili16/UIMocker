package com.llx278.uimockerdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.llx278.uimocker2.Reflect;
import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.test.WebUITest;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 *
 * Created by llx on 2018/3/12.
 */

public class XposedHookLoadPackage implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if ("com.example.test_webview_demo".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("进入 application！！！！！");
                    final Solo solo = Solo.getInstance((Context) param.args[0],null);
                    new Thread(){
                        @Override
                        public void run() {

                            try {
                                WebUITest webUITest = new WebUITest();
                                webUITest.run(solo);
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }.start();
                }
            });
        }
    }


}
