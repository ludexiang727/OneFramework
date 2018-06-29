package com.trip.taxi.end;

import com.trip.base.end.IEndView;
import com.trip.taxi.net.model.TaxiOrderDetail;

/**
 * Created by ludexiang on 2018/6/28.
 */

public interface ITaxiEndView extends IEndView {
  void handlePayInfo(TaxiOrderDetail orderDetail);
}
