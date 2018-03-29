package com.evaluate.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @since 2016/10/11
 */
public class EvaluateRootContainer extends RelativeLayout {
    private boolean mInterceptTouch;
    private OnTouchWhenInterceptListener mOnTouchWhenInterceptListener;
    private int touchViewID;

    public EvaluateRootContainer(Context context) {
        super(context);
    }

    public EvaluateRootContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EvaluateRootContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EvaluateRootContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnTouchWhenInterceptListener(OnTouchWhenInterceptListener listener) {
        mOnTouchWhenInterceptListener = listener;
    }

    public void setCanTouchView(int viewID) {
        this.touchViewID = viewID;
    }

    public void setInterceptTouchEnable(boolean enable) {
        mInterceptTouch = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mInterceptTouch) {
            if (mOnTouchWhenInterceptListener != null) {
                mOnTouchWhenInterceptListener.onTouchWhenIntercept();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptTouch) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnTouchWhenInterceptListener {
        void onTouchWhenIntercept();
    }
}
