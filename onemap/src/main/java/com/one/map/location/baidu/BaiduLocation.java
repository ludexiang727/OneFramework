package com.one.map.location.baidu;

/**
 * Created by mobike on 2017/11/27.
 */

public class BaiduLocation {
  //  private Context mContext;
//  private LocationClient mLocationClient;
//  private LocationListener mLocationListener;
//  private ILocReceive mLocReceive;
//
//  private Address mAddress;
//
//  private int mAccuracyCircleFillColor;
//  private int mAccuracyCircleStrokeColor;
//
//  public Location(Context context) {
//    mContext = context.getApplicationContext();
//    mLocationClient = new LocationClient(mContext);
//    mLocationListener = new LocationListener(this);
//    mAccuracyCircleFillColor = Color.parseColor("#AAFFFF88");
//    mAccuracyCircleStrokeColor = Color.parseColor("#AA00FF00");
//    setOperation();
//    onStart();
//  }
//
//  private void setOperation() {
//    LocationClientOption option = new LocationClientOption();
//    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//    option.setScanSpan(5000); // 1s请求一次定位
//    option.setOpenGps(true);
//    option.setIsNeedAddress(true);
//    option.setIsNeedLocationDescribe(true);
//    mLocationClient.setLocOption(option);
//  }
//
//  /**
//   * register location Listener
//   */
//  private void registerLocationListener() {
//    mLocationClient.registerLocationListener(mLocationListener);
//  }
//
//  /**
//   * unregister location listener
//   */
//  private void unregisterLocationListener() {
//    mLocationClient.unRegisterLocationListener(mLocationListener);
//  }
//
//  @Override
//  public void onStart() {
//    registerLocationListener();
//    if (!mLocationClient.isStarted()) {
//      mLocationClient.start();
//    }
//  }
//
//  @Override
//  public void onStop() {
//    if (mLocationClient.isStarted()) {
//      mLocationClient.stop();
//    }
//    unregisterLocationListener();
//  }
//
//  @Override
//  public Object loadLocationConfig() {
//    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.common_map_location_ic);
//    MyLocationConfiguration locationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
//            true, bitmapDescriptor, mAccuracyCircleFillColor, mAccuracyCircleStrokeColor);
//    return locationConfig;
//  }
//
//  @Override
//  public void onReceiveLocation(BDLocation location) {
//    if (mLocReceive == null) {
//      return;
//    }
//    if (location == null || !location.hasAddr()) {
//      mLocReceive.onLocReceive(mAddress);
//      return;
//    }
//    mAddress = new Address();
//    mAddress.mRadius = location.getRadius();
//    mAddress.mCountry = location.getCountry();
//    mAddress.mCountryCode = location.getCountryCode();
//    mAddress.mCity = location.getCity();
//    mAddress.mCityCode = location.getCityCode();
//    mAddress.mAdrFullName = location.getAddrStr();
//    mAddress.mAdrLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//    mAddress.mStreet = location.getStreet();
//    mAddress.mStreetCode = location.getStreetNumber();
//    mAddress.mAdrDisplayName = location.getLocationDescribe();
//    mLocReceive.onLocReceive(mAddress);
//  }

//  @Override
//  public void setLocReceive(ILocReceive receive) {
//    mLocReceive = receive;
//  }
}
