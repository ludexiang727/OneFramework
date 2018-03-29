package com.test.demo.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by mobike on 2018/1/3.
 */

public class WaveView extends View implements View.OnClickListener {

  private Paint mPaint;
  private int mScreenWidth;
  private int mScreenHeight;
  private Path mPath;
  private int mWaveCount;

  private int mWaveLength = 1000;
  private int mCenterY;

  private int mOffset;

  public WaveView(Context context) {
    this(context, null);
  }

  public WaveView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    mScreenWidth = metrics.widthPixels;
    mScreenHeight = metrics.heightPixels;

    mPath = new Path();

    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setColor(Color.LTGRAY);
    mPaint.setStyle(Style.FILL);

    setOnClickListener(this);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    mPath.reset();
    mPath.moveTo(-mWaveLength + mOffset, mCenterY);
    for (int i = 0; i < mWaveCount; i++) {
      mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset, mCenterY + 60, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, mCenterY);
      mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset, mCenterY - 60, i * mWaveLength + mOffset, mCenterY);
    }

    mPath.lineTo(mScreenWidth, mScreenHeight);
    mPath.lineTo(0, mScreenHeight);
    mPath.close();
    canvas.drawPath(mPath, mPaint);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mScreenWidth = w;
    mScreenHeight = h;
    mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
    mCenterY = mScreenHeight / 2;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {

        return true;
      }
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void onClick(View v) {
    ValueAnimator animator = ValueAnimator.ofInt(0, mWaveLength);
    animator.setDuration(1000);
    animator.setRepeatCount(ValueAnimator.INFINITE);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mOffset = (int) animation.getAnimatedValue();
        postInvalidate();
      }
    });
    animator.start();
  }
}
