package com.trip.taxi.service.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.ToastUtils;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.R;
import com.trip.taxi.TaxiService;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.net.model.TaxiOrderDriverLocation;
import com.trip.taxi.net.model.TaxiOrderStatus;
import com.trip.taxi.service.IServiceView;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class ServicePresenter {
  private Context mContext;
  private IServiceView mView;
  private LocalBroadcastManager mBroadManager;
  private BroadReceiver mReceiver;
  private TaxiOrder mOrder;
  private OrderStatus mCurrentStatus;
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
    filter.addAction(CommonParams.COMMON_LOOPER_ORDER_STATUS);
    filter.addAction(CommonParams.COMMON_LOOPER_DRIVER_LOCATION);
    mBroadManager.registerReceiver(mReceiver, filter);

    TaxiService.loopOrderStatus(mContext, true, mOrder.getOrderId());
  }

  private class BroadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (CommonParams.COMMON_LOOPER_ORDER_STATUS.equalsIgnoreCase(action)) {
        TaxiOrderStatus orderStatus = (TaxiOrderStatus) intent.getSerializableExtra(CommonParams.COMMON_LOOPER_ORDER);
        handleOrderStatus(orderStatus);
      } else if (CommonParams.COMMON_LOOPER_DRIVER_LOCATION.equals(action)) {
        TaxiOrderDriverLocation driverLocation = (TaxiOrderDriverLocation) intent.getSerializableExtra(
            CommonParams.COMMON_LOOPER_DRIVER);
        updateDriverLocation(driverLocation);
      }
    }
  }

  public void addMarks(TaxiOrder order) {
    MarkerOption startOption = new MarkerOption();
    startOption.position = new LatLng(order.getOrderInfo().getStartLat(), order.getOrderInfo().getStartLng());
    startOption.title = order.getOrderInfo().getStartPlaceName();
    startOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_start_icon);

    MarkerOption endOption = new MarkerOption();
    endOption.position = new LatLng(order.getOrderInfo().getEndLat(), order.getOrderInfo().getEndLng());
    endOption.title = order.getOrderInfo().getEndPlaceName();
    endOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_end_icon);

    mView.addMarks(startOption, endOption);
  }

  private void updateDriverLocation(TaxiOrderDriverLocation driverLocation) {
    MarkerOption driverOption = new MarkerOption();
    driverOption.position = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
    driverOption.rotate = driverLocation.getBearing();
    driverOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.taxi_driver);
    Address driverAdr = new Address();
    driverAdr.mAdrLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
    driverAdr.bearing = driverLocation.getBearing();
    if (mCurrentStatus != null) {
      Address to;
      switch (mCurrentStatus) {
        case RECEIVED:
        case SET_OFF: {
          to = new Address();
          to.mAdrLatLng = new LatLng(mOrder.getOrderInfo().getStartLat(), mOrder.getOrderInfo().getStartLng());
          to.mAdrDisplayName = mOrder.getOrderInfo().getStartPlaceName();
          break;
        }
        default: {
          to = new Address();
          to.mAdrLatLng = new LatLng(mOrder.getOrderInfo().getEndLat(), mOrder.getOrderInfo().getEndLng());
          to.mAdrDisplayName = mOrder.getOrderInfo().getEndPlaceName();
        }
      }
      mView.addDriverMarker(driverAdr, to, driverOption);
    }
  }

  private void handleOrderStatus(TaxiOrderStatus orderStatus) {
    OrderStatus state = OrderStatus.fromStateCode(orderStatus.getStatus());
    if (mCurrentStatus == state) {
      return;
    }
    mCurrentStatus = state;
    switch (state) {
      case CANCELED: {
        mView.driverCancel(mCurrentStatus);
        break;
      }
      case RECEIVED: {
        mView.driverReceive(mCurrentStatus);
        break;
      }
      case SET_OFF: {
        // 司机已出发
        mView.driverSetOff(mCurrentStatus);
        break;
      }
      case READY: {
        // 司机已到达
        mView.driverReady(mCurrentStatus, orderStatus.getDriverReadyTime());
        break;
      }
      case START: {
        // 开始行程
        mView.driverStart(mCurrentStatus);
        break;
      }
      case ARRIVED: {
        // 到达终点
        mView.driverEnd(mCurrentStatus);
        break;
      }
    }
  }

  public void cancelOrder(final boolean isShowFullForm) {
    // userid
    TaxiRequest.taxiCancelOrder(mOrder.getOrderId(), UserProfile.getInstance(mContext).getUserId(),
        "", new IResponseListener<TaxiOrderCancel>() {
          @Override
          public void onSuccess(TaxiOrderCancel taxiOrderCancel) {
            if (!isShowFullForm) {
              FormDataProvider.getInstance().saveEndAddress(null);
              FormDataProvider.getInstance().clearData();
            }
            HomeDataProvider.getInstance().saveOrderDetail(null);
            TaxiService.stopService();
            mView.cancelOrderSuccess(taxiOrderCancel);
          }

          @Override
          public void onFail(int errCode, String message) {
//            try {
//              ToastUtils.toast(mContext, message);
//            } catch (Exception e) {
//            }
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFinish(TaxiOrderCancel taxiOrderCancel) {

          }
        });
  }

  public void release() {
    TaxiService.stopService();
    mBroadManager.unregisterReceiver(mReceiver);
  }
}
