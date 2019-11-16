package com.example.basediffadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * 高级自带Diff局部刷新功能Adapter
 * 在对List<T> 进行操作完后 请勿使用notifyDataSetChanged()方法
 * 严格调用 setData addData removeData getData updateData 等方法
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2018/9/11 15:16
 */
public abstract class BaseDiffAdapter<T extends IBaseDiff, V extends RecyclerView.ViewHolder> extends BaseAdapter<T, V> {

    private Class<T> tClass;

    private List<T> oldData = new ArrayList<>();// 用于新旧对比的旧数据
    private List<T> realData = new ArrayList<>();// 当前数据;

    public BaseDiffAdapter(Class<T> tClass) {
        this.tClass = tClass;
        setHasStableIds(true);
    }

    /**
     * 正常数据绑定
     *
     * @param holder
     * @param position
     */
    public abstract void bindViewAndData(V holder, int position, T data);

    /**
     * 局部数据绑定
     *
     * @param holder
     * @param position
     * @param newData
     */
    public abstract void bindPartViewAndData(V holder, int position, T newData);

    @Override
    public final void onBindViewHolder(V holder, int position) {

    }

    @Override
    public final void onBindViewHolder(@NonNull V holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            Log.i("BaseDiff", getClass().getSimpleName() + " bindViewAndData: " + position);
            bindViewAndData(holder, position, getItemData(position));
        } else {
            Log.d("BaseDiff", getClass().getSimpleName() + " bindPartViewAndData: " + position);
            bindPartViewAndData(holder, position, (T) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return realData.size();
    }

    @Override
    public final long getItemId(int position) {
        T t = getItemData(position);
        if (t == null) {
            return new Random().nextLong(); // 这个不可能出现
        } else {
            String id = t.diffId();
            return (id + "BaseDiffAdapter" + id.length()).hashCode();
        }
    }

    @Override
    public void replaceData(int index, T t) {
        if (t != null && index >= 0 && index < realData.size()) {
            realData.set(index, t);
            notifyDataUpdated();
        }
    }

    /**
     * 更新数据 核心方法
     *
     * @param data
     * @param callback
     */
    public void setData(List<T> data, OnCompleteCallback callback) {
        // FIXME 对比出现了问题 暂时用主线程做
        long start = System.currentTimeMillis();
        // 因为 setHasStableIds 需要过滤重复元素
        List<T> outData = filterRepeatItems(data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new BaseDiffCallback(oldData, new ArrayList<>(outData)));
        result.dispatchUpdatesTo(this);
        List<T> oldTemp = JSON.parseList(JSON.toJSONString(outData), tClass);
        oldData.clear();
        if (oldTemp != null)
            oldData.addAll(oldTemp);
        realData.clear();
        realData.addAll(outData);
        if (callback != null)
            callback.onComplete();
        long cost = System.currentTimeMillis() - start;
        Log.w("BaseDiff", getClass().getSimpleName() + " 本次Diff对比耗时 = " + cost + " ms");
    }

    @Override
    public void setData(List<T> outData) {
        setData(outData, null);
    }

    @Override
    public void addData(T... t) {
        addData(Arrays.asList(t));
    }

    @Override
    public void addData(int index, T... t) {
        addData(index, Arrays.asList(t));
    }

    @Override
    public void addData(List<T> list) {
        if (list != null) {
            List<T> temp = new ArrayList<>(realData);
            temp.addAll(list);
            setData(temp);
        }
    }

    @Override
    public void addData(int index, List<T> list) {
        if (list != null) {
            List<T> temp = new ArrayList<>(realData);
            if (temp.size() == 0 && index == 0) {
                temp.addAll(list);
            } else {
                temp.addAll(index, list);
            }
            setData(temp);
        }
    }

    @Override
    public void removeData(int position) {
        if (position >= 0 && position < realData.size()) {
            List<T> temp = new ArrayList<>(realData);
            temp.remove(position);
            setData(temp);
        }
    }

    public void removeData(String id) {
        int flag = -1;
        for (int i = 0; i < realData.size(); i++) {
            if (realData.get(i).diffId().equals(id)) {
                flag = i;
                break;
            }
        }
        if (flag >= 0) {
            removeData(flag);
        }
    }

    @Override
    public void removeData(T... t) {
        removeData(Arrays.asList(t));
    }

    @Override
    public void removeData(List<T> list) {
        if (list != null) {
            List<T> temp = new ArrayList<>(realData);
            temp.removeAll(list);
            setData(temp);
        }
    }

    @Override
    public void clearData() {
        setData(new ArrayList<>());
    }

    @Override
    public void notifyDataUpdated() {
        setData(new ArrayList<>(realData));
    }

    @Override
    public List<T> getData() {
        return realData;
    }

    @Nullable
    @Override
    public T getItemData(int index) {
        if (index >= 0 && index < realData.size()) {
            return realData.get(index);
        } else {
            return null;
        }
    }

    /**
     * 去除重复元素
     *
     * @param data
     * @return
     */
    private List<T> filterRepeatItems(List<T> data) {
        List<T> outData = new ArrayList<>(data != null ? data : new ArrayList<>());

        Set<String> notRepeat = new HashSet<>();
        List<T> repeatItems = new ArrayList<>();
        for (T t : outData) {
            if (notRepeat.contains(t.diffId())) {
                repeatItems.add(t);
            } else {
                notRepeat.add(t.diffId());
            }
        }
        outData.removeAll(repeatItems);
        return outData;
    }

    private class BaseDiffCallback extends DiffUtil.Callback {
        private List<T> oldList;
        private List<T> newList;

        private BaseDiffCallback(List<T> oldList, List<T> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).diffId().equals(newList.get(newItemPosition).diffId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).diffContent().equals(newList.get(newItemPosition).diffContent());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            T newBean = newList.get(newItemPosition);
            if (!areContentsTheSame(oldItemPosition, newItemPosition)) {
                return newBean;
            }
            return null;
        }
    }

    public interface OnCompleteCallback {
        void onComplete();
    }
}