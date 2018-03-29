package com.one.map.model;

import com.one.map.util.WindowUtil;

/**
 * Created by mobike on 2017/11/28.
 */

public class MapStatusOperation {
  /** 默认的地图padding */
  private static final float ZOOM_PADDING_DEFAULT = 80;
  private Padding deltaPadding;
  
  private MapStatusOperation() {
    deltaPadding = new Padding();
    final int padding = (int) (ZOOM_PADDING_DEFAULT * WindowUtil.getScreenDensity());
    deltaPadding.left = padding;
    deltaPadding.top = padding;
    deltaPadding.right = padding;
    deltaPadding.bottom = padding;
    
  }
  
  public static MapStatusOperation instance() {
    return OperationFactory.HOLDER;
  }
  
  private static final class OperationFactory {
    private static MapStatusOperation HOLDER = new MapStatusOperation();
  }
  
  public static final class Padding {
    public int left;
    public int top;
    public int right;
    public int bottom;
    
    public Padding() {
    
    }
  
    public Padding(Padding src) {
      if (src == null)
        return;
      this.top = src.top;
      this.bottom = src.bottom;
      this.left = src.left;
      this.right = src.right;
    }
  }
  
  public Padding getDeltaPadding() {
    return deltaPadding;
  }
  
  public void setDeltaPadding(Padding deltaPadding) {
    this.deltaPadding = deltaPadding;
  }
  
}
