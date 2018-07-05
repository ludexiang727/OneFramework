package com.one.trip.provider;

import com.one.map.model.Address;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ludexiang on 2018/4/17.
 * 业务数据缓存，提供数据 上层存储 不依赖下层
 */

public final class DataProvider {

  /**
   * 缓存个业务线数据，数据封装由业务线做
   */
  private static ConcurrentHashMap<String, Object> mDataProvider = new ConcurrentHashMap<>();

  /**
   * 上车点共享
   */
  private static Address mStartAdr;

  /**
   * 下车点共享
   */
  private static Address mEndAdr;

  private DataProvider() {
  }

  /**
   * 获取数据
   *
   * @param businessKey 业务名称如 taxi、flash、premium 等
   */
  public static Object getData(String businessKey) {
    if (mDataProvider.containsKey(businessKey)) {
      return mDataProvider.get(businessKey);
    }
    return null;
  }

  /**
   * 没有做校验 直接存储
   */
  public static void setData(String businessKey, Object obj) {
    mDataProvider.put(businessKey, obj);
  }

  /**
   * 获取上车点
   */
  public static Address getStartAddress() {
    return mStartAdr;
  }

  public static Address getEndAddress() {
    return mEndAdr;
  }

  public static void setStartAddress(Address startAdr) {
    mStartAdr = startAdr;
  }

  public static void setEndAddress(Address endAdr) {
    mEndAdr = endAdr;
  }
}
