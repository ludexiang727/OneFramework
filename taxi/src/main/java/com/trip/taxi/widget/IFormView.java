package com.trip.taxi.widget;

import android.support.annotation.IntDef;
import android.view.View;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mobike on 2017/12/12.
 */

public interface IFormView {

  int EASY_FORM = 1;
  int FULL_FORM = 1 << 1;

  @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({EASY_FORM, FULL_FORM})
  @interface FormType {

  }

  void setFormType(@FormType int type);

  void setStartPoint(String startPoint);

  void setEndPoint(String endPoint);

  void setOptionType(int type);

  int getOptionType();

  void setTime(long time);

  void setTime(String time);

  void setFormListener(IFormListener listener);

  interface IOnHeightChange {
    void onHeightChange(int height);
  }

  void setOnHeightChange(IOnHeightChange onChangeListener);

  interface IFormListener {

    void onStartClick();

    void onEndClick();

    void onTimeClick();

    void onLocationClick();
  }

  interface IAskListener {

    void onPriceClick();

    void onMsgClick();

    void onRetryClick();

    void onByMeter(boolean isSelected);
  }

  void setAskingListener(IAskListener listener);

  void showLoading(boolean isLoading);

  void showError();

  void updateTitle(String price, String coupon, String discount);

  void setMoney(int fee);

  void setMsg(String msg);

  View getFormView();
}
