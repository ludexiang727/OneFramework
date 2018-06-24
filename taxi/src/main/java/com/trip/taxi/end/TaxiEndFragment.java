package com.trip.taxi.end;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.pop.PopUpService;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.framework.app.widget.TripButton;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams.Service;
import com.trip.base.end.EndFragment;
import com.trip.taxi.R;
import com.trip.taxi.end.presenter.TaxiEndPresenter;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiOrder;

/**
 * Created by ludexiang on 2018/6/24.
 */

public class TaxiEndFragment extends EndFragment implements View.OnClickListener {
  private TaxiEndPresenter mEndPresenter;
  private LinearLayout mConfirmLayout;
  private ShapeImageView mDriverHeaderIcon;
  private ImageView mDriverIM;
  private ImageView mDriverPhone;
  private TextView mDriverName;
  private TextView mDriverCarNo;
  private TextView mDriverCompany;
  private StarView mDriverStarView;
  private TaxiOrder mTaxiOrder;
  private PopUpService mPopService;
  private OrderStatus mCurrentStatus;
  private boolean isAddedMark = false;

  private EditText mInputMoney;
  private TripButton mEndPay;
  private LoadingView mEndLoading;
  private EditWatch mWatcher;

  private LinearLayout mFinishedLayout;
  private TextView mEvaluateDriver;
  private TextView mPayMoney;
  private TextView mPayVoucher;
  private TextView mFeeCharge;
  private TextView mTickWipe;

  private AnimatorSet mRightOutSet;
  private AnimatorSet mLeftInSet;


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    boolean isFromHistory = false;
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISITORY);
    }
    mEndPresenter = new TaxiEndPresenter(getContext(), mTaxiOrder, this);
    mMap.removeDriverLine();
    mMap.stopRadarAnim();
    mTopbarView.setTitle(R.string.taxi_service_driver_trip_ending);
    mTopbarView.setLeft(isFromHistory ? R.drawable.one_top_bar_back_selector : 0);
    mTopbarView.setTitleRight(0);
    mCurrentStatus = OrderStatus.ARRIVED;
    mMap.hideMyLocation();
    mWatcher = new EditWatch();
    mEndPresenter.addMarks(mTaxiOrder);
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_end_trip_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mConfirmLayout = (LinearLayout) view.findViewById(R.id.taxi_end_trip_finish_confirm_money_layout);
    mDriverHeaderIcon = (ShapeImageView) view.findViewById(R.id.taxi_service_driver_icon);
    mDriverIM = (ImageView) view.findViewById(R.id.taxi_service_driver_im);
    mDriverPhone = (ImageView) view.findViewById(R.id.taxi_service_driver_call);
    mDriverName = (TextView) view.findViewById(R.id.taxi_service_driver_name);
    mDriverCarNo = (TextView) view.findViewById(R.id.taxi_service_driver_car_no);
    mDriverCompany = (TextView) view.findViewById(R.id.taxi_service_driver_company);
    mDriverStarView = (StarView) view.findViewById(R.id.taxi_service_driver_star);
    mInputMoney = (EditText) view.findViewById(R.id.taxi_end_input_money);
    mEndPay = (TripButton) view.findViewById(R.id.taxi_end_pay);
    mEndLoading = (LoadingView) view.findViewById(R.id.taxi_end_loading_view);

    mFinishedLayout = (LinearLayout) view.findViewById(R.id.taxi_end_trip_finished_layout);
    mPayMoney = (TextView) view.findViewById(R.id.taxi_end_finish_trip_total_fee);
    mPayVoucher = (TextView) view.findViewById(R.id.taxi_end_finish_trip_voucher);
    mFeeCharge = (TextView) view.findViewById(R.id.taxi_end_options_charge);
    mTickWipe = (TextView) view.findViewById(R.id.taxi_end_options_wipe);
    mEvaluateDriver = (TextView) view.findViewById(R.id.taxi_end_options_evaluate);

    mFeeCharge.setOnClickListener(this);
    mTickWipe.setOnClickListener(this);
    mEvaluateDriver.setOnClickListener(this);
    mEndPay.setOnClickListener(this);
    mDriverPhone.setOnClickListener(this);
    mInputMoney.addTextChangedListener(mWatcher);
    updateDriverCard();
    addAnimators();
    setCameraDistance();
  }

  private void updateDriverCard() {
    OrderDriver driver = mTaxiOrder.getOrderInfo().getDriver();
    mDriverHeaderIcon.loadImageByUrl(null, driver.getDriverIcon(), "");
    mDriverName.setText(driver.getDriverName());
    mDriverStarView.setLevel(driver.getDriverStar().intValue());
  }

  @Override
  protected String getOrderId() {
    return mTaxiOrder.getOrderId();
  }

  @Override
  public void handleFinish() {
    // 已支付
    if (mRightOutSet != null && mLeftInSet != null) {
      mRightOutSet.setTarget(mConfirmLayout);
      mLeftInSet.setTarget(mFinishedLayout);
      mRightOutSet.start();
      mLeftInSet.start();
    }
  }

  // 设置动画
  private void addAnimators() {
    mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.taxi_end_anim_out);
    mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.taxi_end_anim_in);
    mLeftInSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        mFinishedLayout.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationEnd(Animator animation, boolean isReverse) {
        mEvaluate.onEvaluate();
      }
    });
  }

  // 改变视角距离, 贴近屏幕
  private void setCameraDistance() {
    int distance = 10000;
    float scale = getResources().getDisplayMetrics().density * distance;
    mConfirmLayout.setCameraDistance(scale);
    mFinishedLayout.setCameraDistance(scale);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.taxi_service_driver_call) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTaxiOrder.getOrderInfo().getDriver().getDriverTel()));
      startActivity(intent);
    } else if (id == R.id.taxi_end_options_evaluate) {
      mEvaluate.onEvaluate();
    } else if (id == R.id.taxi_end_pay) {
      mEndPay.setTripButtonText("");
      mEndLoading.setVisibility(View.VISIBLE);
      final String oid = mTaxiOrder.getOrderId();
      payList(oid);
    }
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getStartLat(),
        mTaxiOrder.getOrderInfo().getStartLng()));
    model.bounds.add(
        new LatLng(mTaxiOrder.getOrderInfo().getEndLat(), mTaxiOrder.getOrderInfo().getEndLng()));
    if (mMap.getLinePoints() != null) {
      model.bounds.addAll(mMap.getLinePoints());
    }
  }

  class EditWatch implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      mEndPay.setTripButtonText(String.format(getString(R.string.taxi_end_pay_confirm), s));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mEndPresenter.release();
  }
}
