package com.evaluate.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by baozhiheng on 16/12/8.
 */
public class ImageViewAccessibleForCheck extends AppCompatImageView {

    private boolean isCheck = false;

    public ImageViewAccessibleForCheck(Context context) {
        super(context);
    }

    public ImageViewAccessibleForCheck(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewAccessibleForCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCheck(boolean isCheck){
       this.isCheck = isCheck;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        this.isCheck = selected;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        event.setChecked(isCheck);

    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setChecked(isCheck);
        info.setCheckable(true);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(isCheck);
    }
}
