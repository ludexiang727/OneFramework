package com.trip.taxi.wait;

import com.trip.base.wait.IWaitView.IClickListener;
import com.trip.taxi.net.model.TaxiOrderCancel;

/**
 * Created by ludexiang on 2018/6/13.
 */

public interface ITaxiWaitView extends IClickListener{
  void updateSweepAngle(float sweepAngle);
  void countDown(int count);
  void cancelOrderSuccess(TaxiOrderCancel orderCancel);
  void onTripping();
}
