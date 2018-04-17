package com.one.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ludexiang on 2018/4/17.
 */

public class ContainerRelativeLayout extends RelativeLayout {

  private IHeightChangeListener mHeightListener;

  public ContainerRelativeLayout(Context context) {
    this(context, null);
  }

  public ContainerRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ContainerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setHeightListener(IHeightChangeListener listener) {
    mHeightListener = listener;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (mHeightListener != null) {
      mHeightListener.onHeightChange();
    }
  }

  public interface IHeightChangeListener {
    void onHeightChange();
  }
}
