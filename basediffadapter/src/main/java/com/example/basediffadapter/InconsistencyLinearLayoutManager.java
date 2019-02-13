package com.example.basediffadapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Project:
 * Author: LiShen
 * Time: 2018/10/17 20:02
 */
public class InconsistencyLinearLayoutManager extends LinearLayoutManager {
    public InconsistencyLinearLayoutManager(Context context) {
        super(context);
    }

    public InconsistencyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public InconsistencyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException ignore) {
        }
    }
}