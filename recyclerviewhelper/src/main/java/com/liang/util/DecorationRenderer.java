package com.liang.util;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;


public class DecorationRenderer {

    public static void drawDecoration(RecyclerView recyclerView, Canvas canvas, View decoration, Rect offset) {
        canvas.save();
        final Rect mTempRect = new Rect();
        if (recyclerView.getLayoutManager().getClipToPadding()) {
            initClipRectForDecoration(mTempRect, recyclerView, decoration);
            canvas.clipRect(mTempRect);
        }

        canvas.translate(offset.left, offset.top);
        decoration.draw(canvas);
        canvas.restore();
    }


    private static void initClipRectForDecoration(Rect clipRect, RecyclerView recyclerView, View decoration) {
        DimensionHelper.initMargins(clipRect, decoration);
        if (LayoutManagerHelper.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
            clipRect.set(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getWidth() - recyclerView.getPaddingRight() - clipRect.right,
                    recyclerView.getHeight() - recyclerView.getPaddingBottom());
        } else {
            clipRect.set(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getWidth() - recyclerView.getPaddingRight(),
                    recyclerView.getHeight() - recyclerView.getPaddingBottom() - clipRect.bottom);
        }
    }

}
