package com.trip.taxi.end.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.ToastUtils;
import com.one.map.location.LocationProvider;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams;
import com.trip.base.net.model.EvaluateTags;
import com.trip.taxi.R;
import com.trip.taxi.TaxiService;
import com.trip.taxi.end.ITaxiEndView;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderDetail;
import com.trip.taxi.net.model.TaxiOrderStatus;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class TaxiEndPresenter {

  private Context mContext;
  private TaxiOrder mOrder;
  private ITaxiEndView mView;
  private BroadReceiver mReceiver;
  private LocalBroadcastManager mBroadManager;
  private OrderStatus mCurrentStatus;

  public TaxiEndPresenter(Context context, TaxiOrder order, ITaxiEndView view) {
    mContext = context;
    mView = view;
    mOrder = order;
    initBroadcast();
  }

  private void initBroadcast() {
    mBroadManager = LocalBroadcastManager.getInstance(mContext);
    mReceiver = new TaxiEndPresenter.BroadReceiver();
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
    Address from = new Address();
    from.mAdrLatLng = new LatLng(order.getOrderInfo().getStartLat(), order.getOrderInfo().getStartLng());
    from.mAdrDisplayName = order.getOrderInfo().getStartPlaceName();
    Address to = new Address();
    to.mAdrLatLng = new LatLng(order.getOrderInfo().getEndLat(), order.getOrderInfo().getEndLng());
    to.mAdrDisplayName = order.getOrderInfo().getEndPlaceName();
    mView.endRoutePlan(from, to);
  }

  private void handleOrderStatus(TaxiOrderStatus taxiOrderStatus) {
    OrderStatus status = OrderStatus.fromStateCode(taxiOrderStatus.getStatus());
    if (mCurrentStatus == status) {
      return;
    }
    mCurrentStatus = status;
    switch (mCurrentStatus) {
      case ARRIVED: {
        mView.handleArrived(mCurrentStatus);
        break;
      }
      case AUTO_PAID:
      case AUTO_PAYING:
      case CONFIRMED_PRICE: {
        // 司机发起支付
        mView.handlePay(mCurrentStatus);
        break;
      }
      case PAID:
      case FINISH:
      case CONFIRM: {
        // 已支付
        mView.handleFinish(taxiOrderStatus.getPayType());
        break;
      }
    }
  }

  public void loopOrderDetail(String oid) {
    TaxiRequest.taxiOrderDetail(UserProfile.getInstance(mContext).getUserId(), oid,
        new IResponseListener<TaxiOrderDetail>() {
          @Override
          public void onSuccess(TaxiOrderDetail taxiOrderDetail) {
            mView.handlePayInfo(taxiOrderDetail);
          }

          @Override
          public void onFail(int errCode, String errMsg) {
//            try {
//              ToastUtils.toast(mContext, errMsg);
//            } catch (Exception e) {
//            }
            Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFinish(TaxiOrderDetail taxiOrderDetail) {
            mView.orderDetailFail();
          }
        });
  }

  /**
   * 获取Evaluate tags
   */
  public void loadEvaluateTags() {
    TaxiRequest.taxiLoadEvaluateTags(LocationProvider.getInstance().getCityCode(),
        new IResponseListener<EvaluateTags>() {
          @Override
          public void onSuccess(EvaluateTags evaluateTags) {
            mView.evaluateTags(evaluateTags);
          }

          @Override
          public void onFail(int errCod, String message) {
//            try {
//              ToastUtils.toast(mContext, message);
//            } catch (Exception e) {
//            }
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFinish(EvaluateTags evaluateTags) {

          }
        });
  }

  public void submitEvaluate(int rate, @Nullable List<String> tags, @NonNull String comment) {
    TaxiRequest.taxiSubmitEvaluate(mOrder.getOrderId(), rate, tags, comment,
        new IResponseListener<BaseObject>() {
          @Override
          public void onSuccess(BaseObject baseObject) {
            mView.evaluateSuccess();
          }

          @Override
          public void onFail(int errCod, String errMsg) {
//            try {
//              ToastUtils.toast(mContext, errMsg);
//            } catch (Exception e) {
//            }
            Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFinish(BaseObject baseObject) {

          }
        });
  }

  public void release() {
    TaxiService.stopService();
    mBroadManager.unregisterReceiver(mReceiver);
  }

}
