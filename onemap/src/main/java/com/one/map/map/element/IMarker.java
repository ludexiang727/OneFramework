package com.one.map.map.element;

import android.os.Bundle;
import com.one.map.anim.Animation;
import java.util.ArrayList;

/**
 * Created by mobike on 2017/11/29.
 * B -> BitmapDescription
 * V -> Marker
 */

public interface IMarker<B, V> extends IMapElements<V> {
  void setZIndex(int zIndex);
  
  int getZIndex();
  
  void setToTop();
  
  Bundle getExtraInfo();
  
  void setExtraInfo(Bundle extraInfo);
  
  void setTitle(String title);
  
  String getTitle();
  
  void setIcon(B icon);
  
  B getIcon();
  
  void setIcons(ArrayList<B> icons);
  
  ArrayList<B> getIcons();
  
  void setPeriod(int period);
  
  void setAnimation(Animation animation);
  
  V getSourceMarker();
  
  V getMarkerInstance();
  
  void setClick(boolean clickable);
  
  boolean isClickable();

  void rotate(float angle);
}
