package com.one.map.poi;

import com.one.map.model.Address;

/**
 * Created by mobike on 2017/11/27.
 */

public interface IMapPoi {
  /**
   * 路径规划
   */
  void drivingRoutePlan(Address from, Address to, IMapCallback callback);
  
  /**
   * 地址反转
   * @param adr
   * @param callback
   */
  void reverseGeo(Address adr, IMapCallback callback);
  
  interface IMapCallback {
    void callback(Object obj);
  }
  
  
}
