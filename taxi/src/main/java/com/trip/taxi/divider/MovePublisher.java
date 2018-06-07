package com.trip.taxi.divider;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class MovePublisher implements IMovePublisher {

    private VelocityTracker mTracker;
    private OnMoveListener mListener;
    private int mMinFlingVelocity;
    private int mTouchSlop;

    private float mFirstMotionX;
    private float mFirstMotionY;

    public MovePublisher(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mListener == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                onTouchDown(event);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                onTouchMove(event);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP: {
                onTouchEnd(event);
                break;
            }
        }

        return true;
    }

    private void onTouchDown(MotionEvent event) {
        /** 创建VelocityTracker*/
        mTracker = VelocityTracker.obtain();
        mTracker.addMovement(event);
        /** 记录初始点击信息*/
        mFirstMotionX = event.getX();
        mFirstMotionY = event.getY();
    }

    private void onTouchMove(MotionEvent event) {
        float moveX = event.getX() - mFirstMotionY;
        float moveY = event.getY() - mFirstMotionY;
        mListener.onMove(moveX, moveY);
        mTracker.addMovement(event);
    }

    private void onTouchEnd(MotionEvent event) {
        float moveX = event.getX() - mFirstMotionX;
        float moveY = event.getY() - mFirstMotionY;
        mListener.onMove(moveX, moveY);

        mTracker.addMovement(event);
        mTracker.computeCurrentVelocity(1000);

        float velocityY = mTracker.getYVelocity();
        boolean fling = Math.abs(velocityY) >= mMinFlingVelocity;
        boolean moveEnough = Math.abs(moveY) > mTouchSlop;
        boolean bottomToUp = velocityY < 0;
        mListener.onEnd(fling && moveEnough, bottomToUp);

        mTracker.recycle();
        mTracker = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void setOnMoveListener(OnMoveListener l) {
        mListener = l;
    }
}
