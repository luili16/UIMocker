package com.llx278.uimocker2;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

public class Scheduler {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());


    public static void runOnMainSync(Runnable r) {
        validateNotAppThread();
        SyncRunnable sr = new SyncRunnable(r);
        mHandler.post(sr);
        sr.waitForComplete();
    }

    private static final void validateNotAppThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException(
                    "This method can not be called from the main application thread");
        }
    }

    private static final class SyncRunnable implements Runnable {
        private final Runnable mTarget;
        private boolean mComplete;

        public SyncRunnable(Runnable target) {
            mTarget = target;
        }

        public void run() {
            mTarget.run();
            synchronized (this) {
                mComplete = true;
                notifyAll();
            }
        }

        public void waitForComplete() {
            synchronized (this) {
                while (!mComplete) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

}
