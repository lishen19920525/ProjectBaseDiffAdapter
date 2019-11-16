package com.example.basediffadapter;

import java.util.List;

/**
 * Project: ProjectTMS
 * Author: LiShen
 * Time: 2019/3/24 16:22
 */
public interface IBaseDataOperate<T> {
    void setData(List<T> data);

    void addData(T... t);

    void addData(int index, T... t);

    void addData(List<T> data);

    void addData(int index, List<T> data);

    void removeData(int index);

    void removeData(T... t);

    void removeData(List<T> data);

    void replaceData(int index, T t);

    void clearData();

    void notifyDataUpdated();

    List<T> getData();

    T getItemData(int index);
}