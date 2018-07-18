package com.trip.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.trip.base.R;

public class BaseLinearLayout extends LinearLayout {

  ForegroundAttacher foregroundAttacher;

  public BaseLinearLayout(@NonNull Context context) {
    this(context, null);
  }

  public BaseLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    foregroundAttacher = new ForegroundAttacher(this);
    foregroundAttacher.initFromAttrsAndDefStyle(context, attrs, defStyleAttr);

    setWillNotDraw(false);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View);
    Drawable drawable = a.getDrawable(R.styleable.View_outsideBackground);
    if (drawable != null) {
      setOutsideBackground((NinePatchDrawable) drawable);
    }
    a.recycle();
  }

  private NinePatchDrawable outsideBackground;
  private Rect outsideBackgroundPadding = new Rect();

  public void setOutsideBackground(NinePatchDrawable d) {
    outsideBackground = d;
    if (outsideBackground != null) {
      outsideBackground.getPadding(outsideBackgroundPadding);
    }
    setBackgroundDrawable(DrawOutside.solidTransparentHack);
    invalidate();
  }

  public NinePatchDrawable getOutsideBackground() {
    return outsideBackground;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void draw(Canvas canvas) {
    if (outsideBackground != null) {
      DrawOutside.drawOutside(this, canvas, outsideBackground, outsideBackgroundPadding, 255);
    }
    super.draw(canvas);
    foregroundAttacher.callOnDraw(canvas);
  }


  @Override
  public void setForeground(Drawable drawable) {
    foregroundAttacher.setForeground(drawable);
  }

  @Override
  public Drawable getForeground() {
    return foregroundAttacher.getForeground();
  }

  @Override
  protected boolean verifyDrawable(Drawable dr) {
    return super.verifyDrawable(dr) || foregroundAttacher.callOnVerifyDrawable(dr);
  }

  @Override
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    foregroundAttacher.callOnJumpDrawablesToCurrentState();

  }


  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    foregroundAttacher.callOnDrawableStateChanged();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    foregroundAttacher.callOnSizeChanged();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    foregroundAttacher.callOnTouchEvent(event);
    return super.onTouchEvent(event);
  }
}
