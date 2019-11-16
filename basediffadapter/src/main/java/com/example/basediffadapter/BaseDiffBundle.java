package com.example.basediffadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: ProjectSiyou
 * Author: LiShen
 * Time: 2019/4/6 01:02
 */
class BaseDiffBundle<T> {
    @NonNull
    List<T> outData = new ArrayList<>();
    @NonNull
    List<T> oldData = new ArrayList<>();
    @Nullable
    DiffUtil.DiffResult result;
}