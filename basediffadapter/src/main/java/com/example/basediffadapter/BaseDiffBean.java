package com.example.basediffadapter;

import android.support.annotation.NonNull;

/**
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2019/2/11 15:27
 */
public interface BaseDiffBean {
    @NonNull
    String getDiffContent();

    @NonNull
    String getDiffId();
}