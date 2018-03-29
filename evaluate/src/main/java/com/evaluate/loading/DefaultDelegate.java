package com.evaluate.loading;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.evaluate.R;


class DefaultDelegate implements PBDelegate {
  private static final ArgbEvaluator COLOR_EVALUATOR = new ArgbEvaluator();
  private static final Interpolator END_INTERPOLATOR = new LinearInterpolator();
  private static final long ROTATION_ANIMATOR_DURATION = 2000;//2000
  private static final long SWEEP_ANIMATOR_DURATION = 1500;//600
  private static final long END_ANIMATOR_DURATION = 200;
  private static long SHOW_SUCCESS_DURATION = 500;

  private ValueAnimator mSweepAppearingAnimator;
  private ValueAnimator mSweepDisappearingAnimator;
  private ValueAnimator mRotationAnimator;
  private ValueAnimator mEndAnimator;
  private boolean mModeAppearing;
  private Context mContext;
  private int mCurrentColor;
  private int mCurrentIndexColor;
  private float mCurrentSweepAngle;
  private float mCurrentRotationAngleOffset = 0;
  private float mCurrentRotationAngle = 0;
  private float mCurrentEndRatio = 1f;
  private boolean mFirstSweepAnimation;

  //params
  private Interpolator mAngleInterpolator;
  private Interpolator mSweepInterpolator;
  private int[] mColors;
  private float mSweepSpeed;
  private float mRotationSpeed;
  private int mMinSweepAngle;
  private int mMaxSweepAngle;
  private int bgColor;
  private int maskColor;
  private Options mOptions;
  protected Bitmap successIcon;
  protected boolean successState;
  private long startShowSuccessTime;


  private CircularProgressDrawable mParent;
  private CircularProgressDrawable.OnEndListener mOnEndListener;

  public DefaultDelegate(Context context, @NonNull CircularProgressDrawable parent,
      @NonNull Options options) {
    mContext = context;
    mParent = parent;
    mOptions = options;
    mSweepInterpolator = options.sweepInterpolator;
    mAngleInterpolator = options.angleInterpolator;
    mCurrentIndexColor = 0;
    mColors = options.colors;
    mCurrentColor = mColors[0];
    mSweepSpeed = options.sweepSpeed;
    mRotationSpeed = options.rotationSpeed;
    mMinSweepAngle = options.minSweepAngle;
    mMaxSweepAngle = options.maxSweepAngle;
    bgColor = options.bgColor;
    setupAnimations();
  }

  private void reinitValues() {
    mFirstSweepAnimation = true;
    mCurrentEndRatio = 1f;
    mParent.getCurrentPaint().setColor(mCurrentColor);
  }

  @Override
  public void changeToSuccess(Bitmap successIcon) {
    this.successIcon = successIcon;
    startShowSuccessTime = System.currentTimeMillis();
    maskColor = mContext.getResources().getColor(R.color.evaluate_color_FFF6F2);
    successState = true;
  }

  @Override
  public void changeToLoading() {
    successState = false;
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    RectF bounds = mParent.getDrawableBounds();
    int centreX = (int) (bounds.left + (bounds.right - bounds.left) / 2);
    int centreY = (int) (bounds.top + (bounds.right - bounds.left) / 2);
    int radius = (int) (bounds.right - bounds.left) / 2;
    initBackground(canvas, paint, centreX, centreY, radius, successState ? false : true);
    if (successState) {
      drawSuccessIcon(canvas, paint, centreX, centreY, radius);
    } else {
      drawLoadingView(canvas);
    }


  }

  @Override
  public void drawSuccess(Canvas canvas, Paint paint, Bitmap successIcon) {
    if (successIcon != null && mParent != null) {
      RectF bounds = mParent.getDrawableBounds();
      int centreX = (int) (bounds.left + (bounds.right - bounds.left) / 2);
      int centreY = (int) (bounds.top + (bounds.right - bounds.left) / 2);
      int radius = (int) (bounds.right - bounds.left) / 2;
      initBackground(canvas, paint, centreX, centreY, radius, false);

      int width = 2 * radius;
      int left = centreX - radius;
      int top = centreY - radius;
      Rect src = new Rect(left, top, left + width, top + 2 * radius);
      RectF oval = new RectF(src);
      canvas.drawBitmap(successIcon, src, oval, paint);
    }
  }

