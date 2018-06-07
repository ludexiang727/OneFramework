package com.trip.taxi.widget;

import android.view.View;

/**
 * Created by mobike on 2017/12/12.
 */

public interface IFullFormView {

  View getView();

  void setFormType(int type);

  void showExpand();

  void showCollapse();

  void showFullForm();

  void showLoading(boolean isLoading);

  void showError();

  void updatePriceInfo(String price, String coupon, String discount);

  void setTime(long time);

  void setMoney(int fee);

  void setMsg(String msg);

  interface IFullFormListener {

    void onClick(int tag);

    void onByMeterSelected(boolean isSelected);
  }

  void setFullFormListener(IFullFormListener listener);

  boolean fullFormType();
}