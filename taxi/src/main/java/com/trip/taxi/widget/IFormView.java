package com.trip.taxi.widget;

import android.support.annotation.IntDef;
import android.view.View;
import com.trip.taxi.net.model.TaxiOrder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IFormView {

  int EASY_FORM = 1;
  int FULL_FORM = 1 << 2;

  @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({EASY_FORM, FULL_FORM})
  @interface FormType {

  }

  void setFormType(@FormType int type);

  void setStartPoint(String startPoint);

  void setEndPoint(String endPoint);

  void setOptionType(int type);

  @FormType int getFormType();

  int getOptionType();

  void setTime(long time, String showTime);

  void setFormListener(IFormListener listener);

  interface IOnHeightChange {

    /**
     * height == -1 刷新最佳View
     * @param height
     */
    void onHeightChange(int height);
  }

  void setOnHeightChange(IOnHeightChange onChangeListener);

  interface IFormListener {

    void onStartClick();

    void onEndClick();

    void onTimeClick();

    void onTipClick();

    void onMarkClick(View view);

    void forward(TaxiOrder order);
  }

  void showLoading(boolean isLoading);

  void showError();

  void updateTitle(String price, String coupon, String discount);

  void setMoney(int fee);

  void setMsg(String msg);

  void setPay4PickUp(boolean isPickUp);

  View getFormView();
}
