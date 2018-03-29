package com.one.map.map;

/**
 * Created by mobike on 2017/11/29.
 */

public class MarkerOptionConvert {
  public static com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions convert2TencentMarkerOption(MarkerOption option) {
    if (option == null) {
      return null;
    }
    com.tencent.tencentmap.mapsdk.maps.model.LatLng latLng = LatLngConvert.convert2TencentLatLng(option.position);
    com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions markerOptions = new com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions(latLng);
    com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor bitmapDescriptor;
    if (option.descriptor == null) {
      bitmapDescriptor = com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory.defaultMarker();
    } else {
      bitmapDescriptor = com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory.fromBitmap(option.descriptor.getBitmap());
    }
    return markerOptions.icon(bitmapDescriptor).title(option.title).draggable(false);
  }
}
