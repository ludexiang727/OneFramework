package com.one.map.map.element;

import com.one.map.model.LatLng;

public interface ICircle {

  void setCenter(LatLng center);

  void setFillColor(int color);

  void setStroke(int width, int color);

  void setRadius(int length);

  void remove();
}