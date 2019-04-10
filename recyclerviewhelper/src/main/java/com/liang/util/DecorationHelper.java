package com.liang.util;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liang.IDecorationAdapter;
import com.liang.cache.DecorationProvider;

public class DecorationHelper {

    private final IDecorationAdapter mIDecorationAdapter;
    private final DecorationProvider mDecorationProvider;


    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();

    public DecorationHelper(IDecorationAdapter iDecorationAdapter, DecorationProvider decorationProvider) {
        this.mIDecorationAdapter = iDecorationAdapter;
        this.mDecorationProvider = decorationProvider;
    }

    public boolean hasLabelDecoration(View itemView, int orientation, int position) {
        int offset, margin;
        DimensionHelper.initMargins(mTempRect1, itemView);
        if (orientation == LinearLayout.VERTICAL) {
            offset = itemView.getTop();
            margin = mTempRect1.top;
        } else {
            offset = itemView.getLeft();
            margin = mTempRect1.left;
        }

        return offset <= margin && mIDecorationAdapter.getDecorationId(position) >= 0;
    }


    public boolean hasNewDecoration(int position, boolean isReverseLayout) {
        if (indexOutOfBounds(position)) {
            return false;
        }

        long decorationId = mIDecorationAdapter.getDecorationId(position);
        if (decorationId < 0) {
            return false;
        }

        long nextItemDecorationId = -1;
        int nextItemPosition = position + (isReverseLayout ? 1 : -1);
        if (!indexOutOfBounds(nextItemPosition)) {
            nextItemDecorationId = mIDecorationAdapter.getDecorationId(nextItemPosition);
        }
        int firstItemPosition = isReverseLayout ? mIDecorationAdapter.getItemCount() - 1 : 0;
        return position == firstItemPosition || decorationId != nextItemDecorationId;
    }

    private boolean indexOutOfBounds(int position) {
        return position < 0 || position >= mIDecorationAdapter.getItemCount();
    }

    public void initDefaultLabelBounds(Rect bounds, RecyclerView recyclerView, View decoration, View firstView, boolean firstDecoration) {
        int orientation = LayoutManagerHelper.getOrientation(recyclerView);
        initDefaultLabelOffset(bounds, recyclerView, decoration, firstView, orientation);
    }

    public void initFloatingLabelBounds(Rect bounds, RecyclerView recyclerView, View decoration, View firstView, boolean firstDecoration) {
        int orientation = LayoutManagerHelper.getOrientation(recyclerView);
        initFloatingLabelOffset(bounds, recyclerView, decoration, firstView, orientation);

        if (firstDecoration && isLabelDecorationBeingPushedOffscreen(recyclerView, decoration)) {
            View viewAfterNextDecoration = getFirstViewByDecoration(recyclerView, decoration);
            int firstViewUnderDecorationPosition = recyclerView.getChildAdapterPosition(viewAfterNextDecoration);
            View secondDecoration = mDecorationProvider.getDecoration(recyclerView, firstViewUnderDecorationPosition);
            translateDecorationWithNextDecoration(recyclerView, LayoutManagerHelper.getOrientation(recyclerView), bounds,
                    decoration, viewAfterNextDecoration, secondDecoration);
        }
    }

    private void initDefaultLabelOffset(Rect decorationMargins, RecyclerView recyclerView, View decoration, View firstView, int orientation) {

        DimensionHelper.initMargins(mTempRect1, decoration);

        int left = recyclerView.getPaddingLeft();
        int right = recyclerView.getWidth() - recyclerView.getPaddingRight();

        int top = firstView.getTop() - decoration.getHeight();
        int bottom = firstView.getTop();

        decorationMargins.set(left, top, right, bottom);
    }

    private void initFloatingLabelOffset(Rect decorationMargins, RecyclerView recyclerView, View decoration, View firstView, int orientation) {
        int translationX, translationY;
        DimensionHelper.initMargins(mTempRect1, decoration);

        ViewGroup.LayoutParams layoutParams = firstView.getLayoutParams();
        int leftMargin = 0;
        int topMargin = 0;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            leftMargin = marginLayoutParams.leftMargin;
            topMargin = marginLayoutParams.topMargin;
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            translationX = firstView.getLeft() - leftMargin + mTempRect1.left;
            translationY = Math.max(
                    firstView.getTop() - topMargin - decoration.getHeight() - mTempRect1.bottom,
                    getListTop(recyclerView) + mTempRect1.top);
        } else {
            translationY = firstView.getTop() - topMargin + mTempRect1.top;
            translationX = Math.max(
                    firstView.getLeft() - leftMargin - decoration.getWidth() - mTempRect1.right,
                    getListLeft(recyclerView) + mTempRect1.left);
        }

