package com.one.map.map.element;

/**
 * Created by mobike on 2017/11/30.
 * M --> IMapView
 */

public interface IInfoWindow extends IMapElements {
  /**
   * info window 上显示的信息
   * @param msg
   */
  void setMessage(CharSequence msg);
  
  void updateMessage(CharSequence msg);
  
  boolean showInfoWindow();
}
