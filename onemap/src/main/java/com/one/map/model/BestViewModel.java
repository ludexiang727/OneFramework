package com.one.map.model;

import com.one.map.model.MapStatusOperation.Padding;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobike on 2017/11/22.
 */

public class BestViewModel {
  /**
   * 表单高度变化或出现banner
   */
  public Padding padding = new Padding();

  /**
   * 框LatLng
   */
  public List<LatLng> bounds = new ArrayList<LatLng>();

  /**
   * zoomLevel
   * default zoomLevel 18f
   */
  public float zoomLevel = 18f;

  /**
   * zoomCenter 以当前位置为中心进行zoomLevel
   * @return
   */
  public LatLng zoomCenter;
  
}
