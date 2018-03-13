package com.llx278.uimocker2;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.view.View;

import java.util.Map;

/**
 * 代理了部分webView相关执行的api,在微信的X5内核中，因为是通过反射微信的x5webView执行，因此，并不是
 * 所有的方法都能被代理，这里只是选择了几个有用的api.
 * Created by llx on 2018/3/13.
 */

interface WebViewProxy {

    void addJavascriptInterface(Object object, String name);

    boolean canGoBack();

    boolean canGoBackOrForward(int steps);

    boolean canGoForward();

    boolean canZoomIn();

    boolean canZoomOut();

    View findFocus();

    void findNext(boolean forward);

    String getOriginalUrl();

    int getProgress();

    float getScale();

    /**
     * 代理微信的x5内核时，无法拿到微信的WebSettings的实例，因此这里只能是用Object
     */
    Object getSettings();

    String getTitle();

    String getUrl();

    void goBack();

    void goBackOrForward(int steps);

    void goForward();

    void invokeZoomPicker();

    void loadData(String data, String mimeType, String encoding);

    void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl);

    void loadUrl(String url);

    void loadUrl(String url, Map<String, String> additionalHttpHeaders);

    boolean pageDown(boolean bottom);

    boolean pageUp(boolean top);

    void pauseTimers();

    boolean performLongClick();

    void postUrl(String url, byte[] postData);

    void reload();

    void removeJavascriptInterface(String name);

    boolean	requestChildRectangleOnScreen(View child, Rect rect, boolean immediate);

    boolean	requestFocus(int direction, Rect previouslyFocusedRect);

    void	requestFocusNodeHref(Message hrefMsg);

    void	requestImageRef(Message msg);

    void	saveWebArchive(String filename);
    void	setBackgroundColor(int color);
    void	setDownloadListener(Object listener);
    void	setFindListener(Object listener);
    void	setHttpAuthUsernamePassword(String host, String realm, String username, String password);
    void	setInitialScale(int scaleInPercent);
    void setLayerType(int layerType, Paint paint);
    void	setMapTrackballToArrowKeys(boolean setMap);
    void	setNetworkAvailable(boolean networkUp);
    void	setWebChromeClient(Object client);
    void	setWebViewClient(Object client);
    boolean	shouldDelayChildPressedState();
    void	stopLoading();
    void	zoomBy(float zoomFactor);
    boolean	zoomIn();
    boolean	zoomOut();



}
