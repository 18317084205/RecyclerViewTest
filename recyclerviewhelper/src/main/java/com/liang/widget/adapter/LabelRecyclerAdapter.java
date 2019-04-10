package com.liang.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liang.IDecorationAdapter;
import com.liang.LabelTouchListener;
import com.liang.widget.LabelDecoration;

public abstract class LabelRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerViewAdapter<T, VH> implements IDecorationAdapter<VH> {

    private OnLabelClickListener mLabelClickListener;

    public LabelRecyclerAdapter(RecyclerView recyclerView) {
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