  private void initBackground(Canvas canvas, Paint paint, int centreX, int centreY, float radius,
      boolean loadState) {
    initBgPaint(paint, loadState);
    canvas.drawCircle(centreX, centreY, radius, paint);
  }

  private void drawSuccessIcon(Canvas canvas, Paint paint, int centreX, int centerY, int radius) {
    float pg = getValue();
    int width = (int) (2 * radius * pg);
    int left = centreX - radius;
    int top = centerY - radius;
    Rect src = new Rect(left, top, left
        + width, top + 2 * radius);
    RectF oval = new RectF(src);
    Bitmap sucBitmap = creatAnimationView(successIcon, radius);
    if (sucBitmap != null) {
      canvas.drawBitmap(sucBitmap, src, oval, paint);
    }
  }

  private Bitmap creatAnimationView(Bitmap dstBitmap, int radius) {
    float pg = getValue();
    int width = (int) (dstBitmap.getWidth() * pg);
    int height = dstBitmap.getHeight();
    if (width <= 0 || height <= 0) {
      return null;
    }
    Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    /**
     * 产生一个同样大小的画布
     */
    Canvas canvas = new Canvas(target);
    canvas.drawBitmap(dstBitmap, 0, 0, null);
    return target;
  }


  private float getValue() {
    long currentTime = System.currentTimeMillis();
    float progress = (currentTime - startShowSuccessTime) * 1.0f / SHOW_SUCCESS_DURATION;
    if (progress > 1) {
      return 1;
    }
    return progress;
  }

  private Bitmap createCircleImage(Bitmap source, int min) {
    final Paint paint = new Paint();
    paint.setAntiAlias(true);
    Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
    /**
     * 产生一个同样大小的画布
     */
    Canvas canvas = new Canvas(target);
    /**
     * 首先绘制圆形
     */
    canvas.drawCircle(min / 2, min / 2, min / 2, paint);
    /**
     * 使用SRC_IN
     */
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    /**
     * 绘制图片
     */
    canvas.drawBitmap(source, 0, 0, paint);
    return target;
  }

