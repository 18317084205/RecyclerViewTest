package com.liang.cache;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.liang.IDecorationAdapter;
import com.liang.util.LayoutManagerHelper;

public class LabelDecorationCache implements DecorationProvider {

    private final IDecorationAdapter mIDecorationAdapter;
    private final LongSparseArray<View> mDecorations = new LongSparseArray<>();

    public LabelDecorationCache(IDecorationAdapter iDecorationAdapter) {
        mIDecorationAdapter = iDecorationAdapter;
    }

    @Override
    public View getDecoration(RecyclerView parent, int position) {
        long decorationId = mIDecorationAdapter.getDecorationId(position);

        View decoration = mDecorations.get(decorationId);
        if (decoration == null) {
            RecyclerView.ViewHolder viewHolder = mIDecorationAdapter.onCreateDecorationHolder(parent,
                    mIDecorationAdapter.getDecorationType(position));
            mIDecorationAdapter.onBindDecorationHolder(viewHolder, position);
            decoration = viewHolder.itemView;
            if (decoration.getLayoutParams() == null) {
                decoration.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            int widthSpec;
            int heightSpec;

            if (LayoutManagerHelper.getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.UNSPECIFIED);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY);
            }

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(), decoration.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(), decoration.getLayoutParams().height);
            decoration.measure(childWidth, childHeight);
            decoration.layout(0, 0, decoration.getMeasuredWidth(), decoration.getMeasuredHeight());
            mDecorations.put(decorationId, decoration);
        }
        return decoration;
    }

    @Override
    public void invalidate() {
        mDecorations.clear();
    }
}
