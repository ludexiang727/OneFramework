package com.trip.taxi.widget;

import android.support.annotation.Keep;
import android.view.View;

@Keep
public interface IOptionView {
  int NOW = 1;
  int BOOKING = 2;
  
  // default NOW
  int getState();

  void setState(int state);

  @Keep
  interface IOptionChange {
    void onChange();
  }
  
  void setOptionChange(IOptionChange l);

  View getView();
}
