package com.trip.taxi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.trip.taxi.R;

/**
 * Foreground ripple.
 * Created by lijiang on 13/07/2017.
 */

public class ForegroundAttacher {

  private View view;
  // UI
  private Drawable foreground;
  // Controller/logic fields
  private boolean foregroundBoundsChanged = false;

  // Constructors
  public ForegroundAttacher(View view) {
    this.view = view;
  }

  interface Interface {

    void setForeground(Drawable drawable);

    Drawable getForeground();
  }

  public void initFromAttrsAndDefStyle(Context context, AttributeSet attrs, int defStyle) {
    final TypedArray a = context
        .obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);
    final Drawable d = a.getDrawable(R.styleable.View_foreground);
    setForeground(d);
    a.recycle();
  }

  /**
   * Supply a Drawable that is to be rendered on top of all of the child views in the layout.
   *
   * @param drawable The Drawable to be drawn on top of the children.
   */
  public void setForeground(Drawable drawable) {
    if (foreground != drawable) {
      if (foreground != null) {
        foreground.setCallback(null);
        view.unscheduleDrawable(foreground);
      }
      foreground = drawable;
      if (drawable != null) {
        view.setWillNotDraw(false);
        drawable.setCallback(view);
        if (drawable.isStateful()) {
          drawable.setState(view.getDrawableState());
        }
      } else {
        view.setWillNotDraw(true);
      }
      view.requestLayout();
      view.invalidate();
    }
  }

  /**
   * Returns the drawable used as the foreground of this layout. The foreground drawable,
   * if non-null, is always drawn on top of the children.
   *
   * @return A Drawable or null if no foreground was set.
   */
  public Drawable getForeground() {
    return foreground;
  }

  public void callOnDrawableStateChanged() {
    if (foreground != null && foreground.isStateful()) {
      foreground.setState(view.getDrawableState());
    }
  }

  public boolean callOnVerifyDrawable(Drawable who) {
    return who == foreground;
  }

  public void callOnJumpDrawablesToCurrentState() {
    if (foreground != null) {
      foreground.jumpToCurrentState();
    }
  }

  public void callOnSizeChanged() {
    foregroundBoundsChanged = true;
  }

  public void callOnDraw(Canvas canvas) {
    if (foreground != null) {
      final Drawable foreground = this.foreground;
      if (foregroundBoundsChanged) {
        foregroundBoundsChanged = false;
        final int w = view.getRight() - view.getLeft();
        final int h = view.getBottom() - view.getTop();
        foreground.setBounds(0, 0, w, h);
      }
      foreground.draw(canvas);
    }
  }

  public void callOnTouchEvent(MotionEvent e) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
        if (foreground != null) {
          foreground.setHotspot(e.getX(), e.getY());
        }
      }
    }
  }

}
