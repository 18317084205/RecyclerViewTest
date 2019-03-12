package com.liang.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liang.BaseItemDecoration;

public class IntervalDecoration extends BaseItemDecoration {

    private int mSpanCount;
    private int mInterval;

    public IntervalDecoration(int interval) {
        this.mInterval = interval;
    }

    public IntervalDecoration(int spanCount, int interval) {
        this.mSpanCount = spanCount;
        this.mInterval = interval;
    }

    public void setSpanCount(int spanCount) {
        this.mSpanCount = spanCount;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        int position = parent.getChildAdapterPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            int column = position % mSpanCount;
            outRect.left = mInterval - column * mInterval / mSpanCount;
            outRect.right = (column + 1) * mInterval / mSpanCount;
            if (position < mSpanCount) {
                outRect.top = mInterval;
            }
            outRect.bottom = mInterval;
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.set(0, 0, 0, mInterval);
            } else {
                outRect.set(0, 0, mInterval, 0);
            }
        }

    }


}