package com.one.widget;

import android.content.Context;
import android.util.AttributeSet;
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

  private static final int RATIO = 3;

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
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        if (checkScrollView()) {
          mLastDownX = mActionDownX = (int) ev.getX();
          mLastDownY = mActionDownY = (int) ev.getY();
        }
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        int curX = (int) ev.getX();
        int curY = (int) ev.getY();
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
