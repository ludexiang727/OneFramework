package com.one.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ludexiang on 2018/4/20.
 */

public class ContainerRelativeLayout extends RelativeLayout {

  public ContainerRelativeLayout(Context context) {
    this(context, null);
  }

  public ContainerRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ContainerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    setLayoutParams(params);
  }
}
