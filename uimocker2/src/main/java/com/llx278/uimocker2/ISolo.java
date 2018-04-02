package com.llx278.uimocker2;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

/**
 * Created by llx on 2018/3/20.
 */

public interface ISolo {
    Clicker getClicker();

    Scroller getScroller();

    Searcher getSearcher();

    ViewGetter getViewGetter();

    Waiter getWaiter();

    Gesture getGesture();

    ActivityUtils getActivityUtils();

    DialogUtils getDialogUtils();

    WebUtils getWebUtils();

    View findViewById(int id);

    View findViewById(int id, long timeout);

    View findViewById(int id, View parent);

    View findViewById(int id, View parent, long timeout);

    void mockSoftKeyBordSearchButton(EditText editText) throws Exception;

    Context getContext();

    void runOnMainSync(Runnable runnable);

    /**
     * 睡眠时间
     * @param duration 时长
     */
    void sleep(long duration);

    /**
     * 基本睡眠的单位 1s
     */
    void littleSleep();

    void littleSleep(int multiple);

    boolean waitForTextAndClick(String regex);
}
