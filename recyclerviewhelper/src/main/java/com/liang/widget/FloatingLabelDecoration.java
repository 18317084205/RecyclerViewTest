package com.liang.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liang.util.DecorationRenderer;
import com.liang.widget.adapter.LabelBaseAdapter;

public class FloatingLabelDecoration extends LabelDecoration {

    public FloatingLabelDecoration(LabelBaseAdapter adapter) {
        super(adapter);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();
        if (childCount <= 0 || mAdapter.getItemCount() <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            boolean hasStickyDecoration = mDecorationHelper.hasLabelDecoration(itemView, getOrientation(parent), position);
            if (hasStickyDecoration || mDecorationHelper.hasNewDecoration(position, isReverseLayout(parent))) {
                View decoration = mDecorationProvider.getDecoration(parent, position);
                Rect decorationOffset = mDecorationBounds.get(position);
                if (decorationOffset == null) {
                    decorationOffset = new Rect();
                    mDecorationBounds.put(position, decorationOffset);
                }
                mDecorationHelper.initFloatingLabelBounds(decorationOffset, parent, decoration, itemView, hasStickyDecoration);
                DecorationRenderer.drawDecoration(parent, canvas, decoration, decorationOffset);
            }
        }
    }

}
