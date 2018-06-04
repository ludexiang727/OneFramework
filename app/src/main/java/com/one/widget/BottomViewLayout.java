package com.one.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by ludexiang on 2018/6/2.
 */

public class BottomViewLayout extends LinearLayout {

  public BottomViewLayout(Context context) {
    this(context, null);
  }

  public BottomViewLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BottomViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
