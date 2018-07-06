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
import com.evaluate.model.DefaultRateDescriptions;
import com.evaluate.model.EvaluateRateTags;
import com.evaluate.model.EvaluateTag;
import com.evaluate.view.EvaluateView;
import com.evaluate.view.IEvaluateView;
import com.evaluate.view.IEvaluateView.EvaluateListener;
import com.evaluate.view.IEvaluateView.Mode;
import com.evaluate.widgets.CardTitleView;
import com.evaluate.widgets.CardTitleView.CardTitleCloseBtnListener;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.map.map.MarkerOption;
import com.one.map.map.element.Marker;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.one.pay.Pay;
import com.one.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.pay.model.PayList;
import com.one.pay.model.PayModel;
import com.trip.base.R;
import com.trip.base.common.CommonParams;
import com.trip.base.net.BaseRequest;
import com.trip.base.net.model.BasePay;
import com.trip.base.net.model.BasePayList;
import com.trip.base.net.model.PayTypeList;
import com.trip.base.page.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/20.
 */

public abstract class EndFragment extends BaseFragment implements IEndView, IPayCallback {

  protected EvaluateComponent mEvaluate;
  protected boolean isFromHistory;

  private Marker mStartMarker;
  private Marker mEndMarker;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEvaluate = new EvaluateComponent();
    Bundle bundle = getArguments();
    if (bundle != null) {
      isFromHistory = bundle.getBoolean(CommonParams.Service.FROM_HISITORY, false);
    }
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
  public boolean onBackPressed() {
    if (!isFromHistory) {
      onBackHome();
      return true;
    }
    return false;
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

  /**
   * 发起支付
   */
  protected void pay(final String oid, final int payFee) {
    BaseRequest.basePayInfo(oid, payFee, new IResponseListener<BasePay>() {
      @Override
      public void onSuccess(BasePay basePay) {
        payList(basePay.getPayId(), oid, payFee);
      }

      @Override
      public void onFail(int errCod, BasePay basePay) {

      }

      @Override
      public void onFinish(BasePay basePay) {

      }
    });
  }

  /**
   * 获取支付列表
   * @param oid
   * @param fee
   */
  protected void payList(final String payId, final String oid, final int fee) {
    BaseRequest.basePayList(UserProfile.getInstance(getContext()).getUserId(), oid, new IResponseListener<BasePayList>() {
      @Override
      public void onSuccess(BasePayList taxiPayList) {
        List<PayList> payLists = new ArrayList<>();
        for (PayTypeList payList : taxiPayList.getPayList()) {
          PayList pay = new PayList(payList.getPayItemIcon(), payList.getPayItemTitle(),
              payList.getPayItemSelected(), payList.getPayItemIconRes(), payList.getPayItemType());
          payLists.add(pay);
        }

        PayModel model = new PayModel(fee/*taxiPayList.getTotalFee()*/, taxiPayList.getFeeDetail(),
            taxiPayList.getVoucherUrl(), payLists);

        Pay.getInstance(getActivity()).showPayBottom(model, EndFragment.this);
      }

      @Override
      public void onFail(int errCode, BasePayList taxiPayList) {

      }

      @Override
      public void onFinish(BasePayList taxiPayList) {

      }
    });
  }

  /**
   * 支付列表切换
   * @param position
   */
  @Override
  public void onPayListSelect(final int position) {
    BaseRequest.basePaySwitch(UserProfile.getInstance(getContext()).getUserId(), getOrderId(), new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        Pay.getInstance(getActivity()).updatePayBottomList(position);
      }

      @Override
      public void onFail(int errCode, BaseObject baseObject) {
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
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
  public void onPaySuccess() {
    handleFinish();
  }

  @Override
  public void onPayFail() {
    handlePayFail();
  }

  public class EvaluateComponent implements EvaluateListener {

    private PopupWindow mPopWindow;
    private IEvaluateView evaluateView;

    class Tag implements EvaluateTag {

      private boolean isSelected;
      private String text;
      private long id;

      public Tag(String text, long id) {
        this.text = text;
        this.id = id;
      }

      @Override
      public String getText() {
        return text;
      }

      @Override
      public long getId() {
        return id;
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

    public EvaluateComponent() {
      evaluateView = new EvaluateView(getActivity());
      evaluateView.setEvaluateListener(this);
      initPop();
    }

    private void testTags() {
      List<EvaluateTag> oneTags = new ArrayList<EvaluateTag>();
      for (int i = 0; i < 6; i++) {
        EvaluateTag tag = new Tag("This is one " + i, i);
        oneTags.add(tag);
      }
      List<EvaluateTag> twoTags = new ArrayList<EvaluateTag>();
      for (int i = 0; i < 6; i++) {
        EvaluateTag tag = new Tag("This is two " + i, i);
        twoTags.add(tag);
      }
      List<EvaluateTag> threeTags = new ArrayList<EvaluateTag>();
      for (int i = 0; i < 6; i++) {
        EvaluateTag tag = new Tag("This is three " + i, i);
        threeTags.add(tag);
      }
      List<EvaluateTag> fourTags = new ArrayList<EvaluateTag>();
      for (int i = 0; i < 6; i++) {
        EvaluateTag tag = new Tag("This is four " + i, i);
        fourTags.add(tag);
      }
      List<EvaluateTag> fiveTags = new ArrayList<EvaluateTag>();
      for (int i = 0; i < 6; i++) {
        EvaluateTag tag = new Tag("This is five " + i, i);
        fiveTags.add(tag);
      }
      EvaluateRateTags one = new RateTags(1, oneTags);
      EvaluateRateTags two = new RateTags(2, twoTags);
      EvaluateRateTags three = new RateTags(3, threeTags);
      EvaluateRateTags four = new RateTags(4, fourTags);
      EvaluateRateTags five = new RateTags(5, fiveTags);
      List<EvaluateRateTags> rateTags = new ArrayList<>();
      rateTags.add(one);
      rateTags.add(two);
      rateTags.add(three);
      rateTags.add(four);
      rateTags.add(five);
      evaluateView.setTagAreaVisibility(true);
      evaluateView.setRateTags(rateTags);
      evaluateView.setRateDescriptions(DefaultRateDescriptions.getRateDescriptionList());
    }

    class RateTags implements EvaluateRateTags {

      int rate;
      List<EvaluateTag> tags = new ArrayList<>();

      public RateTags(int rate, List<EvaluateTag> tags) {
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
      public List<EvaluateTag> getTags() {
        return tags;
      }

      @Override
      public int getTextRes() {
        return 0;
      }
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
      testTags();
    }

    public void onEvaluate() {
      if (mPopWindow != null && !mPopWindow.isShowing()) {
        mPopWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        Animation enter = AnimationUtils.loadAnimation(getContext(), R.anim.one_in_bottom_to_up);
        mPopWindow.getContentView().startAnimation(enter);
      }
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

    @Override
    public void onRateChange(int rate) {

    }

    @Override
    public void onEvaluateTagSelectChange(int rate, @NonNull EvaluateTag tag, boolean select) {

    }

    @Override
    public void onSubmit(int rate, @Nullable List<EvaluateTag> tags, @NonNull String comment) {
      evaluateView.setMode(Mode.View);
      evaluateView.setTags(tags);
      evaluateView.setRate(rate);
      evaluateView.showSubmited();
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
}
