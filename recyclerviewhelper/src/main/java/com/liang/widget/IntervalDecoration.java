package com.liang.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.liang.BaseItemDecoration;

import java.util.logging.Logger;

public class IntervalDecoration extends BaseItemDecoration {

    private int mSpanCount;
    private int mInterval;

    public IntervalDecoration(int interval) {
        this.mInterval = interval;
    }

    public IntervalDecoration(int spanCount, int interval) {
        if (spanCount <= 0) {
            throw new ExceptionInInitializerError("spanCount must > 0");
        }
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
    public final void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                     RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        int position = parent.getChildAdapterPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            initGridLayoutManager(outRect, view, parent, state, position);
        } else if (layoutManager instanceof LinearLayoutManager) {
            initLinearLayoutManager(outRect, view, parent, state, position);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            initStaggeredGridLayoutManager(outRect, view, parent, state, position);
        }
    }


    protected void initLinearLayoutManager(Rect outRect, View view, RecyclerView parent,
                                           RecyclerView.State state, int position) {

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            if (position == 0) {
                outRect.top = mInterval;
            }
            outRect.bottom = mInterval;
        } else {
            if (position == 0) {
                outRect.left = mInterval;
            }
            outRect.right = mInterval;
        }
    }

    protected void initGridLayoutManager(Rect outRect, View view, RecyclerView parent,
                                         RecyclerView.State state, int position) {

        if (mSpanCount <= 0) {
            throw new ExceptionInInitializerError("spanCount must > 0");
        }

        int column = position % mSpanCount;
        outRect.left = mInterval - column * mInterval / mSpanCount;
        outRect.right = (column + 1) * mInterval / mSpanCount;
        if (position < mSpanCount) {
            outRect.top = mInterval;
        }
        outRect.bottom = mInterval;
    }

    protected void initStaggeredGridLayoutManager(Rect outRect, View view, RecyclerView parent,
                                                  RecyclerView.State state, int position) {
        if (mSpanCount <= 0) {
            throw new ExceptionInInitializerError("spanCount must > 0");
        }

        if (position < mSpanCount) {
            outRect.top = mInterval;
        }
        outRect.bottom = mInterval;
        int column = position % mSpanCount;
        outRect.left = mInterval - column * mInterval / mSpanCount;
        outRect.right = (column + 1) * mInterval / mSpanCount;
    }


}