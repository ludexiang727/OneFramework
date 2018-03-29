package com.test.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.test.demo.anim.TripInitPairAnim;
import com.test.demo.utils.TimeUtils;
import com.test.demo.widget.GiftView;
import com.test.demo.widget.WaveView;

/**
 * Created by mobike on 2017/11/23.
 */

public class TranslateActivity extends AppCompatActivity {

  Button mButton;
  GiftView giftView;
  RelativeLayout popContent;
  int mScreenWidth, mScreenHeight;
  WaveView waveView;

  private static final int UPDATE_INFO_WINDOW = 0x110;
  private long mCurrentTime = SystemClock.elapsedRealtime();

  private Handler mHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case UPDATE_INFO_WINDOW: {
          long updateTime = SystemClock.elapsedRealtime() - mCurrentTime;
          String formatTime = TimeUtils.longToString(updateTime, "mm:ss");
          String time = String.format(getString(R.string.wait_time), formatTime);
          giftView.setGiftText(time);

//          mCurrentTime = SystemClock.elapsedRealtime();
          updateInfoWindow();
          break;
        }
      }
    }
  };

  private void showView() {
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
      @Override
      public void run() {
        TripInitPairAnim anim = new TripInitPairAnim();
        anim.attachView(mButton, waveView);
        anim.setDuration(5000);
        anim.start();
      }
    }, 200);

  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.translate_layout);
    mButton = (Button) findViewById(R.id.test);
    waveView = (WaveView) findViewById(R.id.wave_view);
    giftView = (GiftView) findViewById(R.id.gift);
    giftView.setGiftText("这是一个礼物文案kjhkjhkhkkhhkjhk");
    popContent = (RelativeLayout) findViewById(R.id.pop_content);

//    showView();

    mButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
//        waveView.performClick();
//        giftView.performClick();
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            playAnim();
//          }
//        }, 5000);

        updateInfoWindow();

      }
    });
    mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    mScreenHeight = getResources().getDisplayMetrics().heightPixels;
  }

  @Override
  protected void onResume() {
    super.onResume();
    showView();
  }

  private void updateInfoWindow() {
    Message msg = new Message();
    msg.what = UPDATE_INFO_WINDOW;
    mHandler.sendMessageDelayed(msg, 1000);
  }

  private void playAnim() {
//    ValueAnimator anim = createAnim("translationY", 0f,  1f);
//    anim.addUpdateListener(new AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float value = (float) animation.getAnimatedValue();
//        float currentY = mClose.getY();
//        mClose.setY(currentY + (mScreenHeight - currentY) * value);
//      }
//    });
//    anim.setInterpolator(new LinearInterpolator());
//    anim.setDuration(3000);
//    anim.start();
    AnimatorSet set = new AnimatorSet();
    ValueAnimator alpha = createAnim("alpha", 1f, 0f);
    ValueAnimator translation = createAnim("translation", 0f, 1f);
    ValueAnimator scale = createAnim("scale", 1f, 0f);
    alpha.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float alpha = (float) animation.getAnimatedValue();
        giftView.setAlpha(1f - alpha);
        popContent.setAlpha(alpha);
      }
    });

    translation.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float transX = (float) animation.getAnimatedValue();
        float centerX = mScreenWidth - giftView.getViewWidth();
        float centerY = giftView.getY();
        popContent.setTranslationX((centerX - popContent.getX()) * transX);
        popContent.setTranslationY((centerY - popContent.getY()) * transX);
      }
    });

    scale.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float scale = (float) animation.getAnimatedValue();
        popContent.setScaleX(scale);
        popContent.setScaleY(scale);
      }
    });
    set.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        giftView.setVisibility(View.VISIBLE);
      }
    });
    set.playTogether(alpha, scale, translation);
    set.setDuration(500);
    set.start();
  }

  private ValueAnimator createAnim(String property, float... values) {
    PropertyValuesHolder holder = PropertyValuesHolder.ofFloat(property, values);
    ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(holder);
    return anim;
  }
}
