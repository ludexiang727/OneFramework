package com.evaluate.loading;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.evaluate.R;

public class CircularProgressBar extends ProgressBar {

  private static long SHOW_SUCCESS_DURATION = 1000;
  Drawable indeterminateDrawable;
  private boolean mSuccess;

  public CircularProgressBar(Context context) {
    this(context, null);
  }

  public CircularProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.cpbStyle);
  }

  public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    if (isInEditMode()) {
      setIndeterminateDrawable(new CircularProgressDrawable.Builder(context, true).build());
      return;
    }

    Resources res = context.getResources();
    TypedArray a = context
        .obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, defStyle, 0);

    final int color = a.getColor(R.styleable.CircularProgressBar_cpb_color,
        res.getColor(R.color.cpb_default_color));
    final float strokeWidth = a.getDimension(R.styleable.CircularProgressBar_cpb_stroke_width,
        res.getDimension(R.dimen.cpb_default_stroke_width));
    final float sweepSpeed = a.getFloat(R.styleable.CircularProgressBar_cpb_sweep_speed,
        Float.parseFloat(res.getString(R.string.cpb_default_sweep_speed)));
    final float rotationSpeed = a.getFloat(R.styleable.CircularProgressBar_cpb_rotation_speed,
        Float.parseFloat(res.getString(R.string.cpb_default_rotation_speed)));
    final int colorsId = a.getResourceId(R.styleable.CircularProgressBar_cpb_colors, 0);
    final int bgColor = a.getColor(R.styleable.CircularProgressBar_cpb_bgcolor, res.getColor(R
        .color.cpb_default_bgcolor));

    final int minSweepAngle = a.getInteger(R.styleable.CircularProgressBar_cpb_min_sweep_angle,
        res.getInteger(R.integer.cpb_default_min_sweep_angle));
    final int maxSweepAngle = a.getInteger(R.styleable.CircularProgressBar_cpb_max_sweep_angle,
        res.getInteger(R.integer.cpb_default_max_sweep_angle));
    a.recycle();

    int[] colors = null;
    //colors
    if (colorsId != 0) {
      colors = res.getIntArray(colorsId);
    }

    CircularProgressDrawable.Builder builder = new CircularProgressDrawable.Builder(context)
        .sweepSpeed(sweepSpeed)
        .rotationSpeed(rotationSpeed)
        .strokeWidth(strokeWidth)
        .minSweepAngle(minSweepAngle)
        .maxSweepAngle(maxSweepAngle)
        .backGroundColor(bgColor);

    if (colors != null && colors.length > 0) {
      builder.colors(colors);
    } else {
      builder.color(color);
    }

    indeterminateDrawable = builder.build();
    setIndeterminateDrawable(indeterminateDrawable);
    setIndeterminate(true);
  }

  public void changeToSuccess() {
    Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(),
        R.drawable.evaluate_icon_success);
    if (checkIndeterminateDrawable() != null) {
      checkIndeterminateDrawable().changeToSuccess(bitmap);
      postDelayed(new Runnable() {
        @Override
        public void run() {
          // 停止进度条，防止持续绘制successIcon，占用cpu.
          setIndeterminate(false);
          mSuccess = true;
        }
      }, SHOW_SUCCESS_DURATION);
    }
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mSuccess) {
      if (checkIndeterminateDrawable() != null) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(),
            R.drawable.evaluate_icon_success);
        checkIndeterminateDrawable().drawSuccess(canvas, bitmap);
      }
    }
  }

  public void changeToLoading() {
    if (checkIndeterminateDrawable() != null) {
      checkIndeterminateDrawable().changeToLoading();
      mSuccess = false;
    }
  }

  private CircularProgressDrawable checkIndeterminateDrawable() {
    Drawable ret = getIndeterminateDrawable();
    if (ret == null || !(ret instanceof CircularProgressDrawable)) {
      return null;
    }
    return (CircularProgressDrawable) ret;
  }

  public void progressiveStop() {
    if (checkIndeterminateDrawable() != null) {
      checkIndeterminateDrawable().progressiveStop();
    }
  }

  public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
    if (checkIndeterminateDrawable() != null) {
      checkIndeterminateDrawable().progressiveStop(listener);
    }
  }
}
