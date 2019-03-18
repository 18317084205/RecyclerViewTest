package com.liang.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liang.refresh.R;

public class RefreshLayout extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {
    private static final String TAG = SwipeRefreshLayout.class.getSimpleName();
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private Interpolator mInterpolator;

    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private int mCurrentTargetOffsetTop;
    private View mTarget;

    private int mMediumAnimationDuration;

    boolean mRefreshing = false;
    boolean mLoading = false;
    boolean mRefreshEnabled = false;
    boolean mLoadEnabled = false;


    private float mTotalDragDistance = -1;

    private RelativeLayout mHeaderContainer;
    private RelativeLayout mFooterContainer;

    private int mSpinnerOffsetEnd;

    private int mHeaderViewWidth;// headerView的宽度

    private int mFooterViewWidth;

    private int mHeaderViewHeight;

    private int mFooterViewHeight;

    private int mHeaderViewIndex = -1;
    private int mFooterViewIndex = -1;
    private int mTotalUnconsumed;

    private float mFrom;

    private ValueAnimator mScrollAnimator;

    private OnChildScrollUpCallback mChildScrollUpCallback;
    private OnChildScrollDownCallback mOnChildScrollDownCallback;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadListener mOnLoadListener;
    private OnRefreshAnimatorListener mOnRefreshAnimatorListener;
    private OnLoadAnimatorListener mOnLoadAnimatorListener;

    private View mHeaderView;
    private View mFooterView;

    private float maxProgress = -1f;

    private Handler mHandler = new Handler();

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setHeaderBackgroundColor(int color) {
        mHeaderContainer.setBackgroundColor(color);
    }

    public void setFooterBackgroundColor(int color) {
        mFooterContainer.setBackgroundColor(color);
    }