  private void drawLoadingView(Canvas canvas) {
    Paint paint;//reset paint
    paint = mParent.initPaint(mOptions);
    float startAngle = mCurrentRotationAngle - mCurrentRotationAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
    }
    startAngle %= 360;
    if (mCurrentEndRatio < 1f) {
      float newSweepAngle = sweepAngle * mCurrentEndRatio;
      startAngle = (startAngle + (sweepAngle - newSweepAngle)) % 360;
      sweepAngle = newSweepAngle;
    }
    float endAngle = startAngle + sweepAngle;
    float newEndAngle = transAngle(endAngle);
    if (newEndAngle + sweepAngle > 360) {
      canvas.drawArc(mParent.getDrawableBounds(), newEndAngle, 360 - newEndAngle, false, paint);
      canvas.drawArc(mParent.getDrawableBounds(), 0, sweepAngle + newEndAngle - 360, false, paint);
    } else {
      canvas.drawArc(mParent.getDrawableBounds(), newEndAngle, sweepAngle, false, paint);
    }
  }

  private float transAngle(float input) {
    return (360 - input % 360) % 360;
  }

  private void initBgPaint(Paint paint, boolean loadState) {
    if (loadState) {
      paint.setColor(bgColor); //设置圆环的颜色
    } else {
      paint.setColor(maskColor);
    }
    paint.setStyle(Paint.Style.FILL);
    paint.setStrokeWidth(0); //设置圆环的宽度
    paint.setAntiAlias(true);  //消除锯齿
  }

  @Override
  public void start() {
    mEndAnimator.cancel();
    reinitValues();
    mRotationAnimator.start();
    mSweepAppearingAnimator.start();
  }

  @Override
  public void stop() {
    stopAnimators();
  }

  private void stopAnimators() {
    mRotationAnimator.cancel();
    mSweepAppearingAnimator.cancel();
    mSweepDisappearingAnimator.cancel();
    mEndAnimator.cancel();
  }

  private void setAppearing() {
    mModeAppearing = true;
    mCurrentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentRotationAngleOffset = mCurrentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  public void setCurrentRotationAngle(float currentRotationAngle) {
    mCurrentRotationAngle = currentRotationAngle;
    mParent.invalidate();
  }

  public void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    mParent.invalidate();
  }

  private void setEndRatio(float ratio) {
    mCurrentEndRatio = ratio;
    mParent.invalidate();
  }

  //////////////////////////////////////////////////////////////////////////////
  ////////////////            Animation

  private void setupAnimations() {
    mRotationAnimator = ValueAnimator.ofFloat(0f, 360f);
    mRotationAnimator.setInterpolator(mAngleInterpolator);
    mRotationAnimator.setDuration((long) (ROTATION_ANIMATOR_DURATION / mRotationSpeed));
    mRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float angle = Utils.getAnimatedFraction(animation) * 360f;
        setCurrentRotationAngle(angle);
      }
    });
    mRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);

    mSweepAppearingAnimator = ValueAnimator.ofFloat(mMinSweepAngle, mMaxSweepAngle);
    mSweepAppearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepAppearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepAppearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = Utils.getAnimatedFraction(animation);
        float angle;
        if (mFirstSweepAnimation) {
          angle = animatedFraction * mMaxSweepAngle;
        } else {
          angle = mMinSweepAngle + animatedFraction * (mMaxSweepAngle - mMinSweepAngle);
        }
        setCurrentSweepAngle(angle);
      }
    });
    mSweepAppearingAnimator.addListener(new SimpleAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        mModeAppearing = true;
      }

      @Override
      protected void onPreAnimationEnd(Animator animation) {
        if (isStartedAndNotCancelled()) {
          mFirstSweepAnimation = false;
          setDisappearing();
          mSweepDisappearingAnimator.start();
        }
      }
    });

    mSweepDisappearingAnimator = ValueAnimator.ofFloat(mMaxSweepAngle, mMinSweepAngle);
    mSweepDisappearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepDisappearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepDisappearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = Utils.getAnimatedFraction(animation);
        setCurrentSweepAngle(mMaxSweepAngle - animatedFraction * (mMaxSweepAngle - mMinSweepAngle));

        long duration = animation.getDuration();
        long played = animation.getCurrentPlayTime();
        float fraction = (float) played / duration;
        if (mColors.length > 1 && fraction > .7f) { //because
          int prevColor = mCurrentColor;
          int nextColor = mColors[(mCurrentIndexColor + 1) % mColors.length];
          int newColor = (Integer) COLOR_EVALUATOR
              .evaluate((fraction - .7f) / (1 - .7f), prevColor, nextColor);
          mParent.getCurrentPaint().setColor(newColor);
        }
      }
    });
    mSweepDisappearingAnimator.addListener(new SimpleAnimatorListener() {
      @Override
      protected void onPreAnimationEnd(Animator animation) {
        if (isStartedAndNotCancelled()) {
          setAppearing();
          mCurrentIndexColor = (mCurrentIndexColor + 1) % mColors.length;
          mCurrentColor = mColors[mCurrentIndexColor];
          mParent.getCurrentPaint().setColor(mCurrentColor);
          mSweepAppearingAnimator.start();
        }
      }
    });
    mEndAnimator = ValueAnimator.ofFloat(1f, 0f);
    mEndAnimator.setInterpolator(END_INTERPOLATOR);
    mEndAnimator.setDuration(END_ANIMATOR_DURATION);
    mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        setEndRatio(1f - Utils.getAnimatedFraction(animation));

      }
    });
  }

  /////////////////////////////////////////////////////////
  /// Stop
  /////////////////////////////////////////////////////////

  @Override
  public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
    if (!mParent.isRunning() || mEndAnimator.isRunning()) {
      return;
    }
    mOnEndListener = listener;
    mEndAnimator.addListener(new SimpleAnimatorListener() {

      @Override
      public void onPreAnimationEnd(Animator animation) {
        mEndAnimator.removeListener(this);
        CircularProgressDrawable.OnEndListener endListener = mOnEndListener;
        mOnEndListener = null;

        if (isStartedAndNotCancelled()) {
          setEndRatio(0f);
          mParent.stop();
          if (endListener != null) {
            endListener.onEnd(mParent);
          }
        }
      }
    });
    mEndAnimator.start();
  }
}
