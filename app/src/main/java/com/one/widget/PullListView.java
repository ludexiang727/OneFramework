package com.one.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.one.listener.IHeaderView;
import com.one.listener.IMovePublishListener;
import com.one.listener.IPullView;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/4/3.
 */

@TargetApi(VERSION_CODES.M)
public class PullListView extends ListView implements IMovePublishListener, IPullView, OnScrollListener {

  private SparseArray<ItemRecord> recordSp = new SparseArray(0);
  private int mCurrentFirstVisibleItem = 0;

  private IHeaderView mHeaderView;
  private int mScroller; // 0 scroll Header 1 scroll self
  private int mMaxHeight;

  public PullListView(Context context) {
    this(context, null);
  }

  public PullListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullView);
    mScroller = a.getInt(R.styleable.PullView_scroll_view, 1);
    mMaxHeight = a.getDimensionPixelSize(R.styleable.PullView_scroll_max_height, 0);
    if (mScroller == 0 && mMaxHeight == 0) {
      throw new IllegalArgumentException("ScrollMaxHeight is 0");
    }
    a.recycle();

    mHeaderView = new HeaderView(context, mMaxHeight);
    addHeaderView(mHeaderView.getView());
    setOnScrollListener(this);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    mCurrentFirstVisibleItem = firstVisibleItem;
    View firstView = view.getChildAt(0);
    if (null != firstView) {
      ItemRecord itemRecord = recordSp.get(firstVisibleItem);
      if (null == itemRecord) {
        itemRecord = new ItemRecord();
      }
      //item高度
      itemRecord.height = firstView.getHeight();
      //滑动位置距顶部距离(负值)
      itemRecord.top = firstView.getTop();
      recordSp.append(firstVisibleItem, itemRecord);
    }

  }

  class ItemRecord {
    int height = 0;
    int top = 0;
  }

  /**
   * 获取滑动的距离
   */
  @Override
  public int getScrollingY() {
    int height = 0;
    for (int i = 0; i < mCurrentFirstVisibleItem; i++) {
      ItemRecord itemRecord = recordSp.get(i);
      height += itemRecord.height;
    }
    ItemRecord itemRecord = recordSp.get(mCurrentFirstVisibleItem);
    if (null == itemRecord) {
      itemRecord = new ItemRecord();
    }
    return height - itemRecord.top;
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public void onMove(int offsetX, int offsetY) {
//    Log.e("ldx", "offsetX " + offsetX + " offsetY " + offsetY);
    if (mScroller == 0) {
      mHeaderView.onMove(offsetX, offsetY);
    } else {
      selfScrollerMove(offsetY);
    }
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    if (mScroller == 0) {
      mHeaderView.onUp(bottom2Up, isFling);
    } else {
      selfScrollerUp(bottom2Up, isFling);
    }
  }

  private void selfScrollerMove(int offsetY) {
    int translateY = (int) (getTranslationY() + offsetY + 0.5);
    setTranslationY(translateY);
  }

  private void selfScrollerUp(boolean bottom2Up, boolean isFling) {
    int tranlationY = (int) getTranslationY();
    goonMove(200);
  }

  private void goonMove(long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = animation.getAnimatedFraction();
        float fraction = 1f - animValue;
        setTranslationY(fraction * getTranslationY());
      }
    });
    translate.start();
  }
}