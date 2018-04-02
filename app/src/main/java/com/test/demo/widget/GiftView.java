package com.test.demo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import com.test.demo.R;

/**
 * Created by mobike on 2018/1/3.
 */

public class GiftView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

  private LinearGradient mLinearGradient;
  private Context mContext;
//   Color.parseColor("#1f008900"),
//      Color.parseColor("#ffa99000"),
  private int[] mColors = new int[]{Color.parseColor("#FFFF5322"), Color.parseColor("#FFFAB161")};
  private float[] mPosition = new float[] {0.785f, .925f};
  private Paint mPaint;
  private String mGift ="这是一个礼物券";
  private RectF mRectF;
  private int mViewWidth;
  private int mViewHeight;
  private float mOffset;
  private float mTextSize;
  private float mRectLeft;
  private float mTxtWidth;
  private int mTxtColor;

  public GiftView(Context context) {
    this(context, null);
  }

  public GiftView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context;
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GiftView);
    mViewWidth = a.getDimensionPixelOffset(R.styleable.GiftView_width, 0);
    mViewHeight = a.getDimensionPixelOffset(R.styleable.GiftView_height, 0);
    mTextSize = a.getDimensionPixelSize(R.styleable.GiftView_gift_text_size, 0);
    mTxtColor = Color.WHITE;
    a.recycle();
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStyle(Style.FILL);
    mPaint.setColor(Color.LTGRAY);

    // 需测量文案的宽度
    setOnClickListener(this);
  }

  /**
   * 渐变颜色
   */
  public void setGradientColors(int[] colors) {
    mColors = colors;
    postInvalidate();
  }

  /**
   * 展示的文案
   */
  public void setGiftText(String gift) {
    mGift = gift;
    mOffset = getRectWidth();
    postInvalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 文案宽度
    mTxtWidth = getRectWidth();
    // 其实宽度
    mRectLeft = getMeasuredWidth() - mViewWidth;
    drawRect(canvas);

    int restore = canvas.save();
    Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    txtPaint.setTextSize(mTextSize);
    txtPaint.setTextAlign(Paint.Align.CENTER);
    txtPaint.setColor(mTxtColor);
    Path path = new Path();
    path.moveTo(mRectF.left, mRectF.centerY());
    path.lineTo(mRectF.right, mRectF.centerY());
    // 50 水平偏移量
    // 10 垂直偏移量
    canvas.drawTextOnPath(mGift, path, 50, 10, txtPaint);
    canvas.restoreToCount(restore);

    Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.time);
    Paint iconPaint = new Paint();
    float top = (mRectF.height() - bmp.getHeight()) / 2;
    canvas.drawBitmap(bmp, mRectF.left, top, iconPaint);
  }

  /**
   */
  private void drawRect(Canvas canvas) {
    if (mColors != null) {
      mLinearGradient = new LinearGradient(0, getMeasuredWidth(), getMeasuredWidth(), 0, mColors,
          mPosition, TileMode.MIRROR);
      mPaint.setShader(mLinearGradient);
    }
    mRectF = new RectF(mRectLeft - mOffset, 0, mRectLeft + mViewWidth, mViewHeight);
    canvas.drawRoundRect(mRectF, mViewWidth / 2, mViewHeight / 2, mPaint);
  }

  public void start() {
    ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
    animator.setDuration(500);
    animator.setInterpolator(new AccelerateInterpolator());
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mOffset = mOffset * (float) animation.getAnimatedValue();
        mPosition = new float[]{.9465f, 1f};
        mTxtColor = blendColors(mTxtColor, Color.TRANSPARENT, animation.getAnimatedFraction());
        postInvalidate();
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
//        setVisibility(INVISIBLE);
//        mColors = new int[]{Color.parseColor("#FFFAB161"), Color.parseColor("#FFFF5322")};
//        postInvalidate();
      }

      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);

      }
    });
    animator.start();
  }

  @Override
  public void onClick(View v) {
    start();
  }

  private float getRectWidth() {
    TextPaint paint = getPaint();
    return paint.measureText(mGift);
  }

  /**
   * 颜色变化
   * @param bottom
   * @param top
   * @param ratio
   * @return
   */
  private int blendColors(int bottom, int top, float ratio) {
    float rr = Math.max(0F, Math.min(ratio, 1F));
    float inverseRatio = 1f - rr;
    float r = Color.red(top) * rr + Color.red(bottom) * inverseRatio;
    float g = Color.green(top) * rr + Color.green(bottom) * inverseRatio;
    float b = Color.blue(top) * rr + Color.blue(bottom) * inverseRatio;
    float a = Color.alpha(top) * rr + Color.alpha(bottom) * inverseRatio;
    return Color.argb((int) a, (int) r, (int) g, (int) b);
  }

  public float getViewWidth() {
    return mRectF.width();
  }

}
