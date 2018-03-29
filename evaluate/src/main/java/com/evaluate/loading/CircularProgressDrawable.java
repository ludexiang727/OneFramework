package com.evaluate.loading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.evaluate.R;
import com.evaluate.UIThreadHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class CircularProgressDrawable
        extends Drawable
        implements Animatable {

    public interface OnEndListener {
        void onEnd(CircularProgressDrawable drawable);
    }

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_ROUNDED = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STYLE_NORMAL, STYLE_ROUNDED})
    public @interface Style {

    }

    private final RectF mBounds = new RectF();
    private Options mOptions;
    private Paint mPaint;
    private boolean mRunning;
    private PBDelegate mPBDelegate;
    private Context mContext;

    /**
     * Private method, use #Builder instead
     */
    private CircularProgressDrawable(Context context, PowerManager powerManager, Options options) {
        mContext = context;
        mOptions = options;

        initPaint(options);

        initDelegate();
    }

    public Paint initPaint(Options options) {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(options.borderWidth);
        mPaint.setStrokeCap(options.style == STYLE_ROUNDED ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        mPaint.setColor(options.colors[0]);
        return mPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRunning()) mPBDelegate.draw(canvas, mPaint);
    }

    public void drawSuccess(Canvas canvas, Bitmap successIcon) {
        if (mPBDelegate != null && canvas != null && mPaint != null) {
            mPBDelegate.drawSuccess(canvas, mPaint, successIcon);
        }
    }

    public void changeToSuccess(Bitmap successIcon) {
        if (mPBDelegate != null) {
            mPBDelegate.changeToSuccess(successIcon);
        }
    }

    public void changeToLoading() {
        if (mPBDelegate != null) {
            mPBDelegate.changeToLoading();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        float border = mOptions.borderWidth;
        mBounds.left = bounds.left + border;
        mBounds.right = bounds.right - border;
        mBounds.top = bounds.top + border;
        mBounds.bottom = bounds.bottom - border;
    }


    @Override
    public void start() {
        initDelegate();
        mPBDelegate.start();
        mRunning = true;
        invalidateSelf();
    }

    /**
     * Inits the delegate. Create one if the delegate is null or not the right mode
     */
    private void initDelegate() {
        if (mPBDelegate == null) {
            mPBDelegate = new DefaultDelegate(mContext, this, mOptions);
        }

    }

    @Override
    public void stop() {
        mRunning = false;
        mPBDelegate.stop();
        invalidateSelf();
    }

    public void invalidate() {
        if (getCallback() == null) {
            stop(); // we don't want these animator to keep running...
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidateSelf();
        } else {
            UIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    invalidateSelf();
                }
            });
        }

    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    public Paint getCurrentPaint() {
        return mPaint;
    }

    public RectF getDrawableBounds() {
        return mBounds;
    }

    ////////////////////////////////////////////////////////////////////
    //Progressive stop
    ////////////////////////////////////////////////////////////////////

    public void progressiveStop(OnEndListener listener) {
        mPBDelegate.progressiveStop(listener);
    }

    public void progressiveStop() {
        progressiveStop(null);
    }

    public static class Builder {
        private static final Interpolator DEFAULT_ROTATION_INTERPOLATOR = new LinearInterpolator();
        private static final Interpolator DEFAULT_SWEEP_INTERPOLATOR = new FoSiInterpolator();

        private Interpolator mSweepInterpolator = DEFAULT_SWEEP_INTERPOLATOR;
        private Interpolator mAngleInterpolator = DEFAULT_ROTATION_INTERPOLATOR;
        private float mBorderWidth;
        private int[] mColors;
        private float mSweepSpeed;
        private float mRotationSpeed;
        private int mMinSweepAngle;
        private int mMaxSweepAngle;
        @Style
        int mStyle;
        private int bgColor;
        private PowerManager mPowerManager;
        private Context mContext;

        public Builder(@NonNull Context context) {
            this(context, false);
        }

        public Builder(@NonNull Context context, boolean editMode) {
            initValues(context, editMode);
        }

        private void initValues(@NonNull Context context, boolean editMode) {
            mContext = context;
            mBorderWidth = context.getResources().getDimension(R.dimen.cpb_default_stroke_width);
            mSweepSpeed = 1f;
            mRotationSpeed = 1f;
            if (editMode) {
                mColors = new int[]{Color.BLUE};
                mMinSweepAngle = 20;
                mMaxSweepAngle = 300;
            } else {
                mColors = new int[]{context.getResources().getColor(R.color.cpb_default_color)};
                mMinSweepAngle = context.getResources().getInteger(R.integer.cpb_default_min_sweep_angle);
                mMaxSweepAngle = context.getResources().getInteger(R.integer.cpb_default_max_sweep_angle);
            }
            mStyle = CircularProgressDrawable.STYLE_ROUNDED;
            mPowerManager = Utils.powerManager(context);
        }

        public Builder color(int color) {
            mColors = new int[]{color};
            return this;
        }

        public Builder colors(int[] colors) {
            Utils.checkColors(colors);
            mColors = colors;
            return this;
        }

        public Builder backGroundColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder sweepSpeed(float sweepSpeed) {
            Utils.checkSpeed(sweepSpeed);
            mSweepSpeed = sweepSpeed;
            return this;
        }

        public Builder rotationSpeed(float rotationSpeed) {
            Utils.checkSpeed(rotationSpeed);
            mRotationSpeed = rotationSpeed;
            return this;
        }

        public Builder minSweepAngle(int minSweepAngle) {
            Utils.checkAngle(minSweepAngle);
            mMinSweepAngle = minSweepAngle;
            return this;
        }

        public Builder maxSweepAngle(int maxSweepAngle) {
            Utils.checkAngle(maxSweepAngle);
            mMaxSweepAngle = maxSweepAngle;
            return this;
        }

        public Builder strokeWidth(float strokeWidth) {
            Utils.checkPositiveOrZero(strokeWidth, "StrokeWidth");
            mBorderWidth = strokeWidth;
            return this;
        }

        public Builder style(@Style int style) {
            mStyle = style;
            return this;
        }

        public Builder sweepInterpolator(Interpolator interpolator) {
            Utils.checkNotNull(interpolator, "Sweep interpolator");
            mSweepInterpolator = interpolator;
            return this;
        }

        public Builder angleInterpolator(Interpolator interpolator) {
            Utils.checkNotNull(interpolator, "Angle interpolator");
            mAngleInterpolator = interpolator;
            return this;
        }

        public CircularProgressDrawable build() {
            return new CircularProgressDrawable(
                    mContext,
                    mPowerManager,
                    new Options(mAngleInterpolator,
                            mSweepInterpolator,
                            mBorderWidth,
                            mColors,
                            mSweepSpeed,
                            mRotationSpeed,
                            mMinSweepAngle,
                            mMaxSweepAngle,
                            mStyle, bgColor));
        }
    }
}