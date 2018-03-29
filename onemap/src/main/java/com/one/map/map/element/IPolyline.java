package com.one.map.map.element;

import com.one.map.model.LatLng;
import java.util.List;

/**
 * Created by mobike on 2017/11/30.
 */

public interface IPolyline<V> extends IMapElements<V> {
  void setColor(int color);
  List<LatLng> getPoints();
  void remove();
}
