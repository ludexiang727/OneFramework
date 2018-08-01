package com.trip.taxi.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.one.framework.app.login.ILogin;
import com.one.framework.app.login.ILogin.ILoginListener;
import com.one.framework.app.login.Login;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.utils.ToastUtils;
import com.one.framework.utils.UIUtils;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.widget.BaseLinearLayout;
import com.trip.taxi.R;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.presenter.TaxiFullFormPresenter;
import org.greenrobot.eventbus.EventBus;

public class TaxiFullFormView extends BaseLinearLayout implements IFullFormView, View.OnClickListener {
  private TripButton mSendOrder;
  private LoadingView mInvokeLoading;
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
  private View mMarkLayout;
  private TextView mMark;
  private TextView mMarkSelect;
  private IFullFormListener mClickListener;
  private ValueAnimator mScaleAnim;
  private Context mContext;
  private boolean isFullView = false;
  private boolean mPlayAnimator = false;
  private View mViewSeparator;
  private boolean isChecked;
  private String mMarkMsg = "";
  private TaxiFullFormPresenter mTaxiFullPresenter;
  private FrameLayout mEstimateLayout;

  public TaxiFullFormView(Context context) {
    this(context, null);
  }

  public TaxiFullFormView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TaxiFullFormView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mTaxiFullPresenter = new TaxiFullFormPresenter(context, this);
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
    mTimeView = (TextView) view.findViewById(R.id.taxi_form_booking_time);
    mViewSeparator = view.findViewById(R.id.taxi_full_form_separator);

    mOptionsLayout = (LinearLayout) view.findViewById(R.id.taxi_full_form_options_layout);
    mTipLayout = view.findViewById(R.id.taxi_full_form_tip_layout);
    mTip = (TextView) view.findViewById(R.id.taxi_full_form_tip);
    mCheckLayout = view.findViewById(R.id.taxi_full_form_checkbox_layout);
    mCheck = (CheckBox) view.findViewById(R.id.taxi_full_form_checkbox);
    mMarkLayout = view.findViewById(R.id.taxi_full_form_mark_layout);
    mMark = (TextView) view.findViewById(R.id.taxi_full_form_mark);
    mMarkSelect = (TextView) view.findViewById(R.id.taxi_full_form_mark_select);

    mEstimateLayout = view.findViewById(R.id.estimate_price_layout);
    mRetryEstimateLayout = (LinearLayout) view.findViewById(R.id.taxi_estimate_retry_layout);
    mLoadingView = (LoadingView) view.findViewById(R.id.taxi_estimate_price_loading);
    mPriceLayout = (LinearLayout) view.findViewById(R.id.taxi_estimate_price_layout);
    mEstimatePrice = (TextView) view.findViewById(R.id.taxi_estimate_price);
    mEstimateDiscount = (TextView) view.findViewById(R.id.taxi_estimate_price_charge);
    mEstimateTicket = (TextView) view.findViewById(R.id.taxi_estimate_price_ticket);

    mSendOrder = (TripButton) view.findViewById(R.id.taxi_invoke_driver);
    mInvokeLoading = (LoadingView) view.findViewById(R.id.taxi_invoke_loading);

