package com.liang.widget.adapter;

import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.liang.ItemTouchListener;
import com.liang.widget.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private ObservableArrayList<T> items;
    private RecyclerView mRecyclerView;

    public BaseAdapter(@NonNull RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        items = new ObservableArrayList<>();
        this.setHasStableIds(true);
    }

    public RecyclerView getParent() {
        return mRecyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mRecyclerView.addOnItemTouchListener(new ItemTouchListener(mRecyclerView.getContext(), onItemClickListener));
    }



    public void add(T object) {
        items.add(object);
    }

    public void add(int index, T object) {
        items.add(index, object);
    }

    public void addAll(Collection<T> collection) {
        if (collection != null) {
            items.addAll(collection);
        }
    }

    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
    }

    public void remove(String object) {
        items.remove(object);
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ObservableArrayList<T> getItems() {
        return items;
    }

    protected void resetItems(ObservableArrayList<T> sender) {
        this.items = sender;
    }

}
