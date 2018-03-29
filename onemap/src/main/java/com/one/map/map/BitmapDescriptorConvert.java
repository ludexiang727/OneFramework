package com.one.map.map;


/**
 * Created by mobike on 2017/11/29.
 */

public class BitmapDescriptorConvert {

  public static com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor
  convert2TencentBitmapDescriptor(BitmapDescriptor icon) {
    if (icon == null) {
      return null;
    }
    com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor descriptor =
        com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory
            .fromBitmap(icon.getBitmap());
    return descriptor;
  }


}
