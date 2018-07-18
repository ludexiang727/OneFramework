package com.trip.taxi.presenter;

import android.content.Context;
import com.one.framework.app.login.ILogin;
import com.one.framework.app.login.ILogin.LoginType;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.login.UserProfile.User;
import com.one.framework.net.NetConstant;
import com.one.framework.net.response.IResponseListener;
import com.one.map.log.Logger;
import com.one.map.model.Address;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiEstimatePrice;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.widget.IFullFormView;
import java.text.Normalizer.Form;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class TaxiFullFormPresenter {

  private IFullFormView mFullFormView;
  private Context mContext;

  public TaxiFullFormPresenter(Context context, IFullFormView fullFormView) {
    mFullFormView = fullFormView;
    mContext = context;
  }


  public void taxiEstimatePrice(String marks, boolean isTick) {
    Address start = FormDataProvider.getInstance().obtainStartAddress();
    Address end = FormDataProvider.getInstance().obtainEndAddress();
    long bookingTime = FormDataProvider.getInstance().obtainBookingTime();
    int tip = FormDataProvider.getInstance().obtainTip();

    TaxiRequest.taxiEstimatePrice(start, end, marks, bookingTime, tip, isTick,
        new IResponseListener<TaxiEstimatePrice>() {
          @Override
          public void onSuccess(TaxiEstimatePrice taxiEstimatePrice) {
            mFullFormView.showLoading(false);
            mFullFormView.updatePriceInfo(taxiEstimatePrice.getEstimatePrice(), taxiEstimatePrice.getEstimateCoupin(), taxiEstimatePrice.getEstimateDiscount());
          }

          @Override
          public void onFail(int errCode, TaxiEstimatePrice taxiEstimatePrice) {
            mFullFormView.showLoading(false);
            if (errCode == NetConstant.ADDRESS_EMPTY) {

            } else {
              mFullFormView.estimateFail();
            }
          }

          @Override
          public void onFinish(TaxiEstimatePrice taxiEstimatePrice) {

          }
        });
  }

  /**
   * @param marks 捎话
   * @param isTick 是否达标来接
   */
  public void taxiCreateOrder(String marks, boolean isTick, final int type) {
    if (!UserProfile.getInstance(mContext).isLogin()) {
      UserProfile.getInstance(mContext).getLoginInterface().showLogin(ILogin.DIALOG);
      return;
    }

    Address start = FormDataProvider.getInstance().obtainStartAddress();
    Address end = FormDataProvider.getInstance().obtainEndAddress();
    long bookingTime = FormDataProvider.getInstance().obtainBookingTime();
    int tip = FormDataProvider.getInstance().obtainTip();
    TaxiRequest.taxiCreateOrder(start, end, marks, bookingTime, tip, isTick, type,
        new IResponseListener<TaxiOrder>() {
          @Override
          public void onSuccess(TaxiOrder taxiOrder) {
            mFullFormView.createOrderSuccess(taxiOrder);
          }

          @Override
          public void onFail(int errCode, TaxiOrder taxiOrder) {
            mFullFormView.setFormType(type);
            // unFinished order
            mFullFormView.createOrderFail();
          }

          @Override
          public void onFinish(TaxiOrder taxiOrder) {

          }
        });
  }
}
