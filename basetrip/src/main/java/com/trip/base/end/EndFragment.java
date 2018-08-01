package com.trip.base.end;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.evaluate.model.DefaultRateDescriptions;
import com.evaluate.model.EvaluateRateTags;
import com.evaluate.model.IEvaluateTag;
import com.evaluate.model.RateDescription;
import com.evaluate.view.EvaluateView;
import com.evaluate.view.IEvaluateView;
import com.evaluate.view.IEvaluateView.EvaluateListener;
import com.evaluate.view.IEvaluateView.Mode;
import com.evaluate.widgets.CardTitleView;
import com.evaluate.widgets.CardTitleView.CardTitleCloseBtnListener;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.ToastUtils;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.map.MarkerOption;
import com.one.map.map.element.Marker;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.one.pay.Pay;
import com.one.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.pay.model.PayInfo;
import com.one.pay.model.PayList;
import com.one.pay.model.PayModel;
import com.trip.base.R;
import com.trip.base.common.CommonParams;
import com.trip.base.net.BaseRequest;
import com.trip.base.net.model.BasePay;
import com.trip.base.net.model.BasePayInfo;
import com.trip.base.net.model.BasePayList;
import com.trip.base.net.model.EvaluateTags;
import com.trip.base.net.model.PayTypeList;
import com.trip.base.net.model.Sign;
import com.trip.base.page.BaseFragment;
import com.trip.base.provider.FormDataProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/20.
 */

