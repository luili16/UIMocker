package com.tencent.smtt.sdk;

import android.graphics.Bitmap;

import android.os.Message;
import android.view.View;

/**
 * Created by llx on 2018/3/11.
 */

public class WebChromeClient {

    Object mOriginalWebChromeClient;

    public WebChromeClient(Object originalWebChromeClient) {
        mOriginalWebChromeClient = originalWebChromeClient;
    }

    public Bitmap getDefaultVideoPoster() {
        return null;
    }

    public void getVisitedHistory(Object var1) {
    }

    public boolean onConsoleMessage(Object var1) {
        return false;
    }

    public boolean onCreateWindow(Object var1, boolean var2, boolean var3, Message var4) {
        return false;
    }

    public void onGeolocationPermissionsHidePrompt() {
    }

    public void onGeolocationPermissionsShowPrompt(String var1, Object var2) {

    }

    public void onHideCustomView() {
    }

    public boolean onJsAlert(Object var1, String var2, String var3, Object var4) {
        return false;
    }

    public boolean onJsConfirm(Object var1, String var2, String var3, Object var4) {
        return false;
    }

    public boolean onJsPrompt(Object var1, String var2, String var3, String var4, Object var5) {



        return false;
    }

    public boolean onJsBeforeUnload(Object var1, String var2, String var3, Object var4) {
        return false;
    }

    public boolean onJsTimeout() {
        return true;
    }

    public void onProgressChanged(Object var1, int var2) {
    }

    public void onReachedMaxAppCacheSize(long var1, long var3, Object var5) {

    }

    public void onReceivedIcon(Object var1, Bitmap var2) {
    }

    public void onReceivedTouchIconUrl(Object var1, String var2, boolean var3) {
    }

    public void onReceivedTitle(Object var1, String var2) {
    }

    public void onRequestFocus(Object var1) {
    }

    public void onShowCustomView(View var1, Object var2) {
    }

    public void onShowCustomView(View var1, int var2, Object var3) {
    }

    public void onCloseWindow(Object var1) {
    }

    public View getVideoLoadingProgressView() {
        return null;
    }

    public void openFileChooser(Object var1, String var2, String var3) {

    }

    public boolean onShowFileChooser(Object var1, Object var2, Object var3) {
        return false;
    }

}
