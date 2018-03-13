package com.llx278.uimocker2;

import android.webkit.WebView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 根据webview的类型(是系统的webview还是腾讯的x5)选择不同的注入方式
 * Created by llx on 2018/3/12.
 */

class WebViewInjector {

    private WebElementCreator mWebElementCreator;
    private SystemWebChromeClient mSystemWebChromeCLient;
    private XC_MethodHook.Unhook mHookedMethod;
    WebViewInjector(WebElementCreator creator) {
        mWebElementCreator = creator;
        mSystemWebChromeCLient = new SystemWebChromeClient(mWebElementCreator);
    }

    /**
     * @param webView
     * @return
     */
    boolean inject(final Object webView, final String function, String frame) {

        try {
            if (webView == null) {
                XposedBridge.log("webVIew 是空!");
                return false;
            }

            if (webView instanceof WebView) {
                XposedBridge.log("是系统的webview");



            } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView",webView)) {
                XposedBridge.log("是腾讯的x5WebView");

                 mHookedMethod = XposedHelpers.findAndHookMethod(
                        "com.tencent.smtt.sdk.WebChromeClient",
                        webView.getClass().getClassLoader(),
                        "onJsPrompt",
                        "com.tencent.smtt.sdk.WebView",
                        String.class,
                        String.class,
                        String.class,
                        "com.tencent.smtt.export.external.interfaces.JsPromptResult",
                        mSystemWebChromeCLient.getX5WebChromeHookedCallback());
                return true;
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }

        return false;
    }

    void unInject() {
        if (mHookedMethod != null) {
            XposedBridge.log("unInject成功");
            mHookedMethod.unhook();
        }
    }
}
