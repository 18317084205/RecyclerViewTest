package com.liang.widget.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.liang.IDecorationAdapter;
import com.liang.widget.holder.DataBindingViewHolder;

public abstract class DataBindingLabelAdapter<T> extends DataBindingAdapter<T> implements IDecorationAdapter<DataBindingViewHolder> {

    public DataBindingLabelAdapter(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public final DataBindingViewHolder onCreateDecorationHolder(ViewGroup parent, int decorationType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getItemDecorationLayout(decorationType), parent, false);
        return new DataBindingViewHolder(viewDataBinding.getRoot());
    }

    protected abstract int getItemDecorationLayout(int decorationType);

    @Override
    public final void onBindDecorationHolder(DataBindingViewHolder holder, int position) {
        onBindDecorationHolder(DataBindingUtil.getBinding(holder.itemView), getItem(position));
    }

    protected abstract void onBindDecorationHolder(ViewDataBinding viewDataBinding, T item);

}
