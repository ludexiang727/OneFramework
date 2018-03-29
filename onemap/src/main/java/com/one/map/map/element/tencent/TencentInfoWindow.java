package com.one.map.map.element.tencent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.one.map.R;
import com.one.map.map.element.IInfoWindow;
import com.one.map.map.element.IMarker;
import com.one.map.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;

/**
 * Created by mobike on 2017/11/30.
 */

public class TencentInfoWindow<MAP, MARKER extends IMarker> implements IInfoWindow {
  private Context mContext;
  private TencentMarker mMarker;
  private CharSequence mMsg;
  private TencentMap mMap;
  private LayoutInflater mInflater;
  private InfoWindowAdapter mInfoWindowAdapter;

  private TencentMap.OnInfoWindowClickListener infoWindowClickListener = new TencentMap.OnInfoWindowClickListener() {
    @Override
    public void onInfoWindowClick(Marker marker) {
//      if (mMarker == null || mMarker.getSourceMarker() != marker) {
//      }
    }

    @Override
    public void onInfoWindowClickLocation(int windowWidth, int windowHigh, int x, int y) {

    }
  };

  public TencentInfoWindow(Context context, MAP map, MARKER marker) {
    mContext = context;
    mMarker = (TencentMarker) marker;
    mMap = (TencentMap) map;
    mInflater = LayoutInflater.from(context);
    mInfoWindowAdapter = new InfoWindowAdapter();
    mMap.setOnInfoWindowClickListener(infoWindowClickListener);
  }
  
  @Override
  public void setMessage(CharSequence msg) {
    mMsg = msg;
  }
  
  
  
  @Override
  public void setPosition(LatLng location) {
  
  }
  
  @Override
  public LatLng getPosition() {
    return null;
  }
  
  @Override
  public void remove() {
    if (mMarker != null && mMarker.getSourceMarker() != null
            && mMarker.getSourceMarker().isInfoWindowShown()) {
      mMarker.getSourceMarker().hideInfoWindow();
    }
  }
  
  @Override
  public boolean showInfoWindow() {
    if (mMarker == null) {
      throw new IllegalArgumentException("Marker is null");
    }
    Marker marker = mMarker.getSourceMarker();
    if (marker != null && !marker.isInfoWindowShown()) {
      marker.setTitle(mMsg.toString());
      marker.showInfoWindow();
    }
    mMap.setInfoWindowAdapter(mInfoWindowAdapter);
    return true;
  }
  
  @Override
  public void updateMessage(CharSequence msg) {
    mMsg = msg;
    if (mMarker != null) {
      Marker marker = mMarker.getSourceMarker();
      if (marker != null) {
        if (marker.isInfoWindowShown()) {
          marker.refreshInfoWindow();
        } else {
          showInfoWindow();
        }
      }
    }
  }
  
  private class InfoWindowAdapter implements TencentMap.InfoWindowAdapter {
    private View view;
    private TextView info;
    
    @Override
    public View getInfoWindow(Marker marker) {
      if (mMarker == null) {
        return null;
      }
      Marker sourceMarker = mMarker.getSourceMarker();
      if (sourceMarker == marker) {
        if (view == null) {
          view = mInflater.inflate(R.layout.info_window_layout, null);
          info = (TextView) view.findViewById(R.id.info_window_msg);
          info.setText(mMsg);
        } else {
          info.setText(mMsg);
        }
        return view;
      }
      return null;
    }
  
    @Override
    public View getInfoContents(Marker marker) {
      if (mMarker != null && marker == mMarker.getSourceMarker()) {
        return view;
      }
      return null;
    }
  }
}
