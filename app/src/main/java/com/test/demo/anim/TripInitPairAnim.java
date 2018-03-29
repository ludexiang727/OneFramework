package com.test.demo.anim;

import android.util.Log;
import android.view.View;
import com.test.demo.animators.ViewAnimator;
import com.test.demo.animators.item.MoveYAnimatorItem;
import java.util.Set;

/**
 * Created by ludexiang on 2018/1/12.
 */

public class TripInitPairAnim extends ViewAnimator.ViewPairAnimator {

  private int mTopHeight;
  private int mBottomHeight;

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    if (container != null) {
      container.add(new MoveYAnimatorItem(mTopHeight));
    }
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    if (container != null) {
      container.add(new MoveYAnimatorItem(-mBottomHeight));
    }
  }

  @Override
  protected void onViewAttached(View... views) {
    super.onViewAttached(views);
    if (views.length <= 1) {
     return;
    }

    mTopHeight = views[0].getMeasuredHeight();
    mBottomHeight = views[1].getMeasuredHeight();

    Log.e("ldx", "top " + mTopHeight + " bottom " + mBottomHeight);
    views[0].setTranslationY(-mTopHeight);
    views[1].setTranslationY(mBottomHeight);
  }
}
