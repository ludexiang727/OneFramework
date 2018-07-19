package com.trip.taxi.service;

import com.one.framework.app.common.Status.OrderStatus;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.trip.taxi.net.model.TaxiOrderCancel;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/15.
 */

public interface IServiceView {
  void addDriverMarker(Address driverAdr, Address to, MarkerOption driverMarker);
  void driverCancel(OrderStatus status);
  void driverReceive(OrderStatus status);
  void driverSetOff(OrderStatus status);
  void driverReady(OrderStatus status);
  void driverStart(OrderStatus status);
  void driverEnd(OrderStatus status);
  void addMarks(MarkerOption start, MarkerOption end);
  void cancelOrderSuccess(TaxiOrderCancel taxiOrderCancel);
}
