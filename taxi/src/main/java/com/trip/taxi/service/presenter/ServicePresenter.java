package com.trip.taxi.service.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.trip.taxi.TaxiService;
import com.trip.taxi.common.Common;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderStatus;
import com.trip.taxi.service.IServiceView;
import com.trip.taxi.states.Status.OrderStatus;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class ServicePresenter {
  private Context mContext;
  private IServiceView mView;
  private LocalBroadcastManager mBroadManager;
  private BroadReceiver mReceiver;
  private TaxiOrder mOrder;
  public ServicePresenter(Context context, TaxiOrder order, IServiceView view) {
    mContext = context;
    mView = view;
    mOrder = order;
    initBroadcast();
  }

  private void initBroadcast() {
    mBroadManager = LocalBroadcastManager.getInstance(mContext);
    mReceiver = new BroadReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Common.COMMON_LOOPER_ORDER_STATUS);
    mBroadManager.registerReceiver(mReceiver, filter);

    TaxiService.loopOrderStatus(mContext, true, mOrder.getOrderId());
  }

  private class BroadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (Common.COMMON_LOOPER_ORDER_STATUS.equalsIgnoreCase(action)) {
        TaxiOrderStatus orderStatus = (TaxiOrderStatus) intent.getSerializableExtra(Common.COMMON_LOOPER_ORDER);
        handleOrderStatus(OrderStatus.fromStateCode(orderStatus.getStatus()));
      }
    }
  }

  private void handleOrderStatus(OrderStatus state) {
    switch (state) {
      case SETOFF: {
        // 司机已出发
        mView.driverSetOff();
        break;
      }
      case READY: {
        // 司机已到达
        mView.driverReady();
        break;
      }
      case START: {
        // 开始行程
        mView.driverStart();
        break;
      }
      case ARRIVED: {
        // 到达终点
        mView.driverEnd();
        break;
      }
    }
  }

  public void release() {
    mBroadManager.unregisterReceiver(mReceiver);
  }
}
