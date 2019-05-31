package com.liang.widget.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.liang.widget.holder.DataBindingViewHolder;
import com.liang.widget.listener.OnItemClickListener;

/*
 * 参考http://www.cnblogs.com/DoNetCoder/p/7243878.html?utm_source=tuicool&utm_medium=referral
 * */
public abstract class DataBindingAdapter<T> extends BaseAdapter<T, DataBindingViewHolder> {

    private ItemsChangedCallback itemsChangeCallback;
    private OnItemClickListener<T> onItemClickListener = new OnItemClickListener<T>() {
        @Override
        public void onItemClick(T item, int position) {

        }
    };

    public DataBindingAdapter(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
        this.itemsChangeCallback = new ItemsChangedCallback();
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
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getItemLayout(viewType), parent, false);
        return new DataBindingViewHolder(viewDataBinding.getRoot());
    }

    protected abstract int getItemLayout(int viewType);

    @Override
    public final void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
        ViewDataBinding viewDataBinding = DataBindingUtil.getBinding(holder.itemView);
        onBindViewHolder(viewDataBinding, getItem(position), position);
    }

    protected abstract void onBindViewHolder(ViewDataBinding viewDataBinding, T item, int position);


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        getItems().addOnListChangedCallback(itemsChangeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        getItems().removeOnListChangedCallback(itemsChangeCallback);
    }

    class ItemsChangedCallback extends ObservableList.OnListChangedCallback<ObservableArrayList<T>> {

        @Override
        public void onChanged(ObservableArrayList<T> sender) {
            DataBindingAdapter.this.onChanged(sender);
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            DataBindingAdapter.this.onItemRangeChanged(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            DataBindingAdapter.this.onItemRangeInserted(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<T> sender, int fromPosition, int toPosition, int itemCount) {
            DataBindingAdapter.this.onItemRangeMoved(sender);
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            DataBindingAdapter.this.onItemRangeRemoved(sender, positionStart, itemCount);
        }
    }

    private void onChanged(ObservableArrayList<T> sender) {
        resetItems(sender);
        notifyDataSetChanged();
    }


    private void onItemRangeChanged(ObservableArrayList<T> sender, int positionStart, int itemCount) {
        resetItems(sender);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    private void onItemRangeInserted(ObservableArrayList<T> sender, int positionStart, int itemCount) {
        resetItems(sender);
        if (positionStart == 0 && sender.size() == itemCount) {
            onChanged(sender);
        } else {
            notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    private void onItemRangeMoved(ObservableArrayList<T> sender) {
        resetItems(sender);
        notifyDataSetChanged();
    }

    private void onItemRangeRemoved(ObservableArrayList<T> sender, int positionStart, int itemCount) {
        resetItems(sender);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

}
