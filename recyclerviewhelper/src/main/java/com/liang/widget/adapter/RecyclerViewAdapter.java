package com.liang.widget.adapter;

import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.liang.ItemTouchListener;
import com.liang.widget.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class RecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T,VH> {

    public RecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public void add(T object) {
        super.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<T> collection) {
        if (collection != null) {
            super.addAll(collection);
            notifyDataSetChanged();
        }
    }

    @Override
    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }

    @Override
    public void clear() {
        super.clear();
        notifyDataSetChanged();
    }

    @Override
    public void remove(String object) {
        super.remove(object);
        notifyDataSetChanged();
    }

}
