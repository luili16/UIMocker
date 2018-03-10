package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.ViewGetter;
import com.llx278.uimockerdemo.R;
import com.llx278.uimockerdemo.widget.UniqueButton;

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

        Class<? extends Solo> aClass = solo.getClass();
        Field mViewGetter = aClass.getDeclaredField("mViewGetter");
        mViewGetter.setAccessible(true);
        ViewGetter viewGetter = (ViewGetter) mViewGetter.get(solo);
        Activity currentActivity = solo.getCurrentActivity();
        boolean text1Ret = solo.waitForTextAppear("^text1$",1000 * 5,false);
        assertTrue(text1Ret);

        List<View> viewsList1 = viewGetter.getViewList();
        assertNotNull(viewsList1);

        List<View> viewList2 = viewGetter.getViewList(true);
        assertNotNull(viewList2);

        Activity viewGetterTestActivity = solo.getCurrentActivity();
        View container1 = viewGetterTestActivity.findViewById(R.id.container_1);
        List<View> viewList3 = viewGetter.getViewList(container1, true);
        assertEquals(6,viewList3.size());
        List<View> viewList4 = viewGetter.getViewList(container1, false);
        assertEquals(8,viewList4.size());

        View container3 = viewGetterTestActivity.findViewById(R.id.container_3);
        List<View> scrollViewList = viewGetter.getViewList(container3, true);
        assertEquals(12,scrollViewList.size());
        List<View> scrollViewList1 = viewGetter.getViewList(container3, false);
        assertEquals(21,scrollViewList1.size());

        ArrayList<View> viewListById = viewGetter.getViewListById(R.id.list_item_test_1);
        assertEquals(7,viewListById.size());

        ArrayList<View> listViewList = viewGetter.getViewListById(R.id.container_4_list_view1);
        assertEquals(1,listViewList.size());
        ListView listView = (ListView) listViewList.get(0);
        ArrayList<View> viewListById1 = viewGetter.getViewListById(R.id.list_item_test_1, listView);
        assertEquals(7,viewListById1.size());

        ArrayList<View> scrollViewList3 =viewGetter.getViewListById(R.id.container_4_scroll_view2);
        assertEquals(1,scrollViewList3.size());
        ScrollView scrollViewContainer = (ScrollView) scrollViewList3.get(0);
        ArrayList<View> scrollListById2 = viewGetter.getViewListById(R.id.list_item_2_id, scrollViewContainer, true);
        assertEquals(8,scrollListById2.size());
        ArrayList<View> scrollListById3 = viewGetter.getViewListById(R.id.list_item_2_id, scrollViewContainer, false);
        assertEquals(20,scrollListById3.size());

        ArrayList<TextView> viewListByClass = viewGetter.getViewListByClass(TextView.class, true, container1, true);
        assertEquals(5,viewListByClass.size());
        ArrayList<TextView> viewListByClass2 = viewGetter.getViewListByClass(TextView.class, true, container1, false);
        assertEquals(7,viewListByClass2.size());
        ArrayList<TextView> viewListByClass1 = viewGetter.getViewListByClass(TextView.class, false, container1, true);
        assertEquals(2,viewListByClass1.size());

        ArrayList<View> viewListByName = viewGetter.getViewListByName("com.llx278.uimockerdemo.widget.UniqueButton", null, false);
        assertEquals(2,viewListByName.size());
        ArrayList<View> viewListByName1 = viewGetter.getViewListByName("com.llx278.uimockerdemo.widget.MyButton", null, true);
        assertEquals(2,viewListByName1.size());

        ScrollView sv = viewGetterTestActivity.findViewById(R.id.container_3_scrollview_1);
        TextView scrollViewText1 = viewGetterTestActivity.findViewById(R.id.container_3_scrollview_1_text_1);
        View scrollOrListParent = viewGetter.getScrollParent(scrollViewText1);
        assertEquals(sv,scrollOrListParent);
        HorizontalScrollView hs = viewGetterTestActivity.findViewById(R.id.container_3_scrollview_2);
        Button bt1 = viewGetterTestActivity.findViewById(R.id.container_3_scrollview_2_bt1);
        View scrollPatent = viewGetter.getScrollParent(bt1);
        assertEquals(hs,scrollPatent);
        ListView lv = viewGetterTestActivity.findViewById(R.id.container_4_list_view1);
        View tv =  lv.getChildAt(1);
        View scrollParent1 = viewGetter.getScrollParent(tv);
        assertEquals(lv,scrollParent1);
        WebView wv = viewGetterTestActivity.findViewById(R.id.container_4_web_view3);
        viewGetter.getScrollParent(wv);
        assertEquals(wv,wv);
        RecyclerView rv = viewGetterTestActivity.findViewById(R.id.container_4_recycler_view4);
        View childAt = rv.getChildAt(2);
        View scrollParent2 = viewGetter.getScrollParent(childAt);
        assertEquals(rv,scrollParent2);

        List<View> viewList = viewGetter.getViewList();
        final Button button1 = viewGetterTestActivity.findViewById(R.id.container_1_button_1);
        solo.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                button1.setText("I");
                button1.setFocusableInTouchMode(true);
                button1.requestFocus();
            }
        });
        View freshestView = viewGetter.getFreshestView(viewList);
        assertEquals(button1,freshestView);


        UniqueButton uniqueButton = (UniqueButton) solo.findViewById(R.id.container_1_unique_button_2);
        boolean isShown = viewGetter.isViewSufficientlyShown(uniqueButton);
        assertFalse(isShown);
        Log.d("main","done!");
        Thread.sleep(200);
        currentActivity.finish();
    }
}
