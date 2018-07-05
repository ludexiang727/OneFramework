package com.one.trip.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class CustomerSwipeRefreshLayout extends SwipeRefreshLayout {

  //实际需要滑动的child view
  private View mScrollUpChild;

  public CustomerSwipeRefreshLayout(Context context) {
    this(context, null);
  }

  public CustomerSwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setScrollView(View listView) {
    mScrollUpChild = listView;
  }


  @Override
  public boolean canChildScrollUp() {
    if (mScrollUpChild != null) {
      if (android.os.Build.VERSION.SDK_INT < 14) {
        if (mScrollUpChild instanceof AbsListView) {
          final AbsListView absListView = (AbsListView) mScrollUpChild;
          return absListView.getChildCount() > 0
              && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
              .getTop() < absListView.getPaddingTop());
        } else {
          return mScrollUpChild.canScrollVertically(-1) || mScrollUpChild.getScrollY() > 0;
        }
      } else {
        return mScrollUpChild.canScrollVertically(-1);
      }
    }
    return super.canChildScrollUp();
  }
}
