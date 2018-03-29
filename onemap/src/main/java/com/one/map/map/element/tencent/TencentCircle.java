package com.one.map.map.element.tencent;

import com.one.map.map.LatLngConvert;
import com.one.map.map.element.ICircle;
import com.one.map.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Circle;

/**
 * Created by mobike on 2017/12/4.
 */

public class TencentCircle implements ICircle {
  private Circle circle;
  
  public TencentCircle(Circle circle) {
    this.circle = circle;
  }
  
  @Override
  public void setCenter(LatLng center) {
    this.circle.setCenter(LatLngConvert.convert2TencentLatLng(center));
  }
  
  @Override
  public void setFillColor(int color) {
    this.circle.setFillColor(color);
  }
  
  @Override
  public void setStroke(int width, int color) {
    this.circle.setStrokeWidth(width);
    this.circle.setStrokeColor(color);
  }
  
  @Override
  public void setRadius(int length) {
    this.circle.setRadius(length);
  }
  
  @Override
  public void remove() {
    this.circle.remove();
  }
}
