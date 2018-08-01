package com.trip.base.end;

import com.one.framework.app.common.Status.OrderStatus;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.net.model.EvaluateTags;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/21.
 */

public interface IEndView {
  void addMarks(MarkerOption start, MarkerOption end);
  void handleArrived(OrderStatus status);
  void handleFinish(int payType);
  void handlePayFail();
  void handlePay(OrderStatus status);
  void orderDetailFail();
  void evaluateTags(EvaluateTags tags);
  void evaluateSuccess();
  void endRoutePlan(List<LatLng> driverLines);
  void endRoutePlan(Address from, Address to);
}
