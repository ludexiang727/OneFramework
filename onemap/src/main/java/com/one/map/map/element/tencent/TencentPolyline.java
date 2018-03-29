package com.one.map.map.element.tencent;

import com.one.map.map.element.IPolyline;
import com.one.map.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobike on 2017/11/30.
 */

public class TencentPolyline implements IPolyline<Polyline> {
  private Polyline mTencentPolyline;
  
  public TencentPolyline(Polyline polyline) {
    mTencentPolyline = polyline;
  }
  
  @Override
  public void setColor(int color) {
    mTencentPolyline.setColor(color);
  }
  
  @Override
  public void remove() {
    mTencentPolyline.remove();
  }
  
  @Override
  public void setPosition(LatLng location) {
  
  }

  @Override
  public List<LatLng> getPoints() {
    List<com.tencent.tencentmap.mapsdk.maps.model.LatLng> tencentPoints = mTencentPolyline.getPoints();
    List<LatLng> points = new ArrayList<LatLng>();
    for (com.tencent.tencentmap.mapsdk.maps.model.LatLng latLng: tencentPoints) {
      points.add(new LatLng(latLng.latitude, latLng.longitude));
    }
    return points;
  }

  @Override
  public LatLng getPosition() {
    return null;
  }
}
