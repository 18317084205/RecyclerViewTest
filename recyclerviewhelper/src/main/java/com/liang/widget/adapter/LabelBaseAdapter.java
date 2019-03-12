package com.liang.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liang.DecorationAdapterInterface;
import com.liang.LabelTouchListener;
import com.liang.widget.LabelDecoration;

public abstract class LabelBaseAdapter<T, VH extends RecyclerView.ViewHolder>
        extends BaseAdapter<T, VH> implements DecorationAdapterInterface<VH> {

    private OnLabelClickListener mLabelClickListener;

    public LabelBaseAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public OnLabelClickListener getOnLabelClickListener() {
        return mLabelClickListener;
    }

    public void setOnLabelClickListener(OnLabelClickListener labelClickListener, LabelDecoration labelDecoration) {
        this.mLabelClickListener = labelClickListener;
        getParent().addOnItemTouchListener(new LabelTouchListener(getParent(),labelDecoration));
    }

    public interface OnLabelClickListener {
        void onLabelClick(View headerView, int position, long id);
    }
}
