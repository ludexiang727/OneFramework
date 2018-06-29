package com.trip.taxi.net;

import android.text.TextUtils;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.map.model.Address;
import com.trip.taxi.net.model.TaxiEstimatePrice;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.net.model.TaxiOrderDetail;
import com.trip.taxi.net.model.TaxiOrderDriverLocation;
import com.trip.taxi.net.model.TaxiOrderStatus;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class TaxiRequest {

  private static final String TAXI_ESTIMATE_PRICE_URL = "/api/chariot/trip/taxi/price";
  private static final String TAXI_ORDER_CREATE_URL = "/api/chariot/trip/taxi/create";
  private static final String TAXI_WAIT_ADD_TIP = "/api/chariot/trip/taxi/thanksfee";
  private static final String TAXI_WAIT_PAY4PICKUP = "/api/chariot/trip/taxi/pay4pickup";
  private static final String TAXI_ORDER_CANCEL = "/api/chariot/trip/cancel";
  private static final String TAXI_REPORT_LOCATION = "/api/chariot/trip/report";
  private static final String TAXI_ORDER_STATUS = "/api/chariot/trip/status";
  private static final String TAXI_ORDER_INFO_DETAIL = "/api/chariot/trip/detail";

  /**
   * 获取司机位置
   */
  private static final String TAXI_DRIVER_LOCATION = "/api/chariot/trip/driver/location";

//  private val TAXI_PAY = "/api/chariot/trip/taxi/pay"
//  private val TAXI_BY_METER = "/api/chariot/trip/taxi/pay4pickup"
//  private val TAXI_THX = "/api/chariot/trip/taxi/thanksfee"
//  private val TAXI_FEEDBACK = "/api/chariot/config/taxi/feedback"
//  private val TAXI_SUBMIT_FEED = "/api/chariot/trip/taxi/feedback"

  private static int requestCode;

  /**
   *
   * @param start
   * @param end
   * @param marks 捎话
   * @param bookingTime 预约时间
   * @param tip 调度费
   * @param isTick 是否打表来接
   */
  public static void taxiEstimatePrice(Address start, Address end, String marks, long bookingTime,
      int tip, boolean isTick, IResponseListener<TaxiEstimatePrice> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("startAdrName", start.mAdrDisplayName);
    params.put("startAdrDetail", start.mAdrFullName);

    params.put("startAdrLat", start.mAdrLatLng.latitude);
    params.put("startAdrLng", start.mAdrLatLng.longitude);

    params.put("endAdrName", end.mAdrDisplayName);
    params.put("endAdrDetail", end.mAdrFullName);
    params.put("endAdrLat", end.mAdrLatLng.latitude);
    params.put("endAdrLng", end.mAdrLatLng.longitude);

    if (bookingTime > 0) {
      params.put("bookTime", bookingTime);
    }

    if (TextUtils.isEmpty(marks)) {
      params.put("marks", marks);
    }

    if (tip > 0) {
      params.put("tip", tip);
    }

    params.put("isTick", isTick);

    requestCode = Api.request(TAXI_ESTIMATE_PRICE_URL, params, listener, TaxiEstimatePrice.class);
  }

  /**
   *
   * @param start
   * @param end
   * @param marks
   * @param bookingTime
   * @param tip
   * @param isTick 是否打表来接
   * @param type 实时订单还是预约订单
   * @param listener
   */
  public static void taxiCreateOrder(Address start, Address end, String marks, long bookingTime,
      int tip, boolean isTick, int type, IResponseListener<TaxiOrder> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", 3);

    params.put("startLat", start.mAdrLatLng.latitude);
    params.put("startLng", start.mAdrLatLng.longitude);

    params.put("endLat", end.mAdrLatLng.latitude);
    params.put("endLng", end.mAdrLatLng.longitude);

    params.put("originName", start.mAdrDisplayName);
    params.put("originDetailAdr", start.mAdrFullName);

    params.put("destinationName", end.mAdrDisplayName);
    params.put("destinationDetailAdr", end.mAdrFullName);


    params.put("bookTime", bookingTime == 0 ? System.currentTimeMillis() : bookingTime);
    params.put("payForPickUp", isTick ? 1 : 0);

    params.put("riderTags", marks);
    params.put("thanksFee", tip);
    params.put("type", type);

    requestCode = Api.request(TAXI_ORDER_CREATE_URL, params, listener, TaxiOrder.class);
  }

  /**
   * 添加感谢费
   * @param oid
   * @param tip
   * @param listener
   */
  public static void taxiAddTip(String oid, int tip, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("thanksFee", tip);
    requestCode = Api.request(TAXI_WAIT_ADD_TIP, params, listener, BaseObject.class);
  }

  /**
   * 打表来接
   * @param oid
   * @param listener
   */
  public static void  taxiWaitPay4PickUp(String oid, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    requestCode = Api.request(TAXI_WAIT_PAY4PICKUP, params, listener, BaseObject.class);
  }

  /**
   * 取消订单
   * @param oid
   * @param userId
   * @param reason
   * @param listener
   */
  public static void taxiCancelOrder(String oid, String userId, String reason, IResponseListener<TaxiOrderCancel> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    params.put("reason", reason);
    params.put("bizType", 3);
    requestCode = Api.request(TAXI_ORDER_CANCEL, params, listener, TaxiOrderCancel.class);
  }

  /**
   * 上报位置
   * @param oid
   * @param address
   * @param listener
   */
  public static void taxiReportLocation(String oid, Address address, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("latitude", address.mAdrLatLng.latitude);
    params.put("longitude", address.mAdrLatLng.longitude);
    params.put("bizType", 3);
    requestCode = Api.request(TAXI_REPORT_LOCATION, params, listener, BaseObject.class);
  }

  /**
   * 轮询订单状态
   */
  public static void taxiLoopOrderStatus(String userId, String oid, IResponseListener<TaxiOrderStatus> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    requestCode = Api.request(TAXI_ORDER_STATUS, params, listener, TaxiOrderStatus.class);
  }

  /**
   * 订单信息
   */
  public static void taxiOrderDetail(String userId, String oid, IResponseListener<TaxiOrderDetail> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    requestCode = Api.request(TAXI_ORDER_INFO_DETAIL, params, listener, TaxiOrderDetail.class);
  }

  /**
   * 司机位置 2s looper
   */
  public static void taxiDriverLocation(String userId, String oid, IResponseListener<TaxiOrderDriverLocation> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    requestCode = Api.request(TAXI_DRIVER_LOCATION, params, listener, TaxiOrderDriverLocation.class);
  }


}
