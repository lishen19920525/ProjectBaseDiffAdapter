package com.example.basediffadapter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.util.ArrayMap;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Project:
 * Author: LiShen
 * Time: 2018/6/26 16:10
 * Single background thread
 */
public class WorkThreadHelper {
    private static volatile WorkThreadHelper instance;
    private WorkHandler workHandler;
    private HandlerThread workThread;
    private List<WorkMessageCallback> workMessageCallbacks = new ArrayList<>();

    private WorkThreadHelper() {
    }

    public static WorkThreadHelper get() {
        if (instance == null) {
            synchronized (WorkThreadHelper.class) {
                if (instance == null) {
                    instance = new WorkThreadHelper();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable job) {
        getWorkHandler().post(job);
    }

    public void sendMessage(Message msg) {
        getWorkHandler().sendMessage(msg);
    }

    public void sendMessageDelay(Message msg, int delay) {
        getWorkHandler().sendMessageDelayed(msg, delay);
    }

    public void sendMessageAtTime(Message msg, long time) {
        getWorkHandler().sendMessageAtTime(msg, time);
    }

    public void removeMessages(int messageWhat) {
        getWorkHandler().removeMessages(messageWhat);
    }

    public void registerWorkMessageCallback(WorkMessageCallback callback) {
        if (!workMessageCallbacks.contains(callback))
            workMessageCallbacks.add(callback);
    }

    public void unregisterWorkMessageCallback(WorkMessageCallback callback) {
        workMessageCallbacks.remove(callback);
    }

    public void clearWorkMessageCallbacks() {
        workMessageCallbacks.clear();
    }

    public void shutdown() {
        if (workThread != null) {
            workThread.quit();
        }
        if (workHandler != null) {
            workHandler.destroyed = true;
            workHandler.removeCallbacksAndMessages(null);
        }
    }

    private void handleWorkMessage(Message msg) {
        for (WorkMessageCallback callback : workMessageCallbacks) {
            callback.handleWorkMessage(msg);
        }
    }

    private WorkHandler getWorkHandler() {
        if (workThread == null || !workThread.isAlive() || workHandler == null || workHandler.reference.get() == null) {
            workThread = new HandlerThread("WorkThreadHelper", Process.THREAD_PRIORITY_BACKGROUND);
            workThread.start();
            workHandler = new WorkHandler(this, workThread.getLooper());
        }
        return workHandler;
    }

    private static class WorkHandler extends Handler {
        private final WeakReference<WorkThreadHelper> reference;
        private boolean destroyed;

        private WorkHandler(WorkThreadHelper workThreadHelper, Looper Looper) {
            super(Looper);
            reference = new WeakReference<>(workThreadHelper);
        }

        @Override
        public void handleMessage(Message msg) {
            WorkThreadHelper workThreadHelper = reference.get();
            if (workThreadHelper == null || msg == null || destroyed) {
                return;
            }
            workThreadHelper.handleWorkMessage(msg);
        }
    }

    public interface WorkMessageCallback {
        void handleWorkMessage(Message msg);
    }
}