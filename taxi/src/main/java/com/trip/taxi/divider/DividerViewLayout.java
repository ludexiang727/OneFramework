package com.trip.taxi.divider;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.trip.taxi.R;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

public class DividerViewLayout extends LinearLayout {

    private static final boolean DRAW_BORDER = false;
    private Paint mLinePaint;

    private Map<View, View> mViewDivider = new LinkedHashMap<>();
    private Map<View, View> mDividerView = new LinkedHashMap<>();
    private Map<View, GlobalLayoutListenerWrapper> mViewLayoutListeners = new LinkedHashMap<>();

    private int mDividerMarginLeft;
    private int mDividerMarginRight;
    private int mDividerHeight;
    private int mDividerColor = Color.WHITE;
    private int mDividerBackgroundColor = Color.WHITE;
    private boolean mCutTop = true;

    private Drawable mAdjustableBackground = null;
    private int mBkgMargin;
    private boolean mTouchNoContent = false;

    private IMovePublisher mMovePublisher;
    private OnSizeChangeListener mListener;
    private OnHierarchyChangeListener mHierarchyListener;

    public DividerViewLayout(Context context) {
        this(context, null);
    }

    public DividerViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerViewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readConfigurationForAttributeSet(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        if (DRAW_BORDER) {
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setColor(mDividerColor);
            mLinePaint.setStrokeWidth(mDividerHeight);
        }
    }

