package com.llx278.uimocker2;

import android.util.Log;
import android.view.KeyEvent;

/**
 *
 * Created by llx on 03/01/2018.
 */

class Sender {
    private static final String TAG = "uimocker";
    private final InstrumentationDecorator mInst;
    private final Sleeper mSleeper;

    Sender(InstrumentationDecorator inst, Sleeper sleeper) {
        mInst = inst;
        mSleeper = sleeper;
    }

    void sendKeyCode(int keyCode){
        mSleeper.sleep();
        try {
            mInst.sendCharacterSync(keyCode);
        } catch (SecurityException e) {
            Log.e(TAG,"Can not perform action!",e);
        }
    }

    void goBack(){
        mSleeper.sleep();
        try {
            mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            mSleeper.sleep();
        }catch (Throwable ignored) {}
    }
}
