package com.liang.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liang.ItemTouchListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private ArrayList<T> items = new ArrayList<>();
    private RecyclerView mRecyclerView;


    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

    public BaseAdapter(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        setHasStableIds(true);
    }

    public RecyclerView getParent() {
        return mRecyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mRecyclerView.addOnItemTouchListener(new ItemTouchListener(mRecyclerView.getContext(), onItemClickListener));
    }

    public void add(T object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, T object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
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


}
