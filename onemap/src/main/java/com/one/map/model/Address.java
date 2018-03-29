package com.one.map.model;

/**
 * Created by mobike on 2017/11/17.
 */

public class Address {
  
  /**
   * 地址经纬度
   */
  public LatLng mAdrLatLng;
  
  /**
   * 定位半径
   */
  public float mRadius;
  
  /**
   * 国家 城市 国家码 和 城市码
   */
  public String mCity;
  public String mCityCode;
  public String mCountry;
  public String mCountryCode;
  
  /**
   * 街道及街道码
   */
  public String mStreet;
  public String mStreetCode;
  
  /**
   * 定位返回时间
   */
  public String mServerBackTime;
  
  /**
   * 地址全名称
   */
  public String mAdrFullName;
  
  /**
   * 地址显示名称
   */
  public String mAdrDisplayName;

  /**
   * 旋转角度
   */
  public float bearing;

  public float speed;

  public float accuracy;
  
  @Override
  public String toString() {
    return "Address{" +
        "mAdrLatLng=" + mAdrLatLng +
        ", mRadius=" + mRadius +
        ", mCity='" + mCity + '\'' +
        ", mCityCode='" + mCityCode + '\'' +
        ", mCountry='" + mCountry + '\'' +
        ", mCountryCode='" + mCountryCode + '\'' +
        ", mStreet='" + mStreet + '\'' +
        ", mStreetCode='" + mStreetCode + '\'' +
        ", mServerBackTime='" + mServerBackTime + '\'' +
        ", mAdrFullName='" + mAdrFullName + '\'' +
        ", mAdrDisplayName='" + mAdrDisplayName + '\'' +
        ", bearing=" + bearing +
        ", speed=" + speed +
        '}';
  }
}
