package com.one.map.map.element;

import com.one.map.model.LatLng;

/**
 * Created by mobike on 2017/11/28.
 * V --> 地图上元素
 */

public interface IMapElements<V> {
  
  void setPosition(LatLng location);
  
  LatLng getPosition();
  
  void remove();
}
