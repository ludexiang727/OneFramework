package com.one.map.factory;

import android.content.Context;
import com.one.map.location.ILocation;
import com.one.map.location.tencent.TencentMapLocation;
import com.one.map.poi.IMapPoi;
import com.one.map.poi.impl.TencentMapPoi;
import com.one.map.view.IMapView;
import com.one.map.view.IMapView.MapType;
import com.one.map.view.impl.MapView;

/**
 * Created by mobike on 2017/11/27.
 */

public class MapFactory {
  private MapFactory() {
  
  }
  
  public static MapFactory newInstance() {
    return Factory.create();
  }
  
  public IMapView getMapView(Context context, @MapType int type) {
    return new MapView(context, type);
  }
  
  public IMapPoi getMapPoi(Context context, @MapType int type) {
    if (type == IMapView.BAIDU) {
      // convert baidu poi
    } else if (type == IMapView.TENCENT) {
      return new TencentMapPoi(context.getApplicationContext());
    }
    return null;
  }
  
  public ILocation getMapLocation(Context context, @MapType int type) {
    if (type == IMapView.BAIDU) {
    
    } else if (type == IMapView.TENCENT) {
      return new TencentMapLocation(context.getApplicationContext());
    }
    return null;
  }
  
  private final static class Factory {
    private static MapFactory S_HOLDER;
    public static MapFactory create() {
      if (S_HOLDER == null) {
        S_HOLDER = new MapFactory();
      }
      return S_HOLDER;
    }
  }
}
