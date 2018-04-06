package com.one.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import com.one.listener.IPullView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class PullScrollView extends ScrollView implements IPullView {

  public PullScrollView(Context context) {
    this(context, null);
  }

  public PullScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public int getScrollingY() {
    return getScrollY();
  }

  @Override
  public View getView() {
    return this;
  }
}