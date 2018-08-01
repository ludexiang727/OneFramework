package com.trip.taxi.end;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.pop.PopUpService;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.SystemUtils;
import com.one.map.log.Logger;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams.Service;
import com.trip.base.end.EndFragment;
import com.trip.taxi.R;
import com.trip.taxi.end.presenter.TaxiEndPresenter;
import com.trip.taxi.net.model.FeeInfo;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiEvaluate;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderDetail;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/24.
 */

public class TaxiEndFragment extends EndFragment implements View.OnClickListener, ITaxiEndView {
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

  private LinearLayout mPayLayout;
  private TextView mEndTotalFee;
  private LinearLayout mPayExtraInfoLayout;
  private TextView mNeedPayFee;
  private LinearLayout mPayChargeDistant;
  private TripButton mGoPay;
  private LoadingView mPayLoading;

  private LinearLayout mFinishedLayout;
  private LinearLayout mOnLineLayout;
  private TextView mEvaluateDriver;
  private TextView mPayMoney;
  private TextView mPayVoucher;
  private TextView mFeeCharge;
  private TextView mCashier;
  private TextView mTickWipe;

  private AnimatorSet mRightOutSet;
  private AnimatorSet mLeftInSet;

  private String mConfirmPayFee;
  private boolean isSelfPay; // 是否是乘客发起支付
  private static final int PAY_INFO_STATUS = 0x001;
  private HandlerThread mPayInfoHandler = new HandlerThread("PAY_INFO");
  private PayHandler mPayHandler;
  private FeeInfo feeInfo;
  // 是否已评价
  private boolean isEvaluated = false;
  private int star;
  private String content;
  private List<String> evaluateTag = new ArrayList<>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISTORY);
      TaxiOrderDetail taxiOrderDetail = mTaxiOrder.getOrderInfo();
      if (taxiOrderDetail != null && taxiOrderDetail.getTaxiInfo() != null)
      isEvaluated = taxiOrderDetail.getTaxiInfo().getTaxiFeedBack() == 1;
      if (isEvaluated) {
        TaxiEvaluate taxiEvaluate = taxiOrderDetail.getTaxiInfo().getTaxiEvaluate();
        if (taxiEvaluate != null ) {
          if (!TextUtils.isEmpty(taxiEvaluate.getTags())) {
            String[] tags = taxiEvaluate.getTags().split("\\|");
            for (String tag : tags) {
              evaluateTag.add(tag);
            }
          }
          star = taxiEvaluate.getStar();
          content = taxiEvaluate.getContent();
        }
      }
    }
    mEndPresenter = new TaxiEndPresenter(getContext(), mTaxiOrder, this);
    mPayInfoHandler.start();
    mPayHandler = new PayHandler(mPayInfoHandler.getLooper());
    mPayHandler.sendEmptyMessage(PAY_INFO_STATUS);
    mMap.removeDriverLine();
    mMap.stopRadarAnim();
    mTopbarView.setTitle(R.string.taxi_service_driver_trip_ending);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitleRight(0);
    mCurrentStatus = OrderStatus.ARRIVED;
    mWatcher = new EditWatch();
    mEndPresenter.addMarks(mTaxiOrder);
    loadEvaluateTags();
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_end_trip_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mMap.hideMyLocation();
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

    mPayLayout = (LinearLayout) view.findViewById(R.id.taxi_end_trip_finish_pay_layout);
    mEndTotalFee = (TextView) view.findViewById(R.id.taxi_end_trip_total_fee);
    mPayExtraInfoLayout = (LinearLayout) view.findViewById(R.id.taxi_end_trip_pay_extra_layout);
    mNeedPayFee = (TextView) view.findViewById(R.id.taxi_end_pay_trip_fee);
    mPayChargeDistant = (LinearLayout) view.findViewById(R.id.taxi_end_trip_charge_distant_layout);
    mGoPay = (TripButton) view.findViewById(R.id.taxi_end_go_pay);
    mPayLoading = (LoadingView) view.findViewById(R.id.taxi_end_pay_loading_view);

    mFinishedLayout = (LinearLayout) view.findViewById(R.id.taxi_end_trip_finished_layout);
    mOnLineLayout = (LinearLayout) view.findViewById(R.id.taxi_end_finish_pay_layout);
    mPayMoney = (TextView) view.findViewById(R.id.taxi_end_finish_trip_total_fee);
    mPayVoucher = (TextView) view.findViewById(R.id.taxi_end_finish_trip_voucher);
    mCashier = (TextView) view.findViewById(R.id.taxi_end_finish_trip_by_cashier);
    mFeeCharge = (TextView) view.findViewById(R.id.taxi_end_options_charge);
    mTickWipe = (TextView) view.findViewById(R.id.taxi_end_options_wipe);
    mEvaluateDriver = (TextView) view.findViewById(R.id.taxi_end_options_evaluate);

    mPayChargeDistant.setOnClickListener(this);
    mGoPay.setOnClickListener(this);
    mFeeCharge.setOnClickListener(this);
    mTickWipe.setOnClickListener(this);
    mEvaluateDriver.setOnClickListener(this);
    mEndPay.setOnClickListener(this);
    mDriverPhone.setOnClickListener(this);
    mInputMoney.addTextChangedListener(mWatcher);
    if (isEvaluated) {
      evaluateSuccess();
    }
    updateDriverCard();
    addAnimators();
    setCameraDistance();
  }

  private void updateDriverCard() {
    OrderDriver driver = mTaxiOrder.getOrderInfo().getDriver();
    if (driver != null) {
      mDriverHeaderIcon.loadImageByUrl(null, driver.getDriverIcon(), "default");
      mDriverName.setText(driver.getDriverName());
      mDriverCompany.setText(driver.getDriverCompany());
      mDriverCarNo.setText(driver.getDriverCarNo());
      mDriverStarView.setLevel((int) driver.getDriverStar());
    }
  }

  @Override
  protected String getOrderId() {
    return mTaxiOrder.getOrderId();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!isSelfPay) {
      mPayLoading.setVisibility(View.GONE);
      mGoPay.setTripButtonText(R.string.taxi_end_fee_go_pay);
    }
  }

  @Override
  public void handlePay(OrderStatus status) {
    toggleMapView();
    switch (status) {
      case AUTO_PAID: //
      case AUTO_PAYING:
      case CONFIRMED_PRICE: {
        // 司机发起支付
        mPayLayout.setVisibility(View.VISIBLE);
        mLoading.showDlg();
        mPayHandler.sendEmptyMessage(PAY_INFO_STATUS);
        break;
      }
    }
  }

  @Override
  public void handlePayFail() {
    if (isSelfPay) {
      mEndPay.setTripButtonText(mConfirmPayFee);
      mEndLoading.setVisibility(View.GONE);
    } else {
      mGoPay.setTripButtonText(R.string.taxi_end_fee_go_pay);
      mPayLoading.setVisibility(View.GONE);
    }
  }

  @Override
  public void handleFinish(int payType) {
    HomeDataProvider.getInstance().saveOrderDetail(null);
    mPayLayout.setVisibility(View.GONE);
    // 已支付
    if (mRightOutSet != null && mLeftInSet != null) {
      if (payType == 1) {
        // 现金收款
        mOnLineLayout.setVisibility(View.GONE);
        mCashier.setVisibility(View.VISIBLE);
      } else {
        mOnLineLayout.setVisibility(View.VISIBLE);
        mCashier.setVisibility(View.GONE);
        mPayMoney.setText(String.format("%.2f", feeInfo.getActualPayMoney() * 1f / 100));
      }
      mRightOutSet.setTarget(mConfirmLayout);
      mLeftInSet.setTarget(mFinishedLayout);
      mRightOutSet.start();
      mLeftInSet.start();
    }
  }

  @Override
  protected void loadEvaluateTags() {
    mEndPresenter.loadEvaluateTags();
  }

  @Override
  public void evaluateSuccess() {
    mEvaluateDriver.setText(R.string.taxi_end_evaluated);
  }

  @Override
  protected void submitEvaluate(int rate, @Nullable List<String> tags, @NonNull String comment) {
    mEndPresenter.submitEvaluate(rate, tags, comment);
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
        if (!isEvaluated) {
          mEvaluate.onEvaluate();
        }
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
      if (!isEvaluated) {
        mEvaluate.onEvaluate();
      } else {
        mEvaluate.onEvaluated(star, evaluateTag, content);
      }
    } else if (id == R.id.taxi_end_pay) {
      String money = mInputMoney.getText().toString();
      if (TextUtils.isEmpty(money)) {
        Toast.makeText(getContext(), getString(R.string.taxi_input_money_empty), Toast.LENGTH_SHORT).show();
        return;
      }
      float input = Float.parseFloat(money);
      if (input > 0) {
        isSelfPay = true;
        pay(mEndPay, mEndLoading);
      } else {
        Toast.makeText(getContext(), getString(R.string.taxi_input_money_small_zero), Toast.LENGTH_SHORT).show();
      }
    } else if (id == R.id.taxi_end_go_pay) { // 司机发起支付
      isSelfPay = false;
      pay(mGoPay, mPayLoading);
    }
  }

  @Override
  public void handlePayInfo(TaxiOrderDetail orderDetail) {
    mLoading.dismissDlg();
    if (isAdded()) {
      feeInfo = orderDetail.getFeeInfo();
      Logger.e("ldx", "feeInfo >>>> " + feeInfo);
      if (feeInfo != null) {
        float payMoney = feeInfo.getTotalMoney() * 1f / 100;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        mEndTotalFee.setText(String.format(getString(R.string.taxi_end_pay_money), decimalFormat.format(payMoney)));
        mNeedPayFee.setText(decimalFormat.format(payMoney));
      }
    }
  }

  @Override
  public void orderDetailFail() {
    if (mLoading != null && mLoading.isShowing()) {
      mLoading.dismissDlg();
    }
  }

  /**
   * 去支付
   * @param tripButton
   * @param loading
   */
  private void pay(TripButton tripButton, LoadingView loading) {
    // 目前只有微信支付
    boolean wxInstall = true;
    if (!SystemUtils.isAppInstalled(getContext(), "com.tencent.mm")) { // 校验微信是否支付
      wxInstall = false;
    }

    boolean zfbInstall = true;
    if (!SystemUtils.checkAliPayInstalled(getContext()) || true) { // 暂时先屏蔽支付宝
      zfbInstall = false;
    }

    boolean flag = wxInstall | zfbInstall;

    if (flag) {
      tripButton.setTripButtonText("");
      loading.setVisibility(View.VISIBLE);
      final String oid = mTaxiOrder.getOrderId();
//    payList(oid, feeInfo.getUnPayMoney() / 100);
      payInfo(oid, feeInfo.getUnPayMoney() / 100);
    } else {
      if (!wxInstall) {
        showUnInstallDlg(getString(R.string.taxi_payment_wechat));
      } else {
        showUnInstallDlg(getString(R.string.taxi_payment_zfb));
      }
    }
  }

  private void showUnInstallDlg(String payApp) {
    final SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext())
        .setTitle(getString(R.string.taxi_support_dlg_title))
        .setMessage(String.format(getString(R.string.taxi_uninstall_pay_app), payApp, payApp))
        .setPositiveButton(getString(R.string.taxi_wait_checkbox_i_know), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mPayDlg.dismiss();
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mPayDlg = builder.create();
    mPayDlg.show(getFragmentManager(), "");
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getStartLat(),mTaxiOrder.getOrderInfo().getStartLng()));
    model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getEndLat(), mTaxiOrder.getOrderInfo().getEndLng()));
    if (mMap.getLinePoints() != null) {
      model.bounds.addAll(mMap.getLinePoints());
    }
  }

  class EditWatch implements TextWatcher {
    private int digits = 2;

    public EditWatch setDigits(int d) {
      digits = d;
      return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

      //删除“.”后面超过2位后的数据
      if (s.toString().contains(".")) {
        if (s.length() - 1 - s.toString().indexOf(".") > digits) {
          s = s.toString().subSequence(0,
              s.toString().indexOf(".") + digits+  1);
          mInputMoney.setText(s);
          mInputMoney.setSelection(s.length()); //光标移到最后
        }
      }
      //如果"."在起始位置,则起始位置自动补0
      if (s.toString().trim().substring(0).equals(".")) {
        s = "0" + s;
        mInputMoney.setText(s);
        mInputMoney.setSelection(2);
      }

      //如果起始位置为0,且第二位跟的不是".",则无法后续输入
      if (s.toString().startsWith("0")
          && s.toString().trim().length() > 1) {
        if (!s.toString().substring(1, 2).equals(".")) {
          mInputMoney.setText(s.subSequence(0, 1));
          mInputMoney.setSelection(1);
          return;
        }
      }

      if (!TextUtils.isEmpty(mInputMoney.getText())) {
        mConfirmPayFee = String.format(getString(R.string.taxi_end_pay_confirm), mInputMoney.getText());
      } else {
        mConfirmPayFee = getString(R.string.taxi_end_pay_options);
      }
      mEndPay.setTripButtonText(mConfirmPayFee);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  class PayHandler extends Handler {

    public PayHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case PAY_INFO_STATUS: {
          mEndPresenter.loopOrderDetail(mTaxiOrder.getOrderId());
          break;
        }
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mEndPresenter.release();
  }
}
