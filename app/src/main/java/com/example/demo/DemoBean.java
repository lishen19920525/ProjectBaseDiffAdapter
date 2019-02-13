package com.example.demo;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.example.basediffadapter.BaseDiffBean;

/**
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2019/2/13 11:59
 */
public class DemoBean implements BaseDiffBean, Comparable<DemoBean> {
    public int id;
    @DrawableRes
    public int icon;
    public String content;

    public void generateData() {
        id = DemoUtil.generateId();
        icon = DemoUtil.generateRandomIcon();
        content = DemoUtil.generateRandomString();
    }

    @NonNull
    @Override
    public String getDiffContent() {
        return "DemoBean{" +
                "id=" + id +
                ", icon=" + icon +
                ", content='" + content + '\'' +
                '}';
    }

    @NonNull
    @Override
    public String getDiffId() {
        return String.valueOf(id);
    }

    @Override
    public int compareTo(DemoBean o) {
        return (int) (this.id - o.id);
    }
}