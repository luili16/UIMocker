package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.ViewGetter;
import com.llx278.uimockerdemo.R;
import com.llx278.uimockerdemo.widget.UniqueButton;

import junit.framework.Assert;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 *
 * Created by llx on 2018/2/22.
 */

public class ViewGetterTest {

    public void run(Solo solo) throws Exception {
        Activity currentActivity = solo.getCurrentActivity();
        assertEquals("com.llx278.uimockerdemo.MainActivity",currentActivity.getClass().getName());

        boolean ret = solo.waitForTextAndClick("^ViewGetterTest$");
        assertEquals(true,ret);

        boolean resumeRet = solo.waitForOnResume("com.llx278.uimockerdemo.ViewGetterTestActivity");
        assertEquals(true,resumeRet);

        Class<? extends Solo> aClass = solo.getClass();
        Field mViewGetter = aClass.getDeclaredField("mViewGetter");
        mViewGetter.setAccessible(true);
        ViewGetter viewGetter = (ViewGetter) mViewGetter.get(solo);
        boolean text1Ret = solo.waitForText("^text1$");
        assertTrue(text1Ret);

        List<View> viewsList1 = viewGetter.getViewList();
        assertNotNull(viewsList1);

        List<View> viewList2 = viewGetter.getViewList(true);
        assertNotNull(viewList2);

        Activity viewGetterTestActivity = solo.getCurrentActivity();
        View container = viewGetterTestActivity.findViewById(R.id.container);
        List<View> viewList3 = viewGetter.getViewList(container, true);
        assertEquals(12,viewList3.size());
        List<View> viewList4 = viewGetter.getViewList(container, false);
        assertEquals(14,viewList4.size());


        /*List<View> viewList4 = viewGetter.getViewList(container,false);
        assertEquals(12,viewList3.size());*/
        UniqueButton uniqueButton = (UniqueButton) solo.findViewById(R.id.testbt1);
        boolean isShown = viewGetter.isViewSufficientlyShown(uniqueButton);
        assertFalse(isShown);

        Log.d("main","done!");
    }
}
