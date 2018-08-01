package com.trip.base.net;

import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.trip.base.net.model.BasePay;
import com.trip.base.net.model.BasePayInfo;
import com.trip.base.net.model.BasePayList;
import com.trip.base.net.model.NormalAddress;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/6/24.
 */

public class BaseRequest {
  private static final String BASE_NORMAL_ADR_QUERY = "/api/taxi/address/query";
  private static final String BASE_NORMAL_ADR_UPDATE = "/api/taxi/address/add";
  private static final String BASE_PAY_LIST = "/api/taxi/trip/paylist";
  private static final String BASE_PAY_LIST_SWITCH = "/api/taxi/trip/paylist/switch";
  private static final String BASE_PAY_INFO = "/api/taxi/trip/taxi/pay";
  private static final String BASE_PAY = "/api/taxi/trip/taxi/sign";

  private static int requestCode;

  public static void baseNormalQuery(String token, String cityCode, IResponseListener<NormalAddress> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("cityCode", cityCode);
    params.put("accesstoken", token);
    requestCode = Api.requestGet(BASE_NORMAL_ADR_QUERY, params, listener, NormalAddress.class);
  }

  public static void baseNormalUpdate(String token, String cityCode,
      double latitude, double longitude, int tag, String poiName, String poiDetail, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("cityCode", cityCode);
    params.put("accesstoken", token);
    params.put("latitude", latitude);
    params.put("longitude", longitude);
    params.put("tag", tag);
    params.put("name", poiName);
    params.put("addressDetail", poiDetail);
    requestCode = Api.request(BASE_NORMAL_ADR_UPDATE, params, listener, BaseObject.class);
  }

  /**
   * 获取支付列表
   */
  public static void basePayList(String userId, String oid, IResponseListener<BasePayList> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    requestCode = Api.request(BASE_PAY_LIST, params, listener, BasePayList.class);
  }

  public static void basePaySwitch(String userId, String oid, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", oid);
    params.put("userId", userId);
    requestCode = Api.request(BASE_PAY_LIST_SWITCH, params, listener, BaseObject.class);
  }

  public static void basePayInfo(String orderId, int payFee, IResponseListener<BasePayInfo> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("orderId", orderId);
    params.put("feedback", payFee);
    requestCode = Api.request(BASE_PAY_INFO, params, listener, BasePayInfo.class);
  }

  public static void basePay(String userId, String payId, int channelType, IResponseListener<BasePay> listener) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("ticketId", payId);
    params.put("channelType", channelType);
    params.put("userId", userId);
    requestCode = Api.request(BASE_PAY, params, listener, BasePay.class);
  }
}