    sendEnable(false);
    mTipLayout.setOnClickListener(this);
    mMarkLayout.setOnClickListener(this);
    mTimeLayout.setOnClickListener(this);
    mSendOrder.setOnClickListener(this);
    mRetryEstimateLayout.setOnClickListener(this);
    mCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
        isChecked = isCheck;
        FormDataProvider.getInstance().savePick4Up(isCheck);
        showLoading(true);
      }
    });
  }

  private void sendEnable(boolean enable) {
    mSendOrder.setEnabled(enable);
    if (enable) {
      mSendOrder.setRippleColor(Color.parseColor("#343d4a"), Color.parseColor("#ffffff"));
    } else {
      mSendOrder.setRippleColor(Color.parseColor("#f3f3f3"), Color.parseColor("#f3f3f3"));
    }
  }

  @Override
  public void showLoading(boolean show) {
    if (show) {
      mRetryEstimateLayout.setVisibility(GONE);
      mPriceLayout.setVisibility(GONE);
    }
    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    if (show) {
      mTaxiFullPresenter.taxiEstimatePrice(mMarkMsg, isChecked);
    }
  }

  @Override
  public void setFormType(int type) {
    mFormType = type;
    mTimeLayout.setVisibility(mFormType == NOW ? View.GONE : View.VISIBLE);
    mSendOrder.setTripButtonText(mFormType == NOW ? R.string.taxi_call_now_taxi : R.string.taxi_call_booking_taxi);
    mInvokeLoading.setVisibility(View.GONE);
  }

  /**
   * 直接显示全表单不加入动画
   */
  @Override
  public void showFullForm() {
    fullView();
  }

  private void fullView() {
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
    mPriceLayout.setVisibility(VISIBLE);
    sendEnable(true);
    mEstimatePrice.setText(price);
    mEstimatePrice.setVisibility(TextUtils.isEmpty(price) ? View.GONE : View.VISIBLE);
    mEstimateDiscount.setText(coupon);
    mEstimateDiscount.setVisibility(TextUtils.isEmpty(coupon) ? View.GONE : View.VISIBLE);
    mEstimateTicket.setText(discount);
    mEstimateTicket.setVisibility(TextUtils.isEmpty(discount) ? View.GONE : View.VISIBLE);
  }

  /**
   * 预估失败
   */
  @Override
  public void estimateFail() {
    mRetryEstimateLayout.setVisibility(VISIBLE);
    mPriceLayout.setVisibility(GONE);
    sendEnable(false);
  }

  @Override
  public void setTime(long time, String showTime) {
    if (time == 0) {
      mTimeView.setText(R.string.taxi_book_time);
    } else {
      mTimeView.setText(showTime);
    }
  }

  @Override
  public void setMoney(int fee) {
    if (fee == 0) {
      mTip.setText(getContext().getString(R.string.taxi_thx_money));
    } else {
      mTip.setText(
          UIUtils.highlight(String.format(mContext.getString(R.string.taxi_thx_money_format), fee),
              Color.parseColor("#f05b48")));
    }
  }

  @Override
  public void setPay4PickUp(boolean isPickUp) {
    mCheck.setChecked(isPickUp);
  }

  @Override
  public void setMsg(String msg) {
    mMarkMsg = msg;
    if (TextUtils.isEmpty(msg)) {
      mMark.setText(R.string.taxi_msg);
      mMarkSelect.setText("");
      mMarkSelect.setVisibility(View.GONE);
    } else {
      mMark.setText(R.string.taxi_already_add_msg);
      mMarkSelect.setText(msg);
      mMarkSelect.setVisibility(View.VISIBLE);
      mMarkLayout.setTag(msg);
    }
  }

  @Override
  public void showExpand() {
    if (checkPlayAnim()) {
      return;
    }
    fullView();
  }

  @Override
  public void showCollapse() {
    if (!checkPlayAnim()) {
      return;
    }
    if (mFormType == BOOK) {
      mViewSeparator.setVisibility(View.VISIBLE);
    } else {
      mViewSeparator.setVisibility(View.GONE);
    }
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
    if (mInvokeLoading.getVisibility() == View.VISIBLE) {
      // 暂停所有选择操作
      return;
    }
    int id = view.getId();
    if (id == R.id.taxi_invoke_driver) {
      ILogin login = new Login(mContext);
      if (!login.isLogin()) {
        login.showLogin(ILogin.DIALOG);
        login.setLoginListener(new ILoginListener() {
          @Override
          public void onLoginSuccess() {
            EventBus.getDefault().post(true);
            mSendOrder.performClick();
          }

          @Override
          public void onLoginFail() {

          }
        });
        return;
      }
      if (mFormType == BOOK && FormDataProvider.getInstance().obtainBookingTime() <= 0) {
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.taxi_book_time_empty));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, mContext.getString(R.string.taxi_book_time_empty), Toast.LENGTH_SHORT).show();
        return;
      }
      mSendOrder.setTripButtonText("");
      mInvokeLoading.setVisibility(View.VISIBLE);
      mTaxiFullPresenter.taxiCreateOrder(mMarkMsg, isChecked, mFormType);
    } else if (id == R.id.taxi_estimate_retry_layout) {
      showLoading(true);
    } else {
      if (mClickListener != null) {
        mClickListener.onClick(view);
      }
    }
  }

  @Override
  public void createOrderSuccess(TaxiOrder order) {
    mInvokeLoading.setVisibility(View.GONE);
    if (mClickListener != null) {
      mSendOrder.setTag(order);
      mClickListener.onClick(mSendOrder);
    }
  }

  @Override
  public void createOrderFail() {
    if (mClickListener != null) {
      mSendOrder.setTag(null);
      mClickListener.onClick(mSendOrder);
    }
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
