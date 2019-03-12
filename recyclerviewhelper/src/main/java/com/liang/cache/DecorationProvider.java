package com.liang.cache;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface DecorationProvider {

    View getDecoration(RecyclerView recyclerView, int position);

    void invalidate();
}
