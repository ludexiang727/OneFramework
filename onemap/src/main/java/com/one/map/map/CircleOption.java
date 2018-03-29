package com.one.map.map;

import com.one.map.model.LatLng;

/**
 * Created by challenger on 12/6/2017.
 */

public class CircleOption {

  private int strokeWidth;
  private int strokeColor;
  private LatLng center;
  private int radius;
  private int fillColor;

  public int getStrokeWidth() {
    return strokeWidth;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public int getStrokeColor() {
    return strokeColor;
  }

  public void setStrokeColor(int strokeColor) {
    this.strokeColor = strokeColor;
  }

  public LatLng getCenter() {
    return center;
  }

  public void setCenter(LatLng center) {
    this.center = center;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getFillColor() {
    return fillColor;
  }

  public void setFillColor(int fillColor) {
    this.fillColor = fillColor;
  }
}
