package com.liang.widget.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.liang.listener.OnSideCheckedListener;
import com.liang.side.Side;

import java.util.LinkedHashSet;

public abstract class DataBindingRelateSideAdapter<T> extends DataBindingLabelAdapter<T> {

    private Side mSide;

    public DataBindingRelateSideAdapter(RecyclerView recyclerView, @NonNull Side side) {
        super(recyclerView);
        mSide = side;
        bindSideBarListener();
    }

    public void addTags(LinkedHashSet<String> tags) {
        mSide.setTags(tags);
    }

    public Side getSide() {
        return mSide;
    }

    private void bindSideBarListener() {
        mSide.OnSideCheckedListener(new OnSideCheckedListener() {
            @Override
            public void onItemChecked(String tag) {
                int position = getPositionForSection(tag);
                if (position != -1) {
//                    getParent().smoothScrollToPosition(position);
                    ((LinearLayoutManager) getParent().getLayoutManager()).scrollToPositionWithOffset(position, 0);
                }
            }
        });

        getParent().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                View childView = recyclerView.findChildViewUnder(dx, dy);
//                if (childView != null) {
//                    int position = recyclerView.getChildAdapterPosition(childView);
//                    onRecyclerViewScrolled(position);
//                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int itemPosition = linearManager.findFirstVisibleItemPosition();
                    onRecyclerViewScrolled(itemPosition);
                }
            }
        });
    }

    protected void onRecyclerViewScrolled(int position) {
    }

    protected abstract int getPositionForSection(String tag);


}
