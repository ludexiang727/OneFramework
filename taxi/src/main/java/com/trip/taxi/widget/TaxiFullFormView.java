package com.trip.taxi.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.TripButton;
import com.trip.taxi.R;
import com.trip.taxi.presenter.TaxiFullFormPresenter;
import java.util.Date;

/**
 * Created by mobike on 2017/12/12.
 */

public class TaxiFullFormView extends LinearLayout implements IFullFormView,
    View.OnClickListener {

  private TripButton mSendOrder;
  private LinearLayout mRetryEstimateLayout;
  private LinearLayout mTimeLayout;
  private TextView mTimeView;
  private LoadingView mLoadingView;
  private LinearLayout mPriceLayout;
  private TextView mEstimatePrice;
  private TextView mEstimateDiscount;
  private TextView mEstimateTicket;
  private int mFormType;
  private LinearLayout mOptionsLayout;
  private static final int NOW = 1;
  private static final int BOOK = 2;
  private TextView mTip;
  private View mTipLayout;
  private CheckBox mCheck;
  private View mCheckLayout;
  private TextView mMark;
  private View mMarkLayout;
//  private ImageView mArrow;
  private IFullFormListener mClickListener;
  private ValueAnimator mScaleAnim;
  private Context mContext;
  private boolean isFullView = false;
  private boolean mPlayAnimator = false;
  private View mViewSeparator;
  private TaxiFullFormPresenter mTaxiFullPresenter;

  public TaxiFullFormView(Context context) {
    this(context, null);
  }

  public TaxiFullFormView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TaxiFullFormView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mTaxiFullPresenter = new TaxiFullFormPresenter(this);
    setOrientation(LinearLayout.VERTICAL);
    mContext = context;
    setClipChildren(false);
    loadView();
  }

  private void loadView() {
    final View view = LayoutInflater.from(mContext).inflate(R.layout.taxi_full_form_view_layout, this, true);
    initView(view);
  }

  private void initView(View view) {
    mTimeLayout = (LinearLayout) view.findViewById(R.id.taxi_form_booking_time_layout);
//    mArrow = (ImageView) view.findViewById(R.id.taxi_full_form_arrow);
    mTimeView = (TextView) view.findViewById(R.id.taxi_form_booking_time);
    mViewSeparator = view.findViewById(R.id.taxi_full_form_separator);

    mOptionsLayout = (LinearLayout) view.findViewById(R.id.taxi_full_form_options_layout);
    mTipLayout =  view.findViewById(R.id.taxi_full_form_tip_layout);
    mTip = (TextView) view.findViewById(R.id.taxi_full_form_tip);
    mCheckLayout = view.findViewById(R.id.taxi_full_form_checkbox_layout);
    mCheck = (CheckBox) view.findViewById(R.id.taxi_full_form_checkbox);
    mMarkLayout = view.findViewById(R.id.taxi_full_form_mark_layout);
    mMark = (TextView) view.findViewById(R.id.taxi_full_form_mark);

    mRetryEstimateLayout = (LinearLayout) view.findViewById(R.id.taxi_estimate_retry_layout);
    mLoadingView = (LoadingView) view.findViewById(R.id.taxi_estimate_price_loading);
    mPriceLayout = (LinearLayout) view.findViewById(R.id.taxi_estimate_price_layout);
    mEstimatePrice = (TextView) view.findViewById(R.id.taxi_estimate_price);
    mEstimateDiscount = (TextView) view.findViewById(R.id.taxi_estimate_price_charge);
    mEstimateTicket = (TextView) view.findViewById(R.id.taxi_estimate_price_ticket);

    mSendOrder = (TripButton) view.findViewById(R.id.taxi_invoke_driver);

    mTipLayout.setOnClickListener(this);
    mMarkLayout.setOnClickListener(this);
    mTimeLayout.setOnClickListener(this);
//    mArrow.setOnClickListener(this);

    mCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mClickListener != null) {
          mClickListener.onByMeterSelected(isChecked);
        }
      }
    });
  }

  @Override
  public void showLoading(boolean show) {
    if (show) {
      mRetryEstimateLayout.setVisibility(GONE);
      mPriceLayout.setVisibility(GONE);
    }
    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setFormType(int type) {
    mFormType = type;
    mTimeLayout.setVisibility(mFormType == NOW ? View.GONE : View.VISIBLE);
  }

  /**
   * 直接显示全表单不加入动画
   */
  @Override
  public void showFullForm() {
    fullView();
    mTaxiFullPresenter.taxiEstimatePrice("", 0, 0, false);
  }

  private void fullView() {
//    mArrow.setVisibility(View.VISIBLE);
    if (mFormType == BOOK) {
      mCheckLayout.setVisibility(View.GONE);
      mTimeLayout.setVisibility(View.VISIBLE);
      mViewSeparator.setVisibility(View.VISIBLE);
    } else {
      mTimeLayout.setVisibility(View.GONE);
      mCheckLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void showError() {
    mRetryEstimateLayout.setVisibility(VISIBLE);
    mLoadingView.setVisibility(GONE);
    mPriceLayout.setVisibility(GONE);
  }

  @Override
  public void updatePriceInfo(String price, String coupon, String discount) {
    mRetryEstimateLayout.setVisibility(GONE);
//    mLoadingLayout.setVisibility(GONE);
    mPriceLayout.setVisibility(VISIBLE);
    mEstimatePrice.setText(price);
    mEstimatePrice.setVisibility(TextUtils.isEmpty(price) ? View.GONE : View.VISIBLE);
    mEstimateDiscount.setText(coupon);
    mEstimateDiscount.setVisibility(TextUtils.isEmpty(coupon) ? View.GONE : View.VISIBLE);
    mEstimateTicket.setText(discount);
    mEstimateTicket.setVisibility(TextUtils.isEmpty(discount) ? View.GONE : View.VISIBLE);
  }

  @Override
  public void setTime(long time) {
    if (time == 0) {
      mTimeView.setText(R.string.taxi_book_time);
    } else {
      Date date = new Date();
      date.setTime(time);
//      mTimeView.setText(TimeSelectDialogHelper.Companion.getTimeString(date));
    }
  }

  @Override
  public void setMoney(int fee) {
    if (fee == 0) {
      mTip.setText(getContext().getString(R.string.taxi_thx_money));
    } else {
//      mTip.setText(getContext().getString(R.string.taxi_thx_money) + fee + PassportManager.getInstance()
//          .getCurrency().getUnit());
    }
  }

  @Override
  public void setMsg(String msg) {
    if (TextUtils.isEmpty(msg)) {
      mMark.setText("");
      mMark.setVisibility(View.GONE);
    } else {
      mMark.setText(msg);
      mMark.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void showExpand() {
    if (checkPlayAnim()) {
      return;
    }
    fullView();
    play();
  }

  @Override
  public void showCollapse() {
    if (!checkPlayAnim()) {
      return;
    }
//    mArrow.setVisibility(View.VISIBLE);
    if (mFormType == BOOK) {
      mViewSeparator.setVisibility(View.VISIBLE);
    } else {
      mViewSeparator.setVisibility(View.GONE);
    }
    play();
  }

  private boolean checkPlayAnim() {
    return mOptionsLayout.getScaleY() >= 1.0f;
  }

  @Override
  public void setFullFormListener(IFullFormListener listener) {
    mClickListener = listener;
  }

  @Override
  public void onClick(View view) {
    if (mClickListener != null) {
      mClickListener.onClick(view.getId());
    }
  }

  private void play() {
//    if (mPlayAnimator) {
//      return;
//    }
//    mPlayAnimator = true;
//    PanelAnimator animator = new PanelAnimator();
//    animator.attachView(mOptionsLayout, mOptionsLayout);
//    animator.addListener(new ViewAnimator.DefaultAnimatorListener() {
//      @Override
//      public void onAnimationEnd(Animator animation) {
//        super.onAnimationEnd(animation);
//        mPlayAnimator = false;
//        if (mOptionsLayout.getScaleY() == 1.0f) {
//          isFullView = true;
//          mArrow.setRotation(180);
//        } else {
//          isFullView = false;
//          mArrow.setRotation(0);
//        }
//
//      }
//    });
//    animator.setDuration(200);
//    animator.start();
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public boolean fullFormType() {
    return isFullView;
  }
}
