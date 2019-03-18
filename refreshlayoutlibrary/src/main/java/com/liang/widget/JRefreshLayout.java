package com.liang.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;


public class JRefreshLayout extends ViewGroup implements NestedScrollingParent
        , NestedScrollingChild {

    private static final String TAG = JRefreshLayout.class.getSimpleName();
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private final int mMediumAnimationDuration;
    private int mTouchSlop;
    private boolean mRefreshing = false;
    private boolean mReturningToStart;
    private float mTotalDragDistance = -1;


    //滚动内容区
    private View mTarget;
    //底部布局，顶部布局
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private View mHeaderView;
    private View mFooterView;

    private int mCurrentTargetOffsetTop;

    private int mHeaderHeight = (int) (100 * getResources().getDisplayMetrics().density);
    private int mFooterHeight = (int) (100 * getResources().getDisplayMetrics().density);
    private boolean mNestedScrollInProgress;
    private int mTotalUnconsumed;
    private int mSpinnerOffsetEnd;
    private boolean isUped;
    private boolean isDowned;
    private boolean mUsingCustomStart;
    private int mOriginalOffsetTop;

    public JRefreshLayout(Context context) {
        this(context, null);
    }

    public JRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        setWillNotDraw(false);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        initHeaderLayout();

        initFooterLayout();

        setChildrenDrawingOrderEnabled(true);
        mSpinnerOffsetEnd = mHeaderHeight / 2;
        mTotalDragDistance = mSpinnerOffsetEnd;
        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mHeaderHeight;

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    private void initHeaderLayout() {
        mHeaderLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight);
        layoutParams.gravity = Gravity.CENTER;
        mHeaderLayout.setLayoutParams(layoutParams);
        mHeaderLayout.setBackgroundColor(Color.DKGRAY);
        addView(mHeaderLayout);
    }

    private void initFooterLayout() {
        mFooterLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mFooterHeight);
        layoutParams.gravity = Gravity.CENTER;
        mFooterLayout.setLayoutParams(layoutParams);
        mFooterLayout.setBackgroundColor(Color.BLUE);
        addView(mFooterLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTarget = ensureTarget();
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        mHeaderLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) mHeaderHeight, MeasureSpec.EXACTLY));

        mFooterLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) mFooterHeight, MeasureSpec.EXACTLY));
    }

    private View ensureTarget() {
        if (mTarget != null) {
            return mTarget;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!child.equals(mHeaderLayout) && !child.equals(mFooterLayout)) {
                return child;
            }
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }

        final View child = ensureTarget();

        if (child == null) {
            return;
        }

        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        int headerWidth = mHeaderLayout.getMeasuredWidth();
        int headerHeight = mHeaderLayout.getMeasuredHeight();

        mHeaderLayout.layout((width / 2 - headerWidth / 2), -headerHeight,
                (width / 2 + headerWidth / 2), 0);

        int footerWidth = mFooterLayout.getMeasuredWidth();
        int footerHeight = mFooterLayout.getMeasuredHeight();

        mFooterLayout.layout((width / 2 - footerWidth / 2), height,
                (width / 2 + footerWidth / 2), height + footerHeight);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    public boolean canChildScrollUp() {
//        if (mChildScrollUpCallback != null) {
//            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
//        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    //判断是否可以上拉
    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return mTarget.canScrollVertically(1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        if (mReturningToStart && ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i("CGQ", "不拦截子view滚动");
            mReturningToStart = false;
            if (isUped || isDowned) {
                finishSpinner(0);
            }
        }
        if (isEnabled() && !mReturningToStart && (!canChildScrollUp() || !canChildScrollDown())
                && !mRefreshing && !mNestedScrollInProgress) {
            handled = onTouchEvent(ev);
            Log.i("CGQ", "事件分发");
        }

        //Log.i("CGQ", "handled=" + handled);
        //父控件消费，子空间就不执行了。父控件不消费，再交给子空间处理
        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
//            mCircleView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }


    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }


    private void moveSpinner(float overScrollTop) {

        boolean isUping = false;
        if (overScrollTop < 0) {
            overScrollTop = -overScrollTop;
        } else {
            isUping = true;
        }

        float originalDragPercent = overScrollTop / mTotalDragDistance;
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overScrollTop) - mTotalDragDistance;
        float slingshotDist = mUsingCustomStart ? mSpinnerOffsetEnd - mOriginalOffsetTop
                : mSpinnerOffsetEnd;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mHeaderLayout.getVisibility() != View.VISIBLE) {
            mHeaderLayout.setVisibility(View.VISIBLE);
        }


        if (overScrollTop < mTotalDragDistance) {

        } else {

        }
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, isUping);
    }

    private void finishSpinner(int i) {

    }

    private void setTargetOffsetTopAndBottom(int offset, boolean isUping) {
        if (mTarget != null) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);//正数向下移动，负数向上移动
            //topLayout.bringToFront();
            if (isUping) {
                ViewCompat.offsetTopAndBottom(mHeaderLayout, offset);
            } else {
                ViewCompat.offsetTopAndBottom(mFooterLayout, offset);
            }
//            mContenViewOffsetTop = contentLayout.getTop();
        }

    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child);
    }
    public interface OnChildScrollDownCallback {
        boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child);
    }
}
