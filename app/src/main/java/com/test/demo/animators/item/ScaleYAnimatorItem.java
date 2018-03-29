package com.test.demo.animators.item;

import android.view.View;
import com.test.demo.animators.ViewAnimator;

public class ScaleYAnimatorItem extends ViewAnimator.BaseAnimItem {
    private float mFrom;
    private float mTo;

    public ScaleYAnimatorItem(float from, float to) {
        mFrom = from;
        mTo = to;
    }

    @Override
    public void onViewAttached(View view) {
        view.setScaleY(mFrom);
    }

    @Override
    public void onUpdate(View view, float fraction) {
        float value = mFrom + (mTo - mFrom) * fraction;
        view.setScaleY(value);
    }
}
