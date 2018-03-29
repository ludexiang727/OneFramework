package com.one.map.map.element;

import android.os.Bundle;
import com.one.map.anim.Animation;
import com.one.map.map.BitmapDescriptor;
import com.one.map.model.LatLng;
import java.util.ArrayList;

/**
 * Created by mobike on 2017/11/29.
 */

public class Marker {
  
  private IMarker iMarker;

  public Marker(IMarker m) {
    iMarker = m;
  }

  public void setPosition(LatLng location) {
    iMarker.setPosition(location);
  }

  public LatLng getPosition() {
    return iMarker.getPosition();
  }

  public void setZIndex(int zIndex) {
    iMarker.setZIndex(zIndex);
  }

  public void remove() {
    iMarker.remove();
  }

  public IMarker getSourceMarker() {
    return iMarker;
  }
  
  public int getZIndex() {
    return iMarker.getZIndex();
  }
  
  public Object getMarkerInstance() {
    return null;
  }
  
  public void setToTop() {
    iMarker.setToTop();
  }
  
  public Bundle getExtraInfo() {
    return iMarker.getExtraInfo();
  }
  
  
  public void setExtraInfo(Bundle extraInfo) {
    iMarker.setExtraInfo(extraInfo);
  }
  
  public void setTitle(String title) {
    iMarker.setTitle(title);
  }
  
  public String getTitle() {
    return iMarker.getTitle();
  }
  
  public void setIcon(BitmapDescriptor icon) {
    iMarker.setIcon(icon);
  }
  
  public BitmapDescriptor getIcon() {
    return (BitmapDescriptor) iMarker.getIcon();
  }
  
  public void setIcons(ArrayList icons) {
    
  }
  
  public ArrayList getIcons() {
    return null;
  }
  
  public void setAnimation(Animation animation) {
    iMarker.setAnimation(animation);
  }
  
}
