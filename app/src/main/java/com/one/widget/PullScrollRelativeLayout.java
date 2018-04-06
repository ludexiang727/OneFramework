package com.one.widget;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import com.one.listener.IMovePublishListener;
import com.one.listener.IPullView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class PullScrollRelativeLayout extends RelativeLayout {

  private static final int RATIO = 5;

  private IMovePublishListener mMoveListener;
  private VelocityTracker mTracker;
  private int mMinScroll;
  private int mMinVelocity;
  private int mMaxVelocity;
  private int mLastDownX, mLastDownY;
  private int mActionDownX, mActionDownY;
  private View mScrollView;
  private IPullView mPullView;
  private boolean isScrolling = false;
  private int mActivePointerId = INVALID_POINTER_ID;

  public PullScrollRelativeLayout(Context context) {
    this(context, null);
  }

  public PullScrollRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullScrollRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    ViewConfiguration configuration = ViewConfiguration.get(context);
    mMinScroll = configuration.getScaledTouchSlop();
    mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
  }

  public void setMoveListener(IMovePublishListener listener) {
    mMoveListener = listener;
  }

  public void setScrollView(IPullView scrollView) {
    mPullView = scrollView;
    mScrollView = mPullView.getView();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    addVelocityTracker(ev);
    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
      // 处理两个手交替 begin
      case MotionEvent.ACTION_POINTER_DOWN: {
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        mActionDownX = (int) MotionEventCompat.getX(ev, pointerIndex);
        mActionDownY = (int) MotionEventCompat.getY(ev, pointerIndex);
        break;
      }
      case MotionEvent.ACTION_POINTER_UP: {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

        if (pointerId == mActivePointerId) {
          // This was our active pointer going up. Choose a new
          // active pointer and adjust accordingly.
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          mActionDownX = (int) MotionEventCompat.getX(ev, newPointerIndex);
          mActionDownY = (int) MotionEventCompat.getY(ev, newPointerIndex);
          mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
        break;
      }
      // 处理两个手交替 end
      case MotionEvent.ACTION_DOWN: {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final float x = MotionEventCompat.getX(ev, pointerIndex);
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        if (checkScrollView()) {
          mLastDownX = mActionDownX = (int) x;
          mLastDownY = mActionDownY = (int) y;
          // Save the ID of this pointer (for dragging)
          mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        }
        break;
      }
      case MotionEvent.ACTION_MOVE: {
//        int curX = (int) ev.getX();
//        int curY = (int) ev.getY();
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        final int curX = (int) MotionEventCompat.getX(ev, pointerIndex);
        final int curY = (int) MotionEventCompat.getY(ev, pointerIndex);
        isScrolling = curY - mLastDownY >= mMinScroll || mScrollView.getTranslationY() > 0;
        if (checkScrollView()) {
          if (canScroll()) {
            int offsetX = (curX - mActionDownX) / RATIO;
            int offsetY = (curY - mActionDownY) / RATIO;
            handleMove(offsetX, offsetY);
            mActionDownX = curX;
            mActionDownY = curY;
            return true;
          }
        }
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        int scrollY = (int) mScrollView.getTranslationY();
        mTracker.computeCurrentVelocity(1000, mMaxVelocity);
        float yVelocity = mTracker.getYVelocity();
        boolean isFling = Math.abs(yVelocity) > mMinVelocity ? true : false;
        boolean bottom2Up = scrollY > 0 ? true : false;
        handleUp(bottom2Up, isFling);
        recycleVelocityTracker();
        break;
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  private void handleMove(int offsetX, int offsetY) {
    if (mMoveListener != null) {
      mMoveListener.onMove(offsetX, offsetY);
    }
  }

  private void handleUp(boolean bottom2Up, boolean isFling) {
    if (mMoveListener != null) {
      mMoveListener.onUp(bottom2Up, isFling);
    }
  }

  private boolean checkScrollView() {
    return mScrollView != null;
  }

  /**
   * 判断是否可滚动
   */
  private boolean canScroll() {
    return isScrolling && mPullView.getScrollingY() == 0;
  }

  private void addVelocityTracker(MotionEvent event) {
    if (mTracker == null) {
      mTracker = VelocityTracker.obtain();
    }
    mTracker.addMovement(event);
  }

  private void recycleVelocityTracker() {
    if (mTracker != null) {
      mTracker.recycle();
      mTracker = null;
    }
  }
}
