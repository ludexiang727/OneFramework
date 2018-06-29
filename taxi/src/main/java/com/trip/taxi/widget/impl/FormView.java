package com.trip.taxi.widget.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.map.log.Logger;
import com.trip.taxi.R;
import com.trip.taxi.divider.DividerViewLayout;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.widget.IFormView;
import com.trip.taxi.widget.IFullFormView;
import com.trip.taxi.widget.IOptionView;
import com.trip.taxi.widget.TaxiFullFormView;

public class FormView extends DividerViewLayout implements IFormView, View.OnClickListener,
    IOptionView.IOptionChange, IFullFormView.IFullFormListener {

  private Context mContext;

  private LinearLayout mEasyForm;
  private LinearLayout mBookingTimeLayout;
  private TextView mBookingTime;
  private TextView mStart;
  private TextView mEnd;
  private IOptionView mOptionsView;

  private IFullFormView mFullView;

  private IFormListener iFormView;

  private int mFormType;

  private int mDownX;
  private int mDownY;

  private final int mScaleSlop;

  private IOnHeightChange mHeightChangeListener;

  public FormView(@NonNull Context context) {
    this(context, null);
  }

  public FormView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FormView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    ViewConfiguration configuration = ViewConfiguration.get(mContext);
    mScaleSlop = configuration.getScaledTouchSlop();
    loadFormView();
  }

  private void loadFormView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.taxi_form_view_layout, this, true);
    initView(view);
    setClipChildren(false);
  }

  private void initView(View view) {
    mEasyForm = (LinearLayout) view.findViewById(R.id.taxi_common_address_layout);
    mBookingTimeLayout = (LinearLayout) view.findViewById(R.id.taxi_form_booking_time_layout);
    mBookingTime = (TextView) view.findViewById(R.id.taxi_form_booking_time);
    mStart = (TextView) view.findViewById(R.id.taxi_start_address);
    mEnd = (TextView) view.findViewById(R.id.taxi_end_address);
    mOptionsView = (OptionsView) view.findViewById(R.id.taxi_options_view);
    mFullView = (TaxiFullFormView) view.findViewById(R.id.taxi_form_full_view);

    mStart.setOnClickListener(this);
    mEnd.setOnClickListener(this);
    mBookingTimeLayout.setOnClickListener(this);
    mOptionsView.setOptionChange(this);
    mFullView.setFullFormListener(this);

    mOptionsView.getView().getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            View view = mOptionsView.getView();
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            if (mHeightChangeListener != null) {
              mHeightChangeListener.onHeightChange(view.getMeasuredHeight());
            }
          }
        });
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mFormType == FULL_FORM) {
      mDownX = (int) (ev.getX() + 0.5f);
      mDownY = (int) (ev.getY() + 0.5f);
      if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
        int x = (int) (ev.getX() + 0.5f);
        int y = (int) (ev.getY() + 0.5f);
        if (Math.abs(x - mDownX) <= mScaleSlop && Math.abs(y - mDownY) <= mScaleSlop) {
          return super.onInterceptTouchEvent(ev);
        } else {
          return true;
        }
      }
      return super.onInterceptTouchEvent(ev);
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        mDownX = (int) (event.getX() + 0.5f);
        mDownY = (int) (event.getY() + 0.5f);
        return true;
      }
      case MotionEvent.ACTION_UP: {
        int x = (int) (event.getX() + 0.5f);
        int y = (int) (event.getY() + 0.5f);
        if (y < mDownY) {
          mFullView.showExpand();
        } else {
          mFullView.showCollapse();
        }
        break;
      }
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void setFormType(@FormType int type) {
    // state == 1 now == 2 booking default now
    int state = mOptionsView.getState();
    mFullView.setFormType(state);
//    translation(state);
    mBookingTimeLayout.setVisibility(state == 1 ? View.GONE : View.VISIBLE);
    if (type == EASY_FORM) {
      showEasyForm();
    } else if (type == FULL_FORM) {
      showFullForm();
    }
    mFormType = type;
    if (mHeightChangeListener != null) {
      mHeightChangeListener.onHeightChange(-1);
    }
  }

  private void translation(final int state) {
    int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    mBookingTimeLayout.measure(width, height);
    float from = state == 1 ? 0f : mBookingTimeLayout.getMeasuredHeight();
    float to = state == 1 ? mBookingTimeLayout.getMeasuredHeight() : 0f;
    ObjectAnimator translation = ObjectAnimator.ofFloat(mBookingTimeLayout, "translationY", from, to);
    translation.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        mBookingTimeLayout.setVisibility(state == 1 ? View.GONE : View.VISIBLE);
        Logger.e("ldx", "Animation  End ");
        if (mHeightChangeListener != null) {
          mHeightChangeListener.onHeightChange(-1);
        }
      }

      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        Logger.e("ldx", "Animation  Start ");
        mBookingTimeLayout.setVisibility(View.VISIBLE);
      }

    });
    translation.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mBookingTimeLayout.setTranslationY((Float) animation.getAnimatedValue());
      }
    });
    translation.setDuration(300);
    translation.start();
  }

  @Override
  public void setOptionType(int type) {
    mOptionsView.setState(type - 1);
  }

  @Override
  public int getOptionType() {
    return mOptionsView.getState();
  }

  private void showEasyForm() {
    mEasyForm.setVisibility(View.VISIBLE);
    mFullView.getView().setVisibility(View.GONE);
  }

  private void showFullForm() {
    mEasyForm.setVisibility(View.GONE);
    mFullView.getView().setVisibility(View.VISIBLE);
    mFullView.showFullForm();
  }

  @Override
  public void setStartPoint(String startPoint) {
    mStart.setText(startPoint);
  }

  @Override
  public void setEndPoint(String endPoint) {
    mEnd.setText(endPoint);
  }

  @Override
  public void setTime(long time, String showTime) {
    if (time == 0) {
      mBookingTime.setText(R.string.taxi_book_time);
    } else {
      mBookingTime.setText(showTime);
    }
    mFullView.setTime(time, showTime);
  }

  @Override
  public void setFormListener(IFormListener listener) {
    iFormView = listener;
  }

  @Override
  public void showLoading(boolean isLoading) {
    mFullView.showLoading(isLoading);
  }

  @Override
  public void showError() {
    mFullView.showError();
  }

  @Override
  public void updateTitle(String price, String coupon, String discount) {
    mFullView.updatePriceInfo(price, coupon, discount);
  }

  @Override
  public void setMoney(int fee) {
    mFullView.setMoney(fee);
  }

  @Override
  public void setMsg(String msg) {
    mFullView.setMsg(msg);
  }

  @Override
  public void setPay4PickUp(boolean isPickUp) {
    mFullView.setPay4PickUp(isPickUp);
  }

  @Override
  public void onClick(View view) {
    if (iFormView == null) {
      return;
    }
    final int id = view.getId();
    if (id == R.id.taxi_start_address) {
      iFormView.onStartClick();
    } else if (id == R.id.taxi_end_address) {
      iFormView.onEndClick();
    } else if (id == R.id.taxi_form_booking_time_layout) {
      iFormView.onTimeClick();
    } else if (id == R.id.taxi_full_form_tip_layout) {
      iFormView.onTipClick();
    } else if (id == R.id.taxi_full_form_mark_layout) {
      iFormView.onMarkClick(view);
    } else if (id == R.id.taxi_invoke_driver) {
      TaxiOrder order = (TaxiOrder) view.getTag();
      iFormView.forward(order);
    }
  }

  @Override
  public void onChange() {
    setFormType(mFormType);
  }


  @Override
  public void setOnHeightChange(IOnHeightChange onChangeListener) {
    mHeightChangeListener = onChangeListener;
  }

  @Override
  public View getFormView() {
    if (mFormType == FULL_FORM) {
      return mFullView.getView();
    }
    return mEasyForm;
  }

  @Override
  public int getFormType() {
    return mFormType;
  }
}
