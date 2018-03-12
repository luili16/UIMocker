package com.llx278.uimocker2;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 根据webview的类型(是系统的webview还是腾讯的x5)选择不同的注入方式
 * Created by llx on 2018/3/12.
 */

public class WebViewInjector {

    private WebElementCreator mWebElementCreator;
    private RobotiumWebClient mRobotiumWebCLient;
    private InstrumentationDecorator mInstrumentation;

    public WebViewInjector(WebElementCreator creator,InstrumentationDecorator instrumentation) {
        mRobotiumWebCLient = new RobotiumWebClient(instrumentation, mWebElementCreator);
        mWebElementCreator = creator;
        mInstrumentation = instrumentation;
    }



    /**
     * 对指定的webview通过反射替换WebChromeClient
     * @param webView
     * @return
     */
    public boolean injectAndExecuteJs(final Object webView, final String function) {

        try {
            if (webView == null) {
                XposedBridge.log("webVIew 是空!");
                return false;
            }

            if (webView instanceof WebView) {
                XposedBridge.log("是系统的webview");



            } else if (ReflectUtil.isAssignedFrom("com.tencent.smtt.sdk.WebView",webView)) {
                XposedBridge.log("是腾讯的x5WebView");

                final XC_MethodHook.Unhook andHookMethod = XposedHelpers.findAndHookMethod(
                        "com.tencent.smtt.sdk.WebChromeClient",
                        webView.getClass().getClassLoader(),
                        "onJsPrompt",
                        "com.tencent.smtt.sdk.WebView",
                        String.class,
                        String.class,
                        String.class,
                        "com.tencent.smtt.export.external.interfaces.JsPromptResult",
                        mRobotiumWebCLient.getX5WebChromeHookedCallback());
                final String javaScript = getJavaScriptAsString();
                mInstrumentation.runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        Class<?> aClass = webView.getClass();
                        try {
                            Method loadUrl = aClass.getMethod("loadUrl", String.class);
                            loadUrl.invoke(webView,"javascript:" + javaScript + function);
                        } catch (Exception e) {
                            XposedBridge.log(e);
                        }
                    }
                });
                // 执行js结束
                return true;
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }

        return false;
    }

    /**
     * Returns the JavaScript file RobotiumWeb.js as a String
     *
     * @return the JavaScript file RobotiumWeb.js as a {@code String}
     */

    private String getJavaScriptAsString() {
        InputStream fis = null;
        try {
            fis = new FileInputStream("/data/local/tmp/RobotiumWeb.js");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder javaScript = new StringBuilder();

        try {
            BufferedReader input =  new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while (( line = input.readLine()) != null){
                javaScript.append(line);
                javaScript.append("\n");
            }
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return javaScript.toString();
    }

}
