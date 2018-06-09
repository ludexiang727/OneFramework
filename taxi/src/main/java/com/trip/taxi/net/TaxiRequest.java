package com.trip.taxi.net;

import android.content.Context;
import android.text.TextUtils;
import com.one.framework.net.Api;
import com.one.framework.net.response.IResponseListener;
import com.one.map.model.Address;
import com.trip.taxi.net.model.TaxiEstimatePrice;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class TaxiRequest {

  private static final String TAXI_ESTIMATE_PRICE_URL = "/api/chariot/trip/taxi/price";

//  private val TAXI_PRICE = "/api/chariot/trip/taxi/price"
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
}