    private Animator.AnimatorListener mRefreshListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mRefreshing) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnRefreshListener != null) {
                            mOnRefreshListener.onRefresh();
                        }
                        if (mOnRefreshAnimatorListener != null) {
                            mOnRefreshAnimatorListener.onRefreshStart(mHeaderView);
                        }
                    }
                });

            } else {
                reset();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener mLoadListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mLoading) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnLoadListener != null) {
                            mOnLoadListener.onLoad();
                        }
                        if (mOnLoadAnimatorListener != null) {
                            mOnLoadAnimatorListener.onLoadStart(mFooterView);
                        }
                    }
                });

            } else {
                reset();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener mFinishListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mHeaderContainer.setVisibility(View.GONE);
            mFooterContainer.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public void setChildScrollUpCallback(OnChildScrollUpCallback onChildScrollUpCallback) {
        this.mChildScrollUpCallback = onChildScrollUpCallback;
    }

    public void setOnChildScrollDownCallback(OnChildScrollDownCallback onChildScrollDownCallback) {
        this.mOnChildScrollDownCallback = onChildScrollDownCallback;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
    }

    public void setOnRefreshAnimatorListener(OnRefreshAnimatorListener onRefreshAnimatorListener) {
        this.mOnRefreshAnimatorListener = onRefreshAnimatorListener;
    }

    public void setOnLoadAnimatorListener(OnLoadAnimatorListener onLoadAnimatorListener) {
        this.mOnLoadAnimatorListener = onLoadAnimatorListener;
    }

    public void finish() {
        if (mRefreshing) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnRefreshAnimatorListener != null) {
                        mOnRefreshAnimatorListener.onRefreshFinish(mHeaderView);
                    }
                }
            });
        }

        if (mLoading) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnLoadAnimatorListener != null) {
                        mOnLoadAnimatorListener.onLoadFinish(mFooterView);
                    }
                }
            });
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 500);

    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        if (mScrollAnimator != null) {
            mScrollAnimator.setInterpolator(interpolator);
        }
    }

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mHeaderViewWidth = mFooterViewWidth = metrics.widthPixels;
        mHeaderViewHeight = mFooterViewHeight = metrics.heightPixels;
        initHeaderViewContainer();
        initFooterViewContainer();
        setChildrenDrawingOrderEnabled(true);
        mSpinnerOffsetEnd = metrics.heightPixels;
        mTotalDragDistance = DEFAULT_CIRCLE_TARGET * metrics.density;

        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    private void initHeaderViewContainer() {
        mHeaderContainer = new RelativeLayout(getContext());
        mHeaderContainer.setVisibility(View.GONE);
        addView(mHeaderContainer);

    }

    private void initFooterViewContainer() {
        mFooterContainer = new RelativeLayout(getContext());
        mFooterContainer.setVisibility(View.GONE);
        addView(mFooterContainer);
    }

    private void addHeaderView(@NonNull View view) {
        if (view == null) {
            return;
        }
        if (mHeaderContainer == null) {
            return;
        }
        mRefreshEnabled = true;
        mHeaderContainer.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mHeaderContainer.addView(view, layoutParams);
        mHeaderView = view;
    }

    private void addFooterView(@NonNull View view) {
        if (view == null) {
            return;
        }
        if (mFooterContainer == null) {
            return;
        }
        mLoadEnabled = true;
        mFooterContainer.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mFooterContainer.addView(view, layoutParams);
        mFooterView = view;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        // 将新添加的View,放到最后绘制
        if (mHeaderViewIndex < 0 && mFooterViewIndex < 0) {
            return i;
        }
        if (i == childCount - 2) {
            return mHeaderViewIndex;
        }
        if (i == childCount - 1) {
            return mFooterViewIndex;
        }
        int bigIndex = mFooterViewIndex > mHeaderViewIndex ? mFooterViewIndex
                : mHeaderViewIndex;
        int smallIndex = mFooterViewIndex < mHeaderViewIndex ? mFooterViewIndex
                : mHeaderViewIndex;
        if (i >= smallIndex && i < bigIndex - 1) {
            return i + 1;
        }
        if (i >= bigIndex || (i == bigIndex - 1)) {
            return i + 2;
        }
        return i;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.mRefreshEnabled = refreshEnabled;
        if (mRefreshEnabled) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            textView.setMinHeight((int) mTotalDragDistance);
            addHeaderView(textView);
            setHeaderBackgroundColor(getResources().getColor(R.color.background_light));
            setOnRefreshAnimatorListener(onRefreshAnimatorListener);
        } else {
            mHeaderContainer.removeAllViews();
            setOnRefreshAnimatorListener(null);
            mHeaderView = null;
        }
    }

    public void setLoadEnabled(boolean loadEnabled) {
        this.mLoadEnabled = loadEnabled;
        if (mLoadEnabled) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            textView.setMinHeight((int) mTotalDragDistance);
            addFooterView(textView);
            setFooterBackgroundColor(getResources().getColor(android.R.color.background_light));
            setOnLoadAnimatorListener(onLoadAnimatorListener);
        } else {
            mFooterContainer.removeAllViews();
            setOnLoadAnimatorListener(null);
            mFooterView = null;
        }
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
        mHeaderContainer.measure(MeasureSpec.makeMeasureSpec(mHeaderViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mHeaderViewHeight, MeasureSpec.EXACTLY));
        mFooterContainer.measure(MeasureSpec.makeMeasureSpec(mFooterViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mFooterViewHeight, MeasureSpec.EXACTLY));

        mHeaderViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mHeaderContainer) {
                mHeaderViewIndex = index;
                break;
            }
        }
        mFooterViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mFooterContainer) {
                mFooterViewIndex = index;
                break;
            }
        }
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

        int headViewWidth = mHeaderContainer.getMeasuredWidth();
        int headViewHeight = mHeaderContainer.getMeasuredHeight();
        mHeaderContainer.layout((width / 2 - headViewWidth / 2), -headViewHeight,
                (width / 2 + headViewWidth / 2), 0);

        int footerViewWidth = mFooterContainer.getMeasuredWidth();
        int footerViewHeight = mFooterContainer.getMeasuredHeight();
        mFooterContainer.layout((width / 2 - footerViewWidth / 2), height,
                (width / 2 + footerViewWidth / 2), height + footerViewHeight);
    }

    private View ensureTarget() {
        if (mTarget != null) {
            return mTarget;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!child.equals(mHeaderContainer) && !child.equals(mFooterContainer)) {
                return child;
            }
        }
        return null;
    }

    private void ensureScrollAnimator() {
        if (mScrollAnimator == null) {
            mScrollAnimator = new ValueAnimator();
            mScrollAnimator.setInterpolator(mInterpolator);
            mScrollAnimator.setDuration(mMediumAnimationDuration);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    float offset = (float) animator.getAnimatedValue();
                    setTargetOffsetTopAndBottom((int) (offset - mCurrentTargetOffsetTop));
                    if (mRefreshing || mLoading) {
                        return;
                    }
                    final float progress = offset / mFrom;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnRefreshAnimatorListener != null) {
                                mOnRefreshAnimatorListener.onAnimatorFinish(mHeaderView, progress);
                            }
                            if (mOnLoadAnimatorListener != null) {
                                mOnLoadAnimatorListener.onAnimatorFinish(mFooterView, progress);
                            }
                        }
                    });
                }
            });
        }
        mScrollAnimator.removeAllListeners();
    }

    //判断是否可以下拉
    public boolean canChildScrollUp() {
        if (isAnimationRunning(mScrollAnimator)) {
            return true;
        }

        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }

        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    //判断是否可以上拉
    public boolean canChildScrollDown() {
        if (isAnimationRunning(mScrollAnimator)) {
            return true;
        }

        if (mOnChildScrollDownCallback != null) {
            return mOnChildScrollDownCallback.canChildScrollDown(this, mTarget);
        }

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
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mRefreshing && !mLoading
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }

        if (dy < 0 && mTotalUnconsumed < 0) {
            if (dy < mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }

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
        if (mTotalUnconsumed > 0 || mTotalUnconsumed < 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];

        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }

        if (dy > 0 && !canChildScrollDown()) {
            mTotalUnconsumed -= dy;
            moveSpinner(-mTotalUnconsumed);
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

    private boolean isAnimationRunning(ValueAnimator animation) {
        return animation != null && animation.isRunning();
    }

    private void moveSpinner(float overScrollTop) {
        float slingshotDist = mSpinnerOffsetEnd;
        float originalDragPercent = overScrollTop / (slingshotDist * 2);
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float extraOS = Math.abs(overScrollTop) - slingshotDist;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 1)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = (int) ((slingshotDist * dragPercent) + extraMove);

        final float progress = mTotalDragDistance != 0 ? Math.min(1f, Math.abs(overScrollTop / 2 / mTotalDragDistance)) : 0;

        if (!canChildScrollDown()) {
            if (mLoadEnabled && mFooterContainer.getVisibility() != View.VISIBLE) {
                mFooterContainer.setVisibility(View.VISIBLE);
            }
            setTargetOffsetTopAndBottom(-mCurrentTargetOffsetTop - targetY);
            if (progress != maxProgress && mLoadEnabled && mOnLoadAnimatorListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnLoadAnimatorListener.onAnimatorStart(mFooterView, progress);
                        maxProgress = progress;
                    }
                });

            }
        } else {
            if (mRefreshEnabled && mHeaderContainer.getVisibility() != View.VISIBLE) {
                mHeaderContainer.setVisibility(View.VISIBLE);
            }
            setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
            if (progress != maxProgress && mRefreshEnabled && mOnRefreshAnimatorListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnRefreshAnimatorListener.onAnimatorStart(mHeaderView, progress);
                        maxProgress = progress;
                    }
                });
            }
        }
    }

    private void finishSpinner(int offset) {
        ensureScrollAnimator();
        mFrom = mCurrentTargetOffsetTop;
        if (mRefreshEnabled && offset / 2 > mTotalDragDistance) {
            mRefreshing = true;
            mScrollAnimator.setFloatValues(mCurrentTargetOffsetTop, mTotalDragDistance);
            mScrollAnimator.addListener(mRefreshListener);
            mScrollAnimator.start();
        } else if (mLoadEnabled && offset / 2 < -mTotalDragDistance) {
            mLoading = true;
            mScrollAnimator.setFloatValues(mCurrentTargetOffsetTop, -mTotalDragDistance);
            mScrollAnimator.addListener(mLoadListener);
            mScrollAnimator.start();
        } else {
            reset();
        }
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        if (mTarget != null) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);//正数向下移动，负数向上移动
            ViewCompat.offsetTopAndBottom(mFooterContainer, offset);
            ViewCompat.offsetTopAndBottom(mHeaderContainer, offset);
            mCurrentTargetOffsetTop = mTarget.getTop();
        }
    }

    private void reset() {
        mRefreshing = false;
        mLoading = false;
        // 移动到初始位置
        mFrom = mCurrentTargetOffsetTop;
        ensureScrollAnimator();
        mScrollAnimator.setFloatValues(mCurrentTargetOffsetTop, 0);
        mScrollAnimator.addListener(mFinishListener);
        mScrollAnimator.start();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public interface OnRefreshAnimatorListener {
        void onAnimatorStart(View headerView, float offset);

        void onRefreshStart(View headerView);

        void onRefreshFinish(View headerView);

        void onAnimatorFinish(View headerView, float offset);
    }

    public interface OnLoadAnimatorListener {
        void onAnimatorStart(View footerView, float offset);

        void onLoadStart(View footerView);

        void onLoadFinish(View footerView);

        void onAnimatorFinish(View footerView, float offset);
    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(@NonNull RefreshLayout parent, @Nullable View child);
    }

    public interface OnChildScrollDownCallback {
        boolean canChildScrollDown(@NonNull RefreshLayout parent, @Nullable View child);
    }


    private OnRefreshAnimatorListener onRefreshAnimatorListener = new OnRefreshAnimatorListener() {
        @Override
        public void onAnimatorStart(View headerView, float offset) {
            TextView view = (TextView) headerView;
            if (offset >= 1f) {
                view.setText(R.string.refresh_up);
                return;
            }
            view.setText(R.string.refresh_down);
        }

        @Override
        public void onRefreshStart(View headerView) {
            TextView view = (TextView) headerView;
            view.setText(R.string.refreshing);
        }

        @Override
        public void onRefreshFinish(View headerView) {
            TextView view = (TextView) headerView;
            view.setText(R.string.refresh_finish);
        }

        @Override
        public void onAnimatorFinish(View headerView, float offset) {

        }
    };

    private OnLoadAnimatorListener onLoadAnimatorListener = new OnLoadAnimatorListener() {
        @Override
        public void onAnimatorStart(View headerView, float offset) {
            TextView view = (TextView) headerView;
            if (offset >= 1f) {
                view.setText(R.string.load_down);
                return;
            }
            view.setText(R.string.load_up);
        }

        @Override
        public void onLoadStart(View headerView) {
            TextView view = (TextView) headerView;
            view.setText(R.string.loading);
        }

        @Override
        public void onLoadFinish(View headerView) {
            TextView view = (TextView) headerView;
            view.setText(R.string.load_finish);
        }

        @Override
        public void onAnimatorFinish(View headerView, float offset) {

        }
    };


}
