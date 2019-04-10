package com.liang.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.liang.BaseItemDecoration;
import com.liang.IDecorationAdapter;
import com.liang.cache.LabelDecorationCache;
import com.liang.cache.DecorationProvider;
import com.liang.util.DecorationHelper;
import com.liang.util.DecorationRenderer;
import com.liang.util.DimensionHelper;

public class LabelDecoration extends BaseItemDecoration {
    protected static final String TAG = "LabelDecoration";
    protected final IDecorationAdapter mIDecorationAdapter;
    protected final SparseArray<Rect> mDecorationBounds = new SparseArray<>();
    protected final DecorationProvider mDecorationProvider;
    protected final DecorationHelper mDecorationHelper;
    protected final Rect mTempRect = new Rect();

    public LabelDecoration(IDecorationAdapter iDecorationAdapter) {
        mIDecorationAdapter = iDecorationAdapter;
        mDecorationProvider = new LabelDecorationCache(mIDecorationAdapter);
        mDecorationHelper = new DecorationHelper(mIDecorationAdapter, mDecorationProvider);
        mIDecorationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                invalidateDecorations();
            }
        });
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (mDecorationHelper.hasNewDecoration(itemPosition, isReverseLayout(parent))) {
            View decoration = getDecorationView(parent, itemPosition);
            setItemOffsetsForDecoration(outRect, decoration, getOrientation(parent));
        }
    }

    private void setItemOffsetsForDecoration(Rect itemOffsets, View decoration, int orientation) {
        DimensionHelper.initMargins(mTempRect, decoration);
        if (orientation == LinearLayoutManager.VERTICAL) {
            itemOffsets.top = decoration.getHeight() + mTempRect.top + mTempRect.bottom;
        } else {
            itemOffsets.left = decoration.getWidth() + mTempRect.left + mTempRect.right;
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        final int childCount = parent.getChildCount();
        if (childCount <= 0 || mIDecorationAdapter.getItemCount() <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            boolean hasLabelDecoration = mDecorationHelper.hasLabelDecoration(itemView, getOrientation(parent), position);
            if (hasLabelDecoration || mDecorationHelper.hasNewDecoration(position, isReverseLayout(parent))) {
                View decoration = mDecorationProvider.getDecoration(parent, position);
                Rect decorationBounds = mDecorationBounds.get(position);
                if (decorationBounds == null) {
                    decorationBounds = new Rect();
                    mDecorationBounds.put(position, decorationBounds);
                }
                mDecorationHelper.initDefaultLabelBounds(decorationBounds, parent, decoration, itemView, hasLabelDecoration);
                DecorationRenderer.drawDecoration(parent, canvas, decoration, decorationBounds);
            }
        }
    }

    public int findDecorationPositionUnder(int x, int y) {
        for (int i = 0; i < mDecorationBounds.size(); i++) {
            Rect rect = mDecorationBounds.get(mDecorationBounds.keyAt(i));
            if (rect.contains(x, y)) {
                return mDecorationBounds.keyAt(i);
            }
        }
        return -1;
    }

    public View getDecorationView(RecyclerView parent, int position) {
        return mDecorationProvider.getDecoration(parent, position);
    }

    public void invalidateDecorations() {
        mDecorationProvider.invalidate();
        mDecorationBounds.clear();
    }

}
