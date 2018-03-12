package com.llx278.uimockerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.llx278.uimocker2.Reflect;
import com.llx278.uimockerdemo.utils.X5WebView;

/**
 *
 * Created by llx on 2018/3/11.
 */

public class WebViewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
       /* final WebView webView = findViewById(R.id.web_view);
        Log.d("main","准备loadur");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.baidu.com");*/

        Log.d("main", "准备load_url");
        X5WebView tencentWebView = findViewById(R.id.tencent_web_view);
        tencentWebView.loadUrl("https://www.baidu.com");

        Log.d("main", "loadUrl结束");
    }
}
