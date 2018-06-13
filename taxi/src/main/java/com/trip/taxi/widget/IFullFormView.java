package com.trip.taxi.widget;

import android.view.View;
import com.trip.taxi.net.model.TaxiOrder;

public interface IFullFormView {

  View getView();

  void setFormType(int type);

  void showExpand();

  void showCollapse();

  void showFullForm();

  void showLoading(boolean isLoading);

  void showError();

  void updatePriceInfo(String price, String coupon, String discount);

  void setTime(long time, String showTime);

  void setMoney(int fee);

  void setMsg(String msg);

  void createOrderSuccess(TaxiOrder order);

  interface IFullFormListener {
    void onClick(View view);
  }

  void setFullFormListener(IFullFormListener listener);

  boolean fullFormType();
}
