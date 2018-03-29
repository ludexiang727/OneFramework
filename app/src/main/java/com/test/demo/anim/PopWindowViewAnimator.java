package com.test.demo.anim;

import android.view.View;
import android.view.View.MeasureSpec;
import com.test.demo.animators.ViewAnimator;
import com.test.demo.animators.item.FadeInAnimatorItem;
import com.test.demo.animators.item.MoveYAnimatorItem;
import java.util.Set;

/**
 * Created by ludexiang on 2018/1/16.
 */

public class PopWindowViewAnimator extends ViewAnimator {

  private int mHeight;
  private boolean isEnter;

  public PopWindowViewAnimator(boolean enter) {
    isEnter = enter;
  }

  @Override
  protected void onViewAttached(View... views) {
    View view = views[0];
    int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    view.measure(width, height);
    mHeight = view.getMeasuredHeight();
    view.setTranslationY(isEnter ? mHeight : 0);
  }

  @Override
  protected void newAnimators(int viewIndex, Set<AnimatorItem> container) {
    if (isEnter) {
      container.add(new MoveYAnimatorItem(-mHeight));
    } else {
      container.add(new MoveYAnimatorItem(mHeight));
    }
  }
}
