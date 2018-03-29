package com.one.map.map.element.tencent;

import android.os.Bundle;
import com.one.map.anim.Animation;
import com.one.map.map.BitmapDescriptor;
import com.one.map.map.BitmapDescriptorConvert;
import com.one.map.map.LatLngConvert;
import com.one.map.map.element.IMarker;
import com.one.map.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import java.util.ArrayList;

/**
 * Created by mobike on 2017/11/29.
 */

public class TencentMarker implements IMarker<BitmapDescriptor, Marker> {
  
  private Marker mTencentMarker;
  private boolean isClickable;
  
  public TencentMarker(Marker marker) {
    mTencentMarker = marker;
  }
  
  @Override
  public void setPosition(LatLng location) {
    mTencentMarker.setPosition(LatLngConvert.convert2TencentLatLng(location));
  }
  
  @Override
  public LatLng getPosition() {
    return null;
  }
  
  @Override
  public void setZIndex(int zIndex) {
  
  }
  
  @Override
  public void remove() {
    mTencentMarker.remove();
  }
  
  @Override
  public Marker getSourceMarker() {
    return mTencentMarker;
  }
  
  @Override
  public int getZIndex() {
    return 0;
  }
  
  @Override
  public Marker getMarkerInstance() {
    return mTencentMarker;
  }
  
  @Override
  public void setToTop() {
  
  }
  
  @Override
  public Bundle getExtraInfo() {
    return null;
  }
  
  @Override
  public void setExtraInfo(Bundle extraInfo) {
  
  }
  
  @Override
  public void setTitle(String title) {
    mTencentMarker.setTitle(title);
  }
  
  @Override
  public String getTitle() {
    return mTencentMarker.getTitle();
  }
  
  @Override
  public void setIcon(BitmapDescriptor icon) {
    mTencentMarker.setIcon(BitmapDescriptorConvert.convert2TencentBitmapDescriptor(icon));
  }
  
  @Override
  public BitmapDescriptor getIcon() {
    return null;
  }
  
  @Override
  public void setIcons(ArrayList<BitmapDescriptor> icons) {
  }
  
  @Override
  public ArrayList<BitmapDescriptor> getIcons() {
    return null;
  }
  
  @Override
  public void setPeriod(int period) {
  
  }
  
  @Override
  public void setAnimation(Animation animation) {
  
  }
  
  @Override
  public void setClick(boolean clickable) {
    isClickable = clickable;
  }
  
  @Override
  public boolean isClickable() {
    return isClickable;
  }

  @Override
  public void rotate(float angle) {
    if (mTencentMarker != null) {
      mTencentMarker.setRotation(angle);
    }
  }
}
