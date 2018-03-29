package com.one.map.map;

import com.one.map.model.LatLng;

/**
 * Created by mobike on 2017/11/29.
 */

public class MarkerOption {
  /**
   * marker latlng
   */
  public LatLng position;
  
  /**
   * marker bitmap 描述
   */
  public BitmapDescriptor descriptor;
  
  /**
   * marker title
   */
  public String title;
  
  public boolean isClickable = true;
}
