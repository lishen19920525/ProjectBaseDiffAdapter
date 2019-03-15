package com.example.demo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by LiShen on 2019/3/15 14:44
 * Project: ProjectBaseDiffAdapter
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