public abstract class EndFragment extends BaseFragment implements IEndView, IPayCallback,
    IRoutePlanMsgCallback {

  protected EvaluateComponent mEvaluate;
  protected boolean isFromHistory;

  protected SupportDialogFragment mPayDlg;

  private Marker mStartMarker;
  private Marker mEndMarker;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEvaluate = new EvaluateComponent();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void addMarks(MarkerOption start, MarkerOption end) {
    if (mStartMarker == null) {
      mStartMarker = mMap.addMarker(start);
    }

    if (mEndMarker == null) {
      mEndMarker = mMap.addMarker(end);
    }
  }

  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case LEFT: {
        onBackPressed();
        break;
      }
    }
  }

  @Override
  public boolean onBackPressed() {
    if (mEvaluate != null && mEvaluate.isShowing()) {
      mEvaluate.dismiss();
      return true;
    }
    mapClearElement();
    if (!isFromHistory) {
      FormDataProvider.getInstance().clearData();
      onBackInvoke();
      return true;
    } else {
      finishSelf();
      return true;
    }
  }

  @Override
  public void handleArrived(OrderStatus status) {
    toggleMapView();
  }

  @Override
  public void endRoutePlan(List<LatLng> driverLines) {

  }

  @Override
  public void endRoutePlan(Address from, Address to) {
    mMap.drivingRoutePlan(from, to, Color.LTGRAY, true);
  }

  protected abstract String getOrderId();

  protected abstract void loadEvaluateTags();

  @Override
  public void routePlanPoints(List<LatLng> points) {
    toggleMapView();
  }

  @Override
  public void routePlanMsg(String msg, List<LatLng> points) {

  }

  /**
   * 支付 获取 tradeNo
   */
  protected void payInfo(final String oid, final int payFee) {
    BaseRequest.basePayInfo(oid, payFee, new IResponseListener<BasePayInfo>() {
      @Override
      public void onSuccess(BasePayInfo basePayInfo) {
        // TODO: 2018/7/7 此处应该获取支付列表 暂时不用 默认走微信支付
//        payList(basePayInfo.getPayId(), oid, payFee);
        pay(basePayInfo.getPayId());
      }

      @Override
      public void onFail(int errCod, String message) {
//        try {
//          ToastUtils.toast(getActivity(), getString(R.string.pay_failed));
//        } catch (Exception e) {
//        }
        Toast.makeText(getActivity(), getString(R.string.pay_failed), Toast.LENGTH_SHORT).show();
        handlePayFail();
      }

      @Override
      public void onFinish(BasePayInfo basePayInfo) {

      }
    });
  }

  /**
   * 获取支付列表
   */
  protected void payList(final String payId, final String oid, final int fee) {
    BaseRequest.basePayList(UserProfile.getInstance(getContext()).getUserId(), oid,
        new IResponseListener<BasePayList>() {
          @Override
          public void onSuccess(BasePayList taxiPayList) {
            List<PayList> payLists = new ArrayList<>();
            for (PayTypeList payList : taxiPayList.getPayList()) {
              PayList pay = new PayList(payList.getPayItemIcon(), payList.getPayItemTitle(),
                  payList.getPayItemSelected(), payList.getPayItemIconRes(),
                  payList.getPayItemType());
              payLists.add(pay);
            }

            PayModel model = new PayModel(fee/*taxiPayList.getTotalFee()*/,
                taxiPayList.getFeeDetail(),
                taxiPayList.getVoucherUrl(), payLists);

            Pay.getInstance(getActivity()).showPayBottom(model, EndFragment.this);
          }

          @Override
          public void onFail(int errCode, String message) {

          }

          @Override
          public void onFinish(BasePayList taxiPayList) {

          }
        });
  }

  /**
   * 支付列表切换
   */
  @Override
  public void onPayListSelect(final int position) {
    BaseRequest.basePaySwitch(UserProfile.getInstance(getContext()).getUserId(), getOrderId(),
        new IResponseListener<BaseObject>() {
          @Override
          public void onSuccess(BaseObject baseObject) {
            Pay.getInstance(getActivity()).updatePayBottomList(position);
          }

          @Override
          public void onFail(int errCode, String message) {
          }

          @Override
          public void onFinish(BaseObject baseObject) {

          }
        });
  }

  /**
   * 发起支付 直接支付
   */
  protected void pay(String payId) {
    BaseRequest.basePay(UserProfile.getInstance(getContext()).getUserId(), payId, 2,
        new IResponseListener<BasePay>() {
          @Override
          public void onSuccess(BasePay basePay) {
            if (basePay != null && basePay.getSign() != null) {
              Sign sign = basePay.getSign();
              PayInfo info = new PayInfo(sign.getType(), sign.getMchid(), sign.getPackageName(),
                  sign.getAppId(), sign.getSign(), sign.getPartnerId(), sign.getPrePayId(),
                  sign.getDeviceInfo(), sign.getPayUrl(), sign.getNoncestr(), sign.getTimeStamp(),
                  2);
              Pay.getInstance(getActivity()).pay(info, EndFragment.this);
            }
          }

          @Override
          public void onFail(int errCod, String message) {
            handlePayFail();
          }

          @Override
          public void onFinish(BasePay basePay) {

          }
        });
  }


  /**
   * 提交评价
   */
  protected void submitEvaluate(int rate, @Nullable List<String> tags, @NonNull String comment) {

  }

  @Override
  protected void mapClearElement() {
    if (mStartMarker != null) {
      mStartMarker.remove();
      mStartMarker = null;
    }
    if (mEndMarker != null) {
      mEndMarker.remove();
      mEndMarker = null;
    }
    mMap.removeDriverLine();
    mMap.clearElements();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onPaySuccess() {
    handleFinish(0);
  }

  @Override
  public void onPayFail() {
    handlePayFail();
  }


  @Override
  public void evaluateTags(EvaluateTags tags) {
    if (tags != null) {
      List<EvaluateRateTags> rateTags = new ArrayList<>();
      List<IEvaluateTag> oneTag = new ArrayList<>();
      for (String tag : tags.getEvaluateOne()) {
        oneTag.add(new Tag(tag));
      }
      rateTags.add(new RateTags(1, oneTag));
      List<IEvaluateTag> twoTag = new ArrayList<>();
      for (String tag : tags.getEvaluateTwo()) {
        twoTag.add(new Tag(tag));
      }
      rateTags.add(new RateTags(2, twoTag));
      List<IEvaluateTag> threeTag = new ArrayList<>();
      for (String tag : tags.getEvaluateThree()) {
        threeTag.add(new Tag(tag));
      }
      rateTags.add(new RateTags(3, threeTag));
      List<IEvaluateTag> fourTag = new ArrayList<>();
      for (String tag : tags.getEvaluateFour()) {
        fourTag.add(new Tag(tag));
      }
      rateTags.add(new RateTags(4, fourTag));
      List<IEvaluateTag> fiveTag = new ArrayList<>();
      for (String tag : tags.getEvaluateFive()) {
        fiveTag.add(new Tag(tag));
      }
      rateTags.add(new RateTags(5, fiveTag));
      if (mEvaluate != null) {
        mEvaluate.setTags(rateTags);
      }
    }
  }

  public class EvaluateComponent implements EvaluateListener {

    private PopupWindow mPopWindow;
    private IEvaluateView evaluateView;


    public EvaluateComponent() {
      evaluateView = new EvaluateView(getActivity());
      evaluateView.setEvaluateListener(this);
      initPop();
    }

    private void initPop() {
      mPopWindow = new PopupWindow(evaluateView.getView(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
      mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a0000000")));
      mPopWindow.setOutsideTouchable(true);
      mPopWindow.setAnimationStyle(R.style.PopAlphaAnimation);
      evaluateView.setMode(Mode.Rating);
      evaluateView.hasExtendView(true);
      CardTitleView titleView = new CardTitleView(getContext());
      titleView.setTitle(R.string.base_end_evaluate_title);
      evaluateView.addExtendView(titleView);
      titleView.setCloseIconListener(new CardTitleCloseBtnListener() {
        @Override
        public void onCloseBtnClick() {
          dismiss();
        }
      });
    }

    public void setTags(List<EvaluateRateTags> tags) {
      evaluateView.setTagAreaVisibility(true);
      evaluateView.setRateTags(tags);
      evaluateView.setRateDescriptions(DefaultRateDescriptions.getRateDescriptionList());
    }

    /**
     * 展开评价
     */
    public void onEvaluate() {
      if (mPopWindow != null && !mPopWindow.isShowing()) {
        mPopWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        Animation enter = AnimationUtils.loadAnimation(getContext(), R.anim.one_in_bottom_to_up);
        mPopWindow.getContentView().startAnimation(enter);
      }
    }

    /**
     * 已评价
     */
    public void onEvaluated(int star, List<String> tags, String content) {
      evaluateView.setMode(Mode.View);
      evaluateView.setRate(star);
      List<IEvaluateTag> evaluateTags = new ArrayList<>();
      for (String tag : tags) {
        IEvaluateTag evaluateTag = new Tag(tag);
        evaluateTags.add(evaluateTag);
      }
      evaluateView.setTags(evaluateTags);
      evaluateView.setCommentContent(content);
      RateDescription rateDescription = DefaultRateDescriptions.getRateDescription(star);
      if (rateDescription != null) {
        evaluateView.setRateDescriptionVisibility(true);
        evaluateView.setRateDescription(rateDescription.getTextRes());
      } else {
        evaluateView.setRateDescriptionVisibility(false);
      }
      onEvaluate();
    }

    private void dismiss() {
      if (mPopWindow != null && mPopWindow.isShowing()) {
        Animation exit = AnimationUtils.loadAnimation(getContext(), R.anim.one_out_up_to_bottom);
        mPopWindow.getContentView().startAnimation(exit);
        exit.setAnimationListener(new AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            mPopWindow.dismiss();
          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }
        });

      }
    }

    private boolean isShowing() {
      return mPopWindow != null && mPopWindow.isShowing();
    }

    @Override
    public void onRateChange(int rate) {

    }

    @Override
    public void onEvaluateTagSelectChange(int rate, @NonNull IEvaluateTag tag, boolean select) {

    }

    @Override
    public void onSubmit(int rate, @Nullable List<IEvaluateTag> tags, @NonNull String comment) {
      evaluateView.setMode(Mode.View);
      evaluateView.setTags(tags);
      evaluateView.setRate(rate);
      evaluateView.showSubmited();
      List<String> evaluateTag = new ArrayList<>();
      for (IEvaluateTag tag : tags) {
        evaluateTag.add(tag.getText());
      }
      submitEvaluate(rate, evaluateTag, comment);
    }

    @Override
    public void onLoadData() {

    }

    @Override
    public void onSubmitDisable() {

    }

    @Override
    public void onSwitchToEvaluate() {

    }

    @Override
    public boolean hasEvaluateData() {
      return false;
    }
  }

  class RateTags implements EvaluateRateTags {

    int rate;
    List<IEvaluateTag> tags = new ArrayList<>();

    public RateTags(int rate, List<IEvaluateTag> tags) {
      this.rate = rate;
      this.tags.addAll(tags);
    }

    @Override
    public int getRate() {
      return rate;
    }

    @Override
    public String getText() {
      return null;
    }

    @Override
    public List<IEvaluateTag> getTags() {
      return tags;
    }

    @Override
    public int getTextRes() {
      return 0;
    }
  }

  public class Tag implements IEvaluateTag {

    private boolean isSelected;
    private String text;

    public Tag(String text) {
      this.text = text;
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public long getId() {
      return 0;
    }

    @Override
    public void setSelected(boolean selected) {
      isSelected = selected;
    }

    @Override
    public boolean isSelected() {
      return isSelected;
    }
  }
}
