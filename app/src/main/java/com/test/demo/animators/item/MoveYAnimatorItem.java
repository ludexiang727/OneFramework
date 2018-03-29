package com.test.demo.animators.item;

import android.view.View;
import com.test.demo.animators.ViewAnimator;

public class MoveYAnimatorItem extends ViewAnimator.BaseAnimItem {

    private float mMoveHeight = 0;
    private float mStartTranslate = 0;

    public MoveYAnimatorItem(float move) {
        mMoveHeight = move;
    }

    @Override
    public void onViewAttached(View view) {
        mStartTranslate = view.getTranslationY();
    }

    @Override
    public void onUpdate(View view, float fraction) {
        view.setTranslationY(mStartTranslate + mMoveHeight * fraction);
    }
}
