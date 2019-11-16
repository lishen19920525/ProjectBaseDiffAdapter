package com.example.basediffadapter;

import android.support.annotation.NonNull;

/**
 * Project: ProjectTMS
 * Author: LiShen
 * Time: 2019/3/16 18:34
 */
public interface IBaseDiff {
    @NonNull
    String diffContent();

    @NonNull
    String diffId();
}