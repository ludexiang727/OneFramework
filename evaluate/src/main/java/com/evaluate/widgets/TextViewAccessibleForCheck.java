package com.evaluate.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class TextViewAccessibleForCheck extends android.support.v7.widget.AppCompatTextView {

    public TextViewAccessibleForCheck(Context context) {
        super(context);
    }

    public TextViewAccessibleForCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewAccessibleForCheck(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        event.setChecked(isSelected());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setChecked(isSelected());
        info.setCheckable(true);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(isSelected());
    }

}
