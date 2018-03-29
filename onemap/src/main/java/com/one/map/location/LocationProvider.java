package com.one.map.location;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import com.one.map.location.ILocation.ILocReceive;
import com.one.map.location.tencent.TencentMapLocation;
import com.one.map.model.Address;
import com.one.map.view.IMapView;
import com.one.map.view.IMapView.MapType;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by mobike on 2017/11/28.
 */
@Keep
public class LocationProvider implements ILocReceive {

  private ILocation locationService;
  private static LocationProvider instance;
  /**
   * 缓存的定位
   */
  private Address currentAddress;

  public static synchronized LocationProvider getInstance() {
    if (instance == null) {
      instance = new LocationProvider();
    }
    return instance;
  }

  public void buildLocation(Context context, @MapType int type) {
    switch (type) {
      case IMapView.TENCENT: {
        locationService = new TencentMapLocation(context);
      }
    }
    if (locationService != null) {
      locationService.setLocListener(this);
    }
  }

  public int start() {
    if (locationService != null) {
      return locationService.onStart();
    }
    return -1;
  }


  private final CopyOnWriteArraySet<OnLocationChangedListener> mListeners = new CopyOnWriteArraySet<>();

  public void addLocationChangeListener(OnLocationChangedListener listener) {
    mListeners.add(listener);
  }

  public void removeLocationChangedListener(OnLocationChangedListener listener) {
    mListeners.remove(listener);
  }

  public Address getLocation() {
    return locationService.getCurrentLocation();
  }

  public void stop() {
    if (locationService != null) {
      locationService.onStop();
    }
  }

  @Override
  public void onLocReceive(Address adr) {
    try {
      for (OnLocationChangedListener listener : mListeners) {
        if (listener != null) {
          listener.onLocationChanged(adr);
        }
      }
    } catch (Exception e) {

    }
  }

  public String getCityCode() {
    Address address = getLocation();
    if (address != null) {
      return CityCodeConverter.transQQ2AmapCityCode(address.mCityCode);
    }
    return "";
  }

  @Keep
  public interface OnLocationChangedListener {

    /**
     * 当获得新位置后，调用此接口, 更新定位点位置
     *
     * @param location 新位置信息
     * @throws IllegalArgumentException xxxxx
     */
    void onLocationChanged(Address location);
  }

  public void onSaveInstanceState(Bundle outState) {
    if (locationService != null) {
      locationService.onSaveInstanceState(outState);
    }
  }

  public void setMochaLocation(Location location) {
    if (locationService != null) {
      locationService.setLocation(location);
    }
  }
}
