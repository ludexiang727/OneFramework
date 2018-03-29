package com.evaluate.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import com.evaluate.R;

/**
 * 滑动星星控件
 */
public class StarView extends LinearLayout {

  private int mLevel;
  private int mPendingLevel;

  private Context mContext;

  private static final int STAR_NUM_DEFAULT = 5;
  private static final int sDarkStarDrawableId = R.drawable.evaluation_icon_star_gray;
  private static final int sBrightStarDrawableId = R.drawable.evaluation_icon_star;
  private boolean mStarChangeEnable = true;
  private OnTouchStarChangeListener mOnTouchStarChangeListener;

  public StarView(Context context) {
    super(context);
    this.mContext = context;
    initView();
  }

  public StarView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.mContext = context;
    initView();
  }

  private void initView() {
    setOrientation(LinearLayout.HORIZONTAL);
    setGravity(Gravity.CENTER_HORIZONTAL);
    createStars(STAR_NUM_DEFAULT);
  }

  private void createStars(int starNum) {
    removeAllViews();
    for (int i = 0; i < starNum; i++) {
      View oneStarLayout = LayoutInflater.from(mContext)
          .inflate(R.layout.oc_one_star_layout, this, false);
      ImageViewAccessibleForCheck starIv = (ImageViewAccessibleForCheck) oneStarLayout
          .findViewById(R.id.evaluate_star_one);
      int level = i + 1;
//      starIv.setContentDescription(mContext.getResources().getString(R.string
//          .oc_evaluate_voice_start, level + ""));
      starIv.setFocusable(true);
      starIv.setImageResource(sDarkStarDrawableId);
      addView(oneStarLayout);
    }
  }

  /**
   * 获取当前星级
   */
  public int getLevel() {
    return mLevel;
  }

  /**
   * 设置能否进行评星级
   */
  public void setTouchEnable(boolean flag) {
    if (flag) {
      setOnTouchListener(changeListener);
    } else {
      setOnTouchListener(null);
    }
  }

  public void setOnTouchStarChangeListener(OnTouchStarChangeListener listener) {
    mOnTouchStarChangeListener = listener;
  }

  private OnTouchListener changeListener = new OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          if (mStarChangeEnable) {
            onStarTouch(event);
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (mStarChangeEnable) {
            onStarTouch(event);
          }
          break;
        case MotionEvent.ACTION_UP:
          if (mPendingLevel != mLevel) {
            mLevel = mPendingLevel;
          }
          if (mOnTouchStarChangeListener != null) {
            mOnTouchStarChangeListener.onTouchStarChange(mLevel);
          }
          sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
          break;
      }
      return true;
    }
  };

  private void onStarTouch(MotionEvent event) {
    int newLevel = calculateLevel(event.getX(), mLevel);
    if (newLevel != 0 && newLevel != mLevel) {
      setLevel(getChildCount(), newLevel);
      mPendingLevel = newLevel;
    }
  }

  private int calculateLevel(float x, int oldLevel) {
    int childCount = getChildCount();
    int newLevel = getLevelByPosition(x, childCount);
    if (newLevel == oldLevel || newLevel <= 0) {
      return oldLevel;
    }
    return newLevel;
  }

  private void setLevel(int childCount, int level) {
    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);
      ImageViewAccessibleForCheck starIv = (ImageViewAccessibleForCheck) child
          .findViewById(R.id.evaluate_star_one);
      if (i < level) {
        starIv.setImageResource(sBrightStarDrawableId);
        starIv.setCheck(true);
      } else {
        starIv.setImageResource(sDarkStarDrawableId);
        starIv.setCheck(false);
      }
    }
  }

  /**
   * 设置星级
   */
  public void setLevel(int level) {
    setLevel(getChildCount(), level);
    mLevel = level;
  }

  private int getLevelByPosition(float x, int childCount) {
    int level = 0;
    for (int i = 0; i < childCount - 1; i++) {
      View child = getChildAt(i);
      View nextChild = getChildAt(i + 1);
      if (x >= child.getLeft() && x < nextChild.getLeft()) {
        level = i + 1;
      }
    }
    //最后一个星星
    View lastView = getChildAt(childCount - 1);
    if (x >= lastView.getLeft()) {
      level = childCount;
    }
    return level;
  }

  public interface OnTouchStarChangeListener {

    void onTouchStarChange(int level);
  }

}
