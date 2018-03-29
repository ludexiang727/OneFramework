package com.one.map.map.element;

import com.one.map.model.LatLng;

/**
 * Created by challenger on 12/6/2017.
 */

public class Circle {

  private ICircle circle;

  public Circle(ICircle circle) {
    this.circle = circle;
  }

  public void setCenter(LatLng center) {
    this.circle.setCenter(center);
  }

  public void setFillColor(int color) {
    this.circle.setFillColor(color);
  }

  public void setStroke(int width, int color) {
    this.circle.setStroke(width, color);
  }

  public void setRadius(int length) {
    this.circle.setRadius(length);
  }

  public void remove() {
    this.circle.remove();
  }
}