        decorationMargins.set(translationX, translationY, translationX + decoration.getWidth(),
                translationY + decoration.getHeight());
    }

    private boolean isLabelDecorationBeingPushedOffscreen(RecyclerView recyclerView, View LabelDecoration) {
        View viewAfterDecoration = getFirstViewByDecoration(recyclerView, LabelDecoration);
        int firstViewUnderDecorationPosition = recyclerView.getChildAdapterPosition(viewAfterDecoration);
        if (firstViewUnderDecorationPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        boolean isReverseLayout = LayoutManagerHelper.isReverseLayout(recyclerView);
        if (firstViewUnderDecorationPosition > 0 && hasNewDecoration(firstViewUnderDecorationPosition, isReverseLayout)) {
            View nextDecoration = mDecorationProvider.getDecoration(recyclerView, firstViewUnderDecorationPosition);
            DimensionHelper.initMargins(mTempRect1, nextDecoration);
            DimensionHelper.initMargins(mTempRect2, LabelDecoration);

            if (LayoutManagerHelper.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
                int topOfNextDecoration = viewAfterDecoration.getTop() - mTempRect1.bottom - nextDecoration.getHeight() - mTempRect1.top;
                int bottomOfThisDecoration = recyclerView.getPaddingTop() + LabelDecoration.getBottom() + mTempRect2.top + mTempRect2.bottom;
                if (topOfNextDecoration < bottomOfThisDecoration) {
                    return true;
                }
            } else {
                int leftOfNextDecoration = viewAfterDecoration.getLeft() - mTempRect1.right - nextDecoration.getWidth() - mTempRect1.left;
                int rightOfThisDecoration = recyclerView.getPaddingLeft() + LabelDecoration.getRight() + mTempRect2.left + mTempRect2.right;
                if (leftOfNextDecoration < rightOfThisDecoration) {
                    return true;
                }
            }
        }

        return false;
    }

    private void translateDecorationWithNextDecoration(RecyclerView recyclerView, int orientation, Rect translation,
                                                       View currentDecoration, View viewAfterNextDecoration, View nextDecoration) {
        DimensionHelper.initMargins(mTempRect1, nextDecoration);
        DimensionHelper.initMargins(mTempRect2, currentDecoration);
        if (orientation == LinearLayoutManager.VERTICAL) {
            int topOfLabelDecoration = getListTop(recyclerView) + mTempRect2.top + mTempRect2.bottom;
            int shiftFromNextDecoration = viewAfterNextDecoration.getTop() - nextDecoration.getHeight() - mTempRect1.bottom - mTempRect1.top - currentDecoration.getHeight() - topOfLabelDecoration;
            if (shiftFromNextDecoration < topOfLabelDecoration) {
                translation.top += shiftFromNextDecoration;
            }
        } else {
            int leftOfLabelDecoration = getListLeft(recyclerView) + mTempRect2.left + mTempRect2.right;
            int shiftFromNextDecoration = viewAfterNextDecoration.getLeft() - nextDecoration.getWidth() - mTempRect1.right - mTempRect1.left - currentDecoration.getWidth() - leftOfLabelDecoration;
            if (shiftFromNextDecoration < leftOfLabelDecoration) {
                translation.left += shiftFromNextDecoration;
            }
        }
    }

    private View getFirstViewByDecoration(RecyclerView parent, View firstDecoration) {
        boolean isReverseLayout = LayoutManagerHelper.isReverseLayout(parent);
        int step = isReverseLayout ? -1 : 1;
        int from = isReverseLayout ? parent.getChildCount() - 1 : 0;
        for (int i = from; i >= 0 && i <= parent.getChildCount() - 1; i += step) {
            View child = parent.getChildAt(i);
            if (!itemIsObscuredByDecoration(parent, child, firstDecoration, LayoutManagerHelper.getOrientation(parent))) {
                return child;
            }
        }
        return null;
    }

    private boolean itemIsObscuredByDecoration(RecyclerView parent, View item, View decoration, int orientation) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
        DimensionHelper.initMargins(mTempRect1, decoration);

        int adapterPosition = parent.getChildAdapterPosition(item);
        if (adapterPosition == RecyclerView.NO_POSITION || mDecorationProvider.getDecoration(parent, adapterPosition) != decoration) {
            return false;
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            int itemTop = item.getTop() - layoutParams.topMargin;
            int DecorationBottom = getListTop(parent) + decoration.getBottom() + mTempRect1.bottom + mTempRect1.top;
            if (itemTop >= DecorationBottom) {
                return false;
            }
        } else {
            int itemLeft = item.getLeft() - layoutParams.leftMargin;
            int decorationRight = getListLeft(parent) + decoration.getRight() + mTempRect1.right + mTempRect1.left;
            if (itemLeft >= decorationRight) {
                return false;
            }
        }

        return true;
    }

    private int getListTop(RecyclerView view) {
        if (view.getLayoutManager().getClipToPadding()) {
            return view.getPaddingTop();
        } else {
            return 0;
        }
    }

    private int getListLeft(RecyclerView view) {
        if (view.getLayoutManager().getClipToPadding()) {
            return view.getPaddingLeft();
        } else {
            return 0;
        }
    }
}
