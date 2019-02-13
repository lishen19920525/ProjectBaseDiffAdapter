package com.example.basediffadapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Project:
 * Author: LiShen
 * Time: 2018/11/28 17:48
 */
public class LinearOnlyInsideItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private int space;
    private int orientation;

    public LinearOnlyInsideItemDecoration(Context context, int space, int orientation) {
        this.space = (int) (space * context.getResources().getDisplayMetrics().density);
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) != 0) {
            if (orientation == VERTICAL) {
                outRect.top = space;
            } else if (orientation == HORIZONTAL) {
                outRect.left = space;
            }
        }
    }
}