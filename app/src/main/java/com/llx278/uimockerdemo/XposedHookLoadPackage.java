package com.llx278.uimockerdemo;

import android.app.Application;
import android.content.Context;

import com.llx278.uimocker2.Logger;
import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.test.SysWebUITest;
import com.llx278.uimockerdemo.test.X5WebUITest;

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
                    XposedBridge.log("进入 com.example.test_webview_demo application！！！！！");
                    final Solo solo = Solo.getInstance((Context) param.args[0]);
                    new Thread(){
                        @Override
                        public void run() {

                            try {
                                X5WebUITest webUITest = new X5WebUITest();
                                webUITest.run(solo);
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }.start();
                }
            });
        }else if ("com.android.browser".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Logger.d("进入 com.android.browser application");
                    final Solo solo = Solo.getInstance((Context) param.args[0]);
                    new Thread(){
                        @Override
                        public void run() {

                            try {
                                SysWebUITest webUITest = new SysWebUITest();
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
