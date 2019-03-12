package com.liang;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.liang.widget.LabelDecoration;
import com.liang.widget.adapter.LabelBaseAdapter;

public class LabelTouchListener implements RecyclerView.OnItemTouchListener {
    private final GestureDetector mTapDetector;
    private final RecyclerView mRecyclerView;
    private final LabelDecoration mLabelDecoration;
    private LabelBaseAdapter.OnLabelClickListener mOnLabelClickListener;

    public LabelTouchListener(final RecyclerView recyclerView, final LabelDecoration labelDecoration) {
        mTapDetector = new GestureDetector(recyclerView.getContext(), new SingleTapDetector());
        mRecyclerView = recyclerView;
        mLabelDecoration = labelDecoration;
        mOnLabelClickListener = getAdapter().getOnLabelClickListener();
    }

    public LabelBaseAdapter getAdapter() {
        if (mRecyclerView.getAdapter() instanceof LabelBaseAdapter) {
            return (LabelBaseAdapter) mRecyclerView.getAdapter();
        } else {
            throw new IllegalStateException("A RecyclerView with " +
                    LabelTouchListener.class.getSimpleName() +
                    " requires a " + LabelBaseAdapter.class.getSimpleName());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        if (mOnLabelClickListener != null) {
            boolean tapDetectorResponse = this.mTapDetector.onTouchEvent(e);
            if (tapDetectorResponse) {
                return true;
            }
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                int position = mLabelDecoration.findDecorationPositionUnder((int) e.getX(), (int) e.getY());
                return position != -1;
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = mLabelDecoration.findDecorationPositionUnder((int) e.getX(), (int) e.getY());
            if (position != -1) {
                View labelView = mLabelDecoration.getDecorationView(mRecyclerView, position);
                long labelId = getAdapter().getDecorationId(position);
                mOnLabelClickListener.onLabelClick(labelView, position, labelId);
                mRecyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                labelView.onTouchEvent(e);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
