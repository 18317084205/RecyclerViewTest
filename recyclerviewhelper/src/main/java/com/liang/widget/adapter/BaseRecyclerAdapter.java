package com.liang.widget.adapter;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.liang.ItemTouchListener;
import com.liang.widget.listener.OnItemClickListener;

import java.util.Arrays;
import java.util.Collection;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private ObservableArrayList<T> items;
    private RecyclerView mRecyclerView;
    private final ItemsChangedCallback itemsChangeCallback;

    public BaseRecyclerAdapter(@NonNull RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        items = new ObservableArrayList<>();
        this.itemsChangeCallback = new ItemsChangedCallback();
        this.setHasStableIds(true);
    }

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

    public void remove(T object) {
        items.remove(object);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public void reset(Collection<T> collection) {
        clear();
        if (collection != null) {
            items.addAll(collection);
        }
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ObservableArrayList<T> getItems() {
        return items;
    }

    class ItemsChangedCallback extends ObservableList.OnListChangedCallback<ObservableArrayList<T>> {

        @Override
        public void onChanged(ObservableArrayList<T> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<T> sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition, toPosition);
            notifyItemRangeChanged(Math.min(fromPosition, toPosition), itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<T> sender, int positionStart, int itemCount) {
            Log.d(getClass().getSimpleName(), "onItemRangeRemoved positionStart: " + positionStart + ", itemCount: " + itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }
    }

}
