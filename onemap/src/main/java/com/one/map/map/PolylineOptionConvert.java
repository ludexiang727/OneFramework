package com.one.map.map;


import com.one.map.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobike on 2017/11/30.
 */

public class PolylineOptionConvert {
  public static com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions convert2TencentPolylineOption(PolylineOption option) {
    com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions options = new com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions();
    options.color(option.mColor);
    options.width(option.mWidth);
    options.arrow(false);
    options.arrowSpacing(30);
    options.borderColor(option.mBorderColor);
    options.borderWidth(option.mBorderWidth);
    List<com.tencent.tencentmap.mapsdk.maps.model.LatLng> points = new ArrayList<com.tencent.tencentmap.mapsdk.maps.model.LatLng>();
    for (LatLng latLng : option.mPoints) {
      points.add(LatLngConvert.convert2TencentLatLng(latLng));
    }
    options.addAll(points);
    return options;
  }
}
