package com.trip.taxi.widget;

import android.view.View;

public interface IOptionView {
  int NOW = 1;
  int BOOKING = 2;
  
  // default NOW
  int getState();

  void setState(int state);

  interface IOptionChange {
    void onChange();
  }
  
  void setOptionChange(IOptionChange l);

  View getView();
}
