package com.trip.taxi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.trip.taxi.R;

public class BaseRelativeLayout extends RelativeLayout {
  
  private ForegroundAttacher foregroundAttacher;
  private NinePatchDrawable outsideBackground;
  private Rect outsideBackgroundPadding = new Rect();
  
  public BaseRelativeLayout(Context context) {
    this(context, null);
  }
  
  public BaseRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
  
  
  @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
  public void setOutsideBackground(NinePatchDrawable d) {
    outsideBackground = d;
    if (outsideBackground != null) outsideBackground.getPadding(outsideBackgroundPadding);
    setBackground(DrawOutside.solidTransparentHack);
    invalidate();
  }
  
  public NinePatchDrawable getOutsideBackground() {
    return outsideBackground;
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
  protected boolean verifyDrawable(@NonNull Drawable dr) {
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
  
  public boolean interceptTouchEvent = false;
  
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean b = super.onInterceptTouchEvent(ev);
    return b || interceptTouchEvent;
  }
  
}
