package com.one.framework.app.widget.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by ludexiang on 2018/3/28.
 */

public abstract class AbsTabIndicatorScrollerView extends HorizontalScrollView {

  public AbsTabIndicatorScrollerView(Context context) {
    this(context, null);
  }

  public AbsTabIndicatorScrollerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AbsTabIndicatorScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
