package com.example.demo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basediffadapter.BaseDiffAdapter;

/**
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2019/2/13 12:02
 */
public class DemoAdapter extends BaseDiffAdapter<DemoBean, DemoAdapter.ViewHolder> {

    private Activity activity;

    public DemoAdapter(Activity activity, Class<DemoBean> demoBeanClass) {
        super(demoBeanClass);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(activity.getLayoutInflater().inflate(R.layout.item_demo, null));
    }

    @Override
    public void bindViewAndData(ViewHolder holder, int position, DemoBean data) {
        Log.i("BaseDiffAdapter", "bindViewAndData: " + position);
        // 正常数据绑定
        // 配合 setHasStableIds getItemId 防止 图片闪烁
        Integer cache = (Integer) holder.ivItemDemo.getTag(R.id.ivItemDemo);
        if (cache == null || cache != data.icon) {
            holder.ivItemDemo.setImageResource(data.icon);
            holder.ivItemDemo.setTag(R.id.ivItemDemo, data.icon);
        }
        holder.tvItemDemo.setText(data.content);
        holder.tvItemDemoId.setText("ID: " + data.id);
        holder.ivItemDemoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = holder.getAdapterPosition();
                removeData(index);
            }
        });
    }

    @Override
    public void bindPartViewAndData(ViewHolder holder, int position, DemoBean newBean) {
        Log.d("BaseDiffAdapter", "partBindViewAndData: " + position);
        // 局部数据刷新 业务中可能不会每个界面元素都会刷新
        Integer cache = (Integer) holder.ivItemDemo.getTag(R.id.ivItemDemo);
        if (cache == null || cache != newBean.icon) {
            holder.ivItemDemo.setImageResource(newBean.icon);
            holder.ivItemDemo.setTag(R.id.ivItemDemo, newBean.icon);
        }
        holder.tvItemDemo.setText(newBean.content);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivItemDemo;
        private ImageView ivItemDemoDelete;
        private TextView tvItemDemo;
        private TextView tvItemDemoId;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItemDemo = itemView.findViewById(R.id.ivItemDemo);
            ivItemDemoDelete = itemView.findViewById(R.id.ivItemDemoDelete);
            tvItemDemo = itemView.findViewById(R.id.tvItemDemo);
            tvItemDemoId = itemView.findViewById(R.id.tvItemDemoId);
        }
    }
}