    private void readConfigurationForAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DividerViewLayout);
        mDividerMarginLeft = array.getDimensionPixelOffset(R.styleable.DividerViewLayout_dividerLeftMargin, 0);
        mDividerMarginRight = array.getDimensionPixelOffset(R.styleable.DividerViewLayout_dividerRightMargin, 0);
        mDividerHeight = array.getDimensionPixelOffset(R.styleable.DividerViewLayout_dividerHeight, 0);
        mDividerColor = array.getColor(R.styleable.DividerViewLayout_dividerColor, Color.WHITE);
        mDividerBackgroundColor = array.getColor(R.styleable.DividerViewLayout_dividerBackgroundColor, Color.WHITE);
        mAdjustableBackground = array.getDrawable(R.styleable.DividerViewLayout_adjustableBackground);
        mCutTop = array.getBoolean(R.styleable.DividerViewLayout_cutTop, true);
        mBkgMargin = array.getDimensionPixelOffset(R.styleable.DividerViewLayout_backgroundMargin, 0);
        boolean movable = array.getBoolean(R.styleable.DividerViewLayout_movable, false);
        mMovePublisher = movable ? new MovePublisher(context) : null;
        array.recycle();
    }

    public void setOnMoveListener(IMovePublisher.OnMoveListener l) {
        if (mMovePublisher != null) {
            mMovePublisher.setOnMoveListener(l);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.setOnHierarchyChangeListener(mInnerHierarchyListener);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mHierarchyListener = listener;
    }

    private OnHierarchyChangeListener mInnerHierarchyListener = new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            OnHierarchyChangeListener outerListener = mHierarchyListener;
            if (outerListener != null) {
                outerListener.onChildViewAdded(parent, child);
            }
            DividerViewLayout.this.onChildViewAdded(child);
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            OnHierarchyChangeListener outerListener = mHierarchyListener;
            if (outerListener != null) {
                outerListener.onChildViewRemoved(parent, child);
            }
            DividerViewLayout.this.onChildViewRemoved(child);
        }
    };

    private void onChildViewAdded(View child) {
        /** 只有一个视图,不添加分割线*/
        int size = getChildCount();
        if (size == 1) {
            return;
        }
        /** 添加的是一个分割线,不做处理*/
        if (mViewDivider.containsValue(child)) {
            return;
        }

        View divider = new DividerView(getContext());
        divider.setBackgroundColor(mDividerBackgroundColor);
        LayoutParams dividerParams = new LayoutParams(MATCH_PARENT, mDividerHeight);
        dividerParams.leftMargin = mDividerMarginLeft;
        dividerParams.rightMargin = mDividerMarginRight;

        int index = indexOfChild(child);
        View attach = index != 0 ? child : getChildAt(1);
        mViewDivider.put(attach, divider);
        mDividerView.put(divider, attach);
        int addIndex = index != 0 ? index : index + 1;
        super.addView(divider, addIndex, dividerParams);

        GlobalLayoutListenerWrapper layoutListener = new GlobalLayoutListenerWrapper(divider);
        mViewLayoutListeners.put(attach, layoutListener);

        /** 为分割线加入监听,当分割线对应的view发生高度和可见性变化时,刷新
         * 这是为了修复一个bug*/
        attach.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    /**
     * 修复没有注销监听导致的内存泄漏
     */
    private static class GlobalLayoutListenerWrapper implements ViewTreeObserver.OnGlobalLayoutListener {
        public WeakReference<View> mViewRef;

        public GlobalLayoutListenerWrapper(View tarView) {
            mViewRef = new WeakReference<>(tarView);
        }

        @Override
        public void onGlobalLayout() {
            if (mViewRef != null && mViewRef.get() != null)
                mViewRef.get().invalidate();
        }
    }

    private void onChildViewRemoved(View child) {
        View divider = mViewDivider.remove(child);
        if (divider != null) {
            mDividerView.remove(divider);
            super.removeView(divider);
        }

        GlobalLayoutListenerWrapper layoutListenerWrapper = mViewLayoutListeners.get(child);
        if (layoutListenerWrapper != null) {
            child.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListenerWrapper);
        }
    }

    public View getDivider(View child) {
        return mViewDivider.get(child);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /** 如果是Down事件,判断触摸事件是不是发生在视图可见区域内部*/
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            boolean xInner = ev.getX() >= 0 && ev.getX() < getWidth();
            boolean yInner = ev.getY() >= 0 && ev.getY() < getHeight() - getAdjustHeight();
            mTouchNoContent = xInner && yInner;
        }
        /** 如果触摸区域不是在视图内部,不做后续处理*/
        if (mTouchNoContent) {
            return false;
        }

        if (mMovePublisher == null) {
            return super.dispatchTouchEvent(ev);
        }
        mMovePublisher.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMovePublisher == null) {
            return true;
        } else {
            return mMovePublisher.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mMovePublisher == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return mMovePublisher.onInterceptTouchEvent(ev);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawAdjustBackground(canvas);
        super.dispatchDraw(canvas);
        drawBorders(canvas);
    }

    private void drawAdjustBackground(Canvas canvas) {
        if (mAdjustableBackground == null) {
            return;
        }
        /** 计算真正有内容的高度*/
        int backgroundHeight = getAdjustHeight();
        int saveCount = canvas.save();
        if (mCutTop) {
            canvas.translate(mBkgMargin, getHeight() - backgroundHeight + mBkgMargin);
        } else {
            canvas.translate(mBkgMargin, mBkgMargin);
        }
        int bkgWidth = getWidth() - 2 * mBkgMargin;
        int bkgHeight = backgroundHeight - 2 * mBkgMargin;
        mAdjustableBackground.setBounds(0, 0, bkgWidth, bkgHeight);
        mAdjustableBackground.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /**
     * 获取到一个视图真实内容的高度
     *
     * @return
     */
    public int getAdjustHeight() {
        int adjustHeight = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != View.GONE) {
                adjustHeight += (child.getHeight() * child.getScaleY());
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params != null) {
                    adjustHeight += params.topMargin;
                    adjustHeight += params.bottomMargin;
                }
            }
        }
        return adjustHeight;
    }

    private void drawBorders(Canvas canvas) {
        if (!DRAW_BORDER) {
            return;
        }

        int leftX = getPaddingLeft();
        int rightX = getWidth() - getPaddingRight();
        int topY = getPaddingTop();
        int bottomY = getHeight() - getPaddingBottom();
        /** 绘制左边的线条*/
        canvas.drawLine(leftX, topY, leftX, bottomY, mLinePaint);
        /** 绘制上方的线条*/
        canvas.drawLine(leftX, topY, rightX, topY, mLinePaint);
        /** 绘制右侧的线条*/
        canvas.drawLine(rightX, topY, rightX, bottomY, mLinePaint);
        /** 绘制底部的萧条*/
        canvas.drawLine(leftX, bottomY, rightX, bottomY, mLinePaint);
    }

    private class DividerView extends View {
        private DividerView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            View attach = mDividerView.get(this);
            /** divider对应的view不存在或者不可见,不绘制分割线*/
            if (attach == null || attach.getVisibility() != View.VISIBLE) {
                return;
            }
            /** divider对应的view宽或者高为0,不绘制分割线*/
            if (attach.getHeight() * getScaleY() <= 0 || attach.getWidth() * attach.getScaleX() <= 0) {
                return;
            }

            canvas.drawColor(mDividerColor);
        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener l) {
        mListener = l;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mListener != null) {
            mListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public interface OnSizeChangeListener {
        void onSizeChanged(int w, int h, int oldW, int oldH);
    }

    public void removeGlobalLayoutListener() {
//        if (CollectionUtil.isEmpty(mViewLayoutListeners)) {
//            return;
//        }

        for (Map.Entry<View, GlobalLayoutListenerWrapper> entry : mViewLayoutListeners.entrySet()) {
            View view = entry.getKey();
            GlobalLayoutListenerWrapper listener = entry.getValue();
            if (view != null && listener != null) {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
            }
        }
    }
}
