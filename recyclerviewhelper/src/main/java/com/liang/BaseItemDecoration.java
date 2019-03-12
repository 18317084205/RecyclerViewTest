package com.liang;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.liang.util.LayoutManagerHelper;

public abstract class BaseItemDecoration extends RecyclerView.ItemDecoration {
    protected int getOrientation(RecyclerView parent) {
        return LayoutManagerHelper.getOrientation(parent);
    }

    protected boolean isReverseLayout(RecyclerView parent) {
        return LayoutManagerHelper.isReverseLayout(parent);
    }
}
