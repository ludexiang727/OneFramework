package com.trip.base.wait;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ludexiang on 2018/6/13.
 */

public interface IWaitView {
  View getWaitView(ViewGroup container);

  void addTip(int tip);

  void setClickListener(IClickListener listener);

  interface IClickListener {
    void onClick(View view);
  }
}
