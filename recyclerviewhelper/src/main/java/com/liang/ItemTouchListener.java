package com.liang;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.liang.widget.adapter.BaseAdapter;
import com.liang.widget.adapter.LabelRecyclerAdapter;
import com.liang.widget.listener.OnItemClickListener;

public class ItemTouchListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    GestureDetector mGestureDetector;

    public ItemTouchListener(Context context, OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            int position = view.getChildAdapterPosition(childView);
            mListener.onItemClick(getAdapter(view).getItem(position), position);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
        Log.e("ItemTouchListener", "disallowIntercept: " + disallowIntercept);
    }

    public BaseAdapter getAdapter(RecyclerView view) {
        if (view.getAdapter() instanceof BaseAdapter) {
            return (BaseAdapter) view.getAdapter();
        } else {
            throw new IllegalStateException("A RecyclerView with " +
                    LabelTouchListener.class.getSimpleName() +
                    " requires a " + LabelRecyclerAdapter.class.getSimpleName());
        }
    }
}