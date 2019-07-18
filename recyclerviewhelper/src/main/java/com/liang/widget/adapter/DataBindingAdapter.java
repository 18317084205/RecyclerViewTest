package com.liang.widget.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liang.widget.holder.DataBindingViewHolder;
import com.liang.widget.listener.OnItemClickListener;

/*
 * 参考http://www.cnblogs.com/DoNetCoder/p/7243878.html?utm_source=tuicool&utm_medium=referral
 * */
public abstract class DataBindingAdapter<T> extends BaseRecyclerAdapter<T, DataBindingViewHolder> {

    private OnItemClickListener<T> onItemClickListener = new OnItemClickListener<T>() {
        @Override
        public void onItemClick(T item, int position) {

        }
    };

    public DataBindingAdapter(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    @NonNull
    @Override
    public final DataBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = onCreateView(parent, viewType);
        if (view == null) {
            ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getItemLayout(viewType), parent, false);
            view = viewDataBinding.getRoot();
        }
        return new DataBindingViewHolder(view);
    }

    protected View onCreateView(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    protected int getItemLayout(int viewType) {
        return 0;
    }

    @Override
    public final void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
        ViewDataBinding viewDataBinding = DataBindingUtil.getBinding(holder.itemView);
        onBindViewHolder(viewDataBinding, getItem(position), position);
    }

    protected abstract void onBindViewHolder(ViewDataBinding viewDataBinding, T item, int position);

}
