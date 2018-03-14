package com.llx278.uimocker2;

import android.text.TextUtils;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * 根据webview的类型(是系统的webview还是腾讯的x5)选择不同的注入方式
 * Created by llx on 2018/3/12.
 */

class WebViewInjector {
    private XC_MethodHook.Unhook mCurrentHookedMethod;
    private WebElementCreator mWebElementCreator;
    WebViewInjector(WebElementCreator creator) {
        mWebElementCreator = creator;
    }

    boolean injectTo(final Object webView) {

        try {
            if (webView == null) {
                return false;
            }

            if (webView instanceof WebView) {
                mCurrentHookedMethod = XposedHelpers.findAndHookMethod(WebChromeClient.class,
                        "onJsPrompt",
                        WebView.class,
                        String.class,
                        String.class,
                        String.class,
                        JsPromptResult.class,
                        new WebChromeHookedCallback());

            } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView",webView)) {
                 mCurrentHookedMethod = XposedHelpers.findAndHookMethod(
                        "com.tencent.smtt.sdk.WebChromeClient",
                        webView.getClass().getClassLoader(),
                        "onJsPrompt",
                        "com.tencent.smtt.sdk.WebView",
                        String.class,
                        String.class,
                        String.class,
                        "com.tencent.smtt.export.external.interfaces.JsPromptResult",
                        new X5WebChromeHookedCallback());
                return true;
            }
        } catch (Exception e) {
            Logger.e(e);
        }
        return false;
    }

    void unInject() {
        if (mCurrentHookedMethod != null) {
            mCurrentHookedMethod.unhook();
        }
    }

    /**
     * 处理JavaScript执行的结果
     * @param webView 执行javaScript的webView
     * @param message 执行的结果
     * @return true 此结果可以被处理，false，此结果不能被处理
     */
    private boolean handleMessage(View webView,String message) {

        if (!TextUtils.isEmpty(message) && message.startsWith("inject_result")) {
            String tag = message.substring(14, 18);
            String msg = message.substring(19);
            switch (tag) {
                case "elem":
                case "text":
                    packageWebElement(webView,msg);
                    break;
                case "fini":
                    mWebElementCreator.setFinished(true);
                    break;
                default:
            }
            return true;
        }
        return false;
    }

    private void packageWebElement(View webView,String msg) {
        WebViewProxy proxy = WebViewProxyCreator.create(webView);
        float scale = proxy.getScale();
        int[] locationOfWebViewXY = new int[2];
        proxy.getLocationOnScreen(locationOfWebViewXY);
        mWebElementCreator.createWebElementAndAddInList(msg, scale, locationOfWebViewXY);
    }

    private class WebChromeHookedCallback extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            WebView thisWebView = (WebView) param.args[0];
            String message = (String) param.args[2];
            JsPromptResult r = (JsPromptResult) param.args[4];
            if (handleMessage(thisWebView,message)) {
                r.confirm();
                param.setResult(true);
            }
        }
    }

    private class X5WebChromeHookedCallback extends XC_MethodHook{
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            View thisWebView = (View) param.args[0];
            String message = (String) param.args[2];
            Object r = param.args[4];
            if (handleMessage(thisWebView,message)) {
                Reflect reflect = new Reflect(r);
                reflect.method("confirm").invoke();
                param.setResult(true);
            }
        }
    }
}
