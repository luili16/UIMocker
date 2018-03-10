package com.llx278.uimockerdemo.test;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.Solo;
import com.llx278.uimockerdemo.R;

import junit.framework.Assert;

import java.lang.reflect.Field;

/**
 * Created by llx on 2018/3/10.
 */

public class ClickerTest {

    public void run(Solo solo) throws Exception {
        Log.d("main","进入ClickerTest");
        Thread.sleep(2000);

        Class<? extends Solo> aClass = solo.getClass();
        Field mClicker = aClass.getDeclaredField("mClicker");
        mClicker.setAccessible(true);
        Clicker clicker = (Clicker) mClicker.get(solo);
        Activity currentActivity = solo.getCurrentActivity();

        final Button bt = currentActivity.findViewById(R.id.container_1_mybutton_1);
        solo.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bt.setText("bt1-Clicked");
                    }
                });
                bt.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        bt.setText("bt1-longClick");
                        return true;
                    }
                });
            }
        });
        float[] location = getClickCoordinates(bt);
        boolean b = clicker.clickOnScreen(location[0], location[1]);
        Assert.assertTrue(b);
        boolean found = false;
        long endTime = SystemClock.uptimeMillis() + 1000;
        while (SystemClock.uptimeMillis() < endTime) {
            String text = bt.getText().toString();
            if ("bt1-Clicked".equals(text)) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);
        boolean b1 = clicker.longClickOnScreen(location[0], location[1], 2000);
        Assert.assertTrue(b1);
        found = false;
        endTime = SystemClock.uptimeMillis() + 1000;
        while (SystemClock.uptimeMillis() < endTime) {
            String text = bt.getText().toString();
            if ("bt1-longClick".equals(text)) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);

        final Button button = currentActivity.findViewById(R.id.container_2_mybutton_1);
        solo.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.setText("bt2-clicked");
                    }
                });
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        button.setText("bt2-longClicked");
                        return true;
                    }
                });
            }
        });
        boolean b2 = clicker.clickOnView(button);
        Assert.assertTrue(b2);
        found = false;
        endTime = SystemClock.uptimeMillis() + 1000;
        while (SystemClock.uptimeMillis() < endTime) {
            if ("bt2-clicked".equals(button.getText().toString())) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);
        boolean b3 = clicker.longClickOnView(button, 2000);
        Assert.assertTrue(b3);
        found = false;
        endTime = SystemClock.uptimeMillis() + 1000;
        while (SystemClock.uptimeMillis() < endTime) {
            if ("bt2-longClicked".equals(button.getText().toString())) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);

        HorizontalScrollView container =  currentActivity.findViewById(R.id.container_3_scrollview_2);
        final Button target = currentActivity.findViewById(R.id.container_3_scrollview_2_bt1);
        solo.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                target.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        target.setText("bt3-click");
                    }
                });
                target.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        target.setText("bt3-longClick");
                        return true;
                    }
                });
            }
        });
        boolean b4 = clicker.dispatchClickEventOnTarget(target, container);
        Assert.assertTrue(b4);
        endTime = SystemClock.uptimeMillis() + 1000;
        found = false;
        while (SystemClock.uptimeMillis() < endTime) {
            if ("bt3-click".equals(target.getText().toString())) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);
        boolean b5 = clicker.dispatchLongClickEventOnTarget(target, container, 2000);
        Assert.assertTrue(b5);
        found = false;
        endTime = SystemClock.uptimeMillis() + 1000;
        while (SystemClock.uptimeMillis() < endTime) {
            if ("bt3-longClick".equals(target.getText().toString())) {
                found = true;
                break;
            }
            Thread.sleep(20);
        }
        Assert.assertTrue(found);
        Log.d("main","done");
        Thread.sleep(100);
        currentActivity.finish();
    }

    private float[] getClickCoordinates(View view) throws Exception {
        int[] xyLocation = new int[2];
        float[] xyToClick = new float[2];
        int trialCount = 0;

        view.getLocationOnScreen(xyLocation);
        while (xyLocation[0] == 0 && xyLocation[1] == 0 && trialCount < 10) {
            Thread.sleep(300);
            view.getLocationOnScreen(xyLocation);
            trialCount++;
        }

        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();
        final float x = xyLocation[0] + (viewWidth / 2.0f);
        float y = xyLocation[1] + (viewHeight / 2.0f);

        xyToClick[0] = x;
        xyToClick[1] = y;

        return xyToClick;
    }

}
