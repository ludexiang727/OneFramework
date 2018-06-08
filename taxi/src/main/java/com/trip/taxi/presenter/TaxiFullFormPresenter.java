package com.trip.taxi.presenter;

import com.one.framework.net.response.IResponseListener;
import com.one.map.log.Logger;
import com.one.map.model.Address;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiEstimatePrice;
import com.trip.taxi.widget.IFullFormView;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class TaxiFullFormPresenter {

  private IFullFormView mFullFormView;

  public TaxiFullFormPresenter(IFullFormView fullFormView) {
    mFullFormView = fullFormView;
  }

  public void taxiEstimatePrice(String marks, long bookingTime, int tip,
      boolean isTick) {
    Address start = FormDataProvider.getInstance().obtainStartAddress();
    Address end = FormDataProvider.getInstance().obtainEndAddress();

    TaxiRequest.taxiEstimatePrice(start, end, marks, bookingTime, tip, isTick,
        new IResponseListener<TaxiEstimatePrice>() {
          @Override
          public void onSuccess(TaxiEstimatePrice taxiEstimatePrice) {
            Logger.e("ldx", "aaaaaaaaaaa onSuccess ..");
          }

          @Override
          public void onFail(TaxiEstimatePrice taxiEstimatePrice) {
            Logger.e("ldx", "aaaaaaaaaaa onFail ..");
          }

          @Override
          public void onFinish(TaxiEstimatePrice taxiEstimatePrice) {
            Logger.e("ldx", "aaaaaaaaaaa onFinish ..");
          }
        });
  }
}
