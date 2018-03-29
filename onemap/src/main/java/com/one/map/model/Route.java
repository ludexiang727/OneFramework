package com.one.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mobike on 2017/11/27.
 */

public class Route {
  /**
   * 路线总体方向
   */
  public String direction;
  /**
   * 路径的距离，单位为米
   */
  public float distance;
  /**
   * 预计所需要的时间
   */
  public float duration;
  /**
   * 途径的路线点
   */
  public List<LatLng> polyLine = new ArrayList<>();
  /**
   * 路线策略 仅当设置需要返回多条路线时返回，且为非必有值
   */
  public List<String> tags;
  /**
   * 设置的途经点
   */
  public Map<String, LatLng> waypoints = new HashMap<String, LatLng>();
}
