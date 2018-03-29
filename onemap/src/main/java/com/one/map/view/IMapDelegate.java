package com.one.map.view;

import android.view.View;
import com.one.map.map.BitmapDescriptor;
import com.one.map.map.CircleOption;
import com.one.map.map.MarkerOption;
import com.one.map.map.PolylineOption;
import com.one.map.map.element.Circle;
import com.one.map.map.element.IMarker;
import com.one.map.map.element.Marker;
import com.one.map.map.element.Polyline;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.one.map.model.MapStatusOperation;

/**
 * Created by mobike on 2017/11/27.
 */

public interface IMapDelegate<MAP> {
  
  /**
   * 获得MapView 实例
   *
   * @return
   */
  View getView();
  
  void setPadding(MapStatusOperation.Padding padding);
  
  /**
   * 是否显示路况
   * @param isShowTraffic
   */
  void setTraffic(boolean isShowTraffic);
  
  /**
   * 是否展示 + - 控制
   */
  void setUIController(boolean isShowUIController);
  
  /**
   * 添加Marker
   */
  Marker addMarker(MarkerOption option);
  
  
  IMarker myLocationConfig(BitmapDescriptor bitmapDescriptor, LatLng position);
  
  /**
   * 是否展示定位
   */
  void setMyLocationEnable(boolean enable);
  
  /**
   * 绘制路线
   * @param
   */
  Polyline addPolyline(PolylineOption option);
  
  Circle addCircle(CircleOption option);
  
  /**
   * 刷新最佳view
   * @param model
   */
  void doBestView(BestViewModel model);
  
  void clearElements();
  
  boolean showInfoWindow(IMarker marker, CharSequence msg);
  void updateInfoWindowMsg(CharSequence msg);
  void removeInfoWindow();
  
  /*** mapview life cycle begin ***/
  void onResume();
  void onStart();
  void onPause();
  void onRestart();
  void onStop();
  void onDestroy();
  /*** mapview life cycle end ***/
  
}
