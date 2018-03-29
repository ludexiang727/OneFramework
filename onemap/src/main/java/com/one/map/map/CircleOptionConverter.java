package com.one.map.map;

/**
 * Created by mobike on 2017/12/4.
 */

public class CircleOptionConverter {
  public static com.tencent.tencentmap.mapsdk.maps.model.CircleOptions convert2TencentCircleOption(CircleOption option) {
    com.tencent.tencentmap.mapsdk.maps.model.CircleOptions circleOptions = new com.tencent.tencentmap.mapsdk.maps.model.CircleOptions();
    
    circleOptions.center(LatLngConvert.convert2TencentLatLng(option.getCenter()));
    circleOptions.radius(option.getRadius());
    circleOptions.fillColor(option.getFillColor());
    circleOptions.strokeColor(option.getStrokeColor());
    circleOptions.strokeWidth(option.getStrokeWidth());
    
    return circleOptions;
  }
}
