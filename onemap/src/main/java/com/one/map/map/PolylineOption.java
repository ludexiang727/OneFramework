package com.one.map.map;

import com.one.map.model.LatLng;
import java.util.List;

/**
 * Created by mobike on 2017/11/30.
 */

public class PolylineOption {
  /**
   * 折线宽度
   */
  int mWidth;
  
  /**
   * 折线颜色
   */
  int mColor = 0xFF000000;

  /**
   * 描边颜色
   */
  int mBorderColor = 0xFF0000FF;

  /**
   * 描边颜色
   */
  int mBorderWidth;
  /**
   * 折线顶点集
   */
  List<LatLng> mPoints;
  
  /**
   * 设置折形坐标点列表
   *
   * @param points 折形坐标点列表 数目大于2，且不能含有 null
   * @return 该折形选项对象
   */
  public PolylineOption points(List<LatLng> points) {
    this.mPoints = points;
    if (points == null) {
      throw new IllegalArgumentException("points list can not be null");
    }
    if (points.size() < 2) {
      throw new IllegalArgumentException(
              "points count can not less than 2");
    }
    if (points.contains(null)) {
      throw new IllegalArgumentException(
              "points list can not contains null");
    }
    return this;
  }
  
  
  /**
   * 设置折线色
   *
   * @param color 折线颜色
   * @return 该折线选项对象
   */
  public PolylineOption color(int color) {
    this.mColor = color;
    return this;
  }
  
  /**
   * 设置折线宽度
   *
   * @param width 宽度
   * @return 该折线选项对象
   */
  public PolylineOption width(int width) {
    this.mWidth = width;
    return this;
  }

  public PolylineOption border(int color) {
    this.mBorderColor = color;
    return this;
  }

  public PolylineOption borderWidth(int width) {
    this.mBorderWidth = width;
    return this;
  }
}
