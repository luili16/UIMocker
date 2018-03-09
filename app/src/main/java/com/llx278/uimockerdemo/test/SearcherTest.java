package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.llx278.uimocker2.Filter;
import com.llx278.uimocker2.ReflectUtil;
import com.llx278.uimocker2.Scroller;
import com.llx278.uimocker2.Searcher;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.UIUtil;
import com.llx278.uimockerdemo.R;
import com.llx278.uimockerdemo.widget.MyButton;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * Created by liu on 18-3-9.
 */

public class SearcherTest {

    public void run(Solo solo) throws Exception {

        Log.d("main","进入SearcherTest");

        Thread.sleep(2000);
        Class<? extends Solo> aClass = solo.getClass();
        Field mSearcher = aClass.getDeclaredField("mSearcher");
        mSearcher.setAccessible(true);
        Searcher searcher = (Searcher) mSearcher.get(solo);
        Activity currentActivity = solo.getCurrentActivity();

        TextView text1 = searcher.searchTextViewByText("^text1$", true);
        assertNotNull(text1);
        assertEquals("text1",text1.getText().toString());
        TextView iAmInvisible = searcher.searchTextViewByText("^I am inVisible$", false);
        assertNotNull(iAmInvisible);
        assertEquals("I am inVisible",iAmInvisible.getText().toString());
        ArrayList<TextView> textViewArrayList = searcher.searchTextViewListByText("^text(0-9)?", true);
        assertNotNull(textViewArrayList);
        assertFalse(textViewArrayList.isEmpty());
        assertEquals(9,textViewArrayList.size());
        ScrollView scrollView = currentActivity.findViewById(R.id.container_3_scrollview_1);
        TextView text16View = searcher.searchTextViewByTextWithVerticallyScroll("^scrollText21$",
                true, scrollView,
                Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertNotNull(text16View);
        assertEquals("scrollText21",text16View.getText().toString());

        ScrollView scrollView1 = currentActivity.findViewById(R.id.container_4_scroll_view2);
        ArrayList<TextView> scrollViewTest = searcher.searchTextViewListByTextWithVerticallyScroll("^hello Scroll(0-9)*",
                true, scrollView1,
                Scroller.VerticalDirection.DOWN_TO_UP, 4000);
        assertEquals(20,scrollViewTest.size());

        Button button = searcher.searchButtonByText("^button1$", true);
        assertNotNull(button);
        assertEquals("button1",button.getText().toString());
        ArrayList<Button> buttons = searcher.searchButtonListByText("^button(0-9)?", true);
        assertEquals(2,buttons.size());
        ScrollView scrollView2 = currentActivity.findViewById(R.id.container_6_scrollView);
        ArrayList<Button> buttons1 = searcher.searchButtonListByTextWithVerticallyScroll("btt(0-9)?", true, scrollView2,
                Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertEquals(8,buttons1.size());
        ArrayList<Button> buttons11 = searcher.searchButtonListByTextWithVerticallyScroll("^btt0$", true, scrollView2,
                Scroller.VerticalDirection.UP_TO_DOWN, 2000);
        assertEquals(2,buttons11.size());
        Button button1 = searcher.searchButtonByTextWithVerticallyScroll("^btt6$", true, scrollView2,
                Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertEquals("btt6",button1.getText().toString());

        ScrollView scrollView3 = currentActivity.findViewById(R.id.container_6_scrollView_0);
        final EditText editText = currentActivity.findViewById(R.id.container_6_1_edit_view_8);
        solo.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setText("hello world!");
            }
        });

        EditText editText1 = searcher.searchEditTextByText("^edit3$", true);
        assertEquals("edit3",editText1.getHint().toString());
        ArrayList<EditText> editTextArrayList = searcher.searchEditTextListByText("^edit(0-9)*", true);
        assertEquals(4,editTextArrayList.size());
        ArrayList<EditText> editTextArrayList1 = searcher.searchEditTextListByTextWithVerticallyScroll("^edit(0-9)*",
                true, scrollView3, Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertEquals(11,editTextArrayList1.size());
        EditText editText2 = searcher.searchEditTextByTextWithVerticallyScroll("^hello world!$",true,
                scrollView3, Scroller.VerticalDirection.UP_TO_DOWN,2000);
        assertEquals("hello world!",editText2.getText().toString());

        TextView textView = searcher.searchTByText(TextView.class, "^text1$", true);
        assertEquals("text1",textView.getText().toString());
        ArrayList<Button> buttons2 = searcher.searchTListByText(Button.class, "^btt(0-4)?", true);
        assertEquals(4,buttons2.size());
        ArrayList<EditText> editTextArrayList2 = searcher.searchTListByTextWithVerticallyScroll(EditText.class, "^edit(0-9)?", true, scrollView3,
                Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertEquals(8,editTextArrayList2.size());
        Button button2 = searcher.searchTByTextWithVerticallyScroll(Button.class, "^btt0$", true, scrollView3,
                Scroller.VerticalDirection.DOWN_TO_UP, 2000);
        assertEquals("btt0",button2.getText().toString());

        View view = searcher.searchViewById(R.id.container_3_scrollview_2_bt1, true);
        assertNotNull(view);
        View view1 = searcher.searchViewByIdWithVerticallyScroll(R.id.container_6_scrollView_bt_1, true, scrollView2,
                Scroller.VerticalDirection.UP_TO_DOWN, 2000);
        assertNotNull(view1);

        View view2 = searcher.searchViewByFilter("com.llx278.uimockerdemo.widget.MyButton", null, new Filter() {
            @Override
            public boolean match(View view) {
                if (view instanceof MyButton) {
                    if ("mybutton1".equals(((MyButton) view).getText().toString())) {
                        return true;
                    }
                }
                return false;
            }
        }, true);
        assertEquals("mybutton1",((MyButton)view2).getText().toString());
        View view3 = searcher.forceSearchViewByText("^mybutton1$", null, true);
        assertNotNull(view3);


        Log.d("main","done");

    }

}
