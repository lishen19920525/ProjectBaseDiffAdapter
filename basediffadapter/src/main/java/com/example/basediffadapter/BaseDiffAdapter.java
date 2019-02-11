package com.example.basediffadapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 高级自带Diff局部刷新功能Adapter
 * 在对List<T> 进行操作完后 请勿使用notifyDataSetChanged()方法
 * 严格调用 setData addData removeData getData updateData 等方法
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2018/9/11 15:16
 */
public abstract class BaseDiffAdapter<T extends BaseDiffBean, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    private Activity activity;
    private Class<T> tClass;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<T> oldData = new ArrayList<>();// 用于新旧对比的旧数据
    private List<T> data = new ArrayList<>();// 当前数据

    private OnItemClickListener<T> onItemClickListener;
    private boolean canNotRefresh = false;

    public BaseDiffAdapter(Activity activity, Class<T> tClass) {
        this.activity = activity;
        this.tClass = tClass;
        setHasStableIds(true);
    }

    public abstract void bindViewAndData(V holder, int position);

    public abstract void partBindViewAndData(V holder, int position, T newBean);

    @Override
    public abstract V onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(V holder, int position) {
        bindViewAndData(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            partBindViewAndData(holder, position, (T) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<T> outData) {
        setData(outData, null);
    }

    /**
     * 更新数据 核心方法
     *
     * @param outData
     * @param callback
     */
    public void setData(List<T> outData, final OnSetDataFinishCallback callback) {
        if (canNotRefresh) {
            return;
        }
        canNotRefresh = true;

        data.clear();
        if (outData != null) {
            data.addAll(outData);
        }

        WorkThreadHelper.get().execute(new Runnable() {
            @Override
            public void run() {
                // 子线程计算差异
                final DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                        new BaseDiffCallback(
                                new ArrayList<>(oldData),
                                new ArrayList<>(data)));

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // dispatch 到 adapter 进行界面刷新
                        result.dispatchUpdatesTo(BaseDiffAdapter.this);
                    }
                });

                oldData.clear();
                for (T t : data) {
                    // 子线程序列化生成新列表
                    oldData.add(JSON.parseObject(JSON.toJSONString(t), tClass));
                }
                canNotRefresh = false;

                // 如果有回调
                if (callback != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFinish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 加数据
     *
     * @param t
     */
    public void addData(T t) {
        if (t != null) {
            List<T> temp = new ArrayList<>(data);
            temp.add(t);
            setData(temp);
        }
    }

    /**
     * 加数据
     *
     * @param index
     * @param t
     */
    public void addData(int index, T t) {
        if (t != null) {
            List<T> temp = new ArrayList<>(data);
            temp.add(index, t);
            setData(temp);
        }
    }

    /**
     * 加一批数据
     *
     * @param list
     */
    public void addData(List<T> list) {
        if (list != null) {
            List<T> temp = new ArrayList<>(data);
            temp.addAll(list);
            setData(temp);
        }
    }

    /**
     * 删数据
     *
     * @param position
     */
    public void removeData(int position) {
        List<T> temp = new ArrayList<>(data);
        temp.remove(position);
        setData(temp);
    }

    /**
     * 删数据
     *
     * @param id
     */
    public void removeData(String id) {
        int flag = -1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getDiffId().equals(id)) {
                flag = i;
                break;
            }
        }
        if (flag >= 0) {
            removeData(flag);
        }
    }

    /**
     * 删数据
     *
     * @param t
     */
    public void removeData(T t) {
        List<T> temp = new ArrayList<>(data);
        temp.remove(t);
        setData(temp);
    }

    /**
     * 清除
     */
    public void clearData() {
        List<T> temp = new ArrayList<>(data);
        temp.clear();
        setData(temp);
    }

    /**
     * 修改完 {@link #data} 之后 调用此方法
     */
    public void notifyDataUpdated() {
        setData(new ArrayList<>(getData()));
    }

    public List<T> getData() {
        return data;
    }

    public T getItemData(int position) {
        return data.get(position);
    }


    public View inflater(@LayoutRes int layoutId) {
        return getActivity().getLayoutInflater().inflate(layoutId, null);
    }

    public Activity getActivity() {
        return activity;
    }

    /**
     * Item被点击了
     *
     * @param holder
     * @return
     */
    public int onViewHolderItemClick(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position >= 0 && position < getData().size()) {
            if (getOnItemClickListener() != null) {
                getOnItemClickListener().onItemClick(position, getItemData(position));
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

    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
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
            return oldList.get(oldItemPosition).getDiffId().equals(
                    newList.get(newItemPosition).getDiffId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getDiffContent().equals(
                    newList.get(newItemPosition).getDiffContent());
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

    public interface OnSetDataFinishCallback {
        void onFinish();
    }
}