package com.test.demo.animators.item;

import android.view.View;
import android.view.ViewGroup;
import com.test.demo.animators.ViewAnimator;

/**
 * Created by ludexiang on 2018/1/11.
 */

public class WidthAnimatorItem extends ViewAnimator.BaseAnimItem {

  private int mFrom;
  private int mTo;

  public WidthAnimatorItem(int from, int to) {
    mFrom = from;
    mTo = to;
  }

  @Override
  public void onViewAttached(View view) {
    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
    params.width = mFrom;
    view.setLayoutParams(params);
  }

  @Override
  public void onUpdate(View view, float fraction) {
    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
    params.width = (int) (mFrom + mTo * fraction);
    view.setLayoutParams(params);
  }
}
