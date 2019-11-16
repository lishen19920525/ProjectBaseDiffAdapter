package com.example.basediffadapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project:
 * Author: LiShen
 * Time: 2019/3/24 16:33
 */
public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V>
        implements IBaseDataOperate<T> {
    private List<T> data = new ArrayList<>();
    private OnItemClickListener<T> onItemClickListener;

    public BaseAdapter() {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void setData(List<T> data) {
        this.data.clear();
        if (data != null)
            this.data.addAll(data);
        notifyDataUpdated();
    }

    @Override
    public void addData(T... t) {
        if (t != null) {
            this.data.addAll(Arrays.asList(t));
            notifyDataUpdated();
        }
    }

    @Override
    public void addData(int index, T... t) {
        if (index >= 0 && index < data.size() && t != null) {
            this.data.addAll(index, Arrays.asList(t));
            notifyDataUpdated();
        } else if (index == 0 && data.size() == 0 && t != null) {
            this.data.addAll(Arrays.asList(t));
            notifyDataUpdated();
        }
    }

    @Override
    public void addData(List<T> data) {
        if (data != null) {
            this.data.addAll(data);
            notifyDataUpdated();
        }
    }

    @Override
    public void addData(int index, List<T> data) {
        if (index >= 0 && index < this.data.size() && data != null) {
            this.data.addAll(index, data);
            notifyDataUpdated();
        } else if (index == 0 && this.data.size() == 0 && data != null) {
            this.data.addAll(data);
            notifyDataUpdated();
        }
    }

    @Override
    public void removeData(int index) {
        if (index >= 0 && index < data.size()) {
            data.remove(index);
            notifyDataUpdated();
        }
    }

    @Override
    public void removeData(T... t) {
        if (t != null) {
            data.removeAll(Arrays.asList(t));
            notifyDataUpdated();
        }
    }

    @Override
    public void replaceData(int index, T t) {
        if (t != null && index >= 0 && index < data.size()) {
            data.set(index, t);
            notifyDataUpdated();
        }
    }

    @Override
    public void removeData(List<T> data) {
        if (data != null) {
            this.data.removeAll(data);
            notifyDataUpdated();
        }
    }

    @Override
    public void clearData() {
        data.clear();
        notifyDataUpdated();
    }

    @Override
    public void notifyDataUpdated() {
        notifyDataSetChanged();
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Nullable
    @Override
    public T getItemData(int index) {
        if (index >= 0 && index < data.size()) {
            return data.get(index);
        } else {
            return null;
        }
    }

    public int onViewHolderItemClick(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (position >= 0 && position < getData().size()) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, getItemData(position));
            }
        }
        return position;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T t);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}