package com.trip.taxi.wait;

import com.trip.base.wait.IWaitView;

/**
 * Created by ludexiang on 2018/6/13.
 */

public interface ITaxiWaitView extends IWaitView {
  void updateSweepAngle(float sweepAngle);
  void countDown(int count);
}
