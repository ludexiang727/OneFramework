package com.one.map.tencent;

import android.content.Context;
import android.graphics.Color;
import com.one.map.map.BitmapDescriptor;
import com.one.map.map.BitmapDescriptorConvert;
import com.one.map.map.CircleOption;
import com.one.map.map.CircleOptionConverter;
import com.one.map.map.LatLngConvert;
import com.one.map.map.MarkerOption;
import com.one.map.map.MarkerOptionConvert;
import com.one.map.map.PolylineOption;
import com.one.map.map.PolylineOptionConvert;
import com.one.map.map.element.IInfoWindow;
import com.one.map.map.element.IMarker;
import com.one.map.map.element.IPolyline;
import com.one.map.map.element.tencent.TencentCircle;
import com.one.map.map.element.tencent.TencentInfoWindow;
import com.one.map.map.element.tencent.TencentMarker;
import com.one.map.map.element.tencent.TencentPolyline;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.one.map.model.MapStatusOperation;
import com.one.map.util.MapUtils;
import com.one.map.view.IMapDelegate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.Circle;
import com.tencent.tencentmap.mapsdk.maps.model.CircleOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

/**
 * Created by mobike on 2017/11/27.
 */

public class TencentMapDelegate implements IMapDelegate<TencentMap> {
  
  private Context mContext;
  private MapView mView;
  private TencentMap mTencentMap;
  private Polyline mPolyline;
  private IInfoWindow mTencentInfoWindow;
  private IPolyline mTencentPolyline;
  private SupportMapFragment mMapFragment;
  private Marker mMyMarker;
  
  public TencentMapDelegate(Context context) {
    mContext = context;
    mView = new MapView(mContext);
    mTencentMap = mView.getMap();
    mTencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
    mTencentMap.setLocationSource(new LocationSource() {
      @Override
      public void activate(OnLocationChangedListener onLocationChangedListener) {
      
      }
    
      @Override
      public void deactivate() {
      
      }
    });
  
    mTencentMap.getUiSettings().setScaleViewEnabled(false);
    mTencentMap.getUiSettings().setZoomControlsEnabled(false);
    mTencentMap.getUiSettings().setMyLocationButtonEnabled(false);
  }
  
  @Override
  public MapView getView() {
    return mView;
  }
  
  @Override
  public void setPadding(MapStatusOperation.Padding padding) {
    mTencentMap.setPadding(padding.left, padding.top, padding.right, padding.bottom);
  }
  
  @Override
  public IMarker myLocationConfig(BitmapDescriptor bitmapDescriptor, LatLng latLng) {
    CircleOption circleOption = new CircleOption();
    circleOption.setCenter(latLng);
    circleOption.setFillColor(Color.parseColor("#0f0000ff"));
//      circleOption.setStrokeColor(Color.parseColor("#a0000056"));
//      circleOption.setStrokeWidth(1);
    circleOption.setRadius(50);
    addCircle(circleOption);
    
    // tencent draw Marker
    MarkerOptions op = new MarkerOptions(LatLngConvert.convert2TencentLatLng(latLng))
            .icon(BitmapDescriptorConvert.convert2TencentBitmapDescriptor(bitmapDescriptor))
            .anchor(0.5f, 0.5f);
    mMyMarker = mTencentMap.addMarker(op);
    TencentMarker tencentMarker = new TencentMarker(mMyMarker);
    return tencentMarker;
  }
  
  @Override
  public void setTraffic(boolean isShowTraffic) {
    mTencentMap.setTrafficEnabled(isShowTraffic);
  }
  
  @Override
  public void setUIController(boolean isShowUIController) {
    UiSettings settings = mTencentMap.getUiSettings();
    settings.setZoomControlsEnabled(isShowUIController);
  }
  
  @Override
  public com.one.map.map.element.Marker addMarker(MarkerOption option) {
    final MarkerOptions options = MarkerOptionConvert.convert2TencentMarkerOption(option);
    Marker marker = mTencentMap.addMarker(options);
    final TencentMarker tencentMarker = new TencentMarker(marker);
    tencentMarker.setClick(option.isClickable);
    TencentMap.OnMarkerClickListener clickListener = new TencentMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        return true;
      }
    };
    mTencentMap.setOnMarkerClickListener(clickListener);
    return new com.one.map.map.element.Marker(tencentMarker);
  }
  
  @Override
  public com.one.map.map.element.Circle addCircle(CircleOption options) {
    CircleOptions circleOptions = CircleOptionConverter.convert2TencentCircleOption(options);
    Circle baiduCircle = mTencentMap.addCircle(circleOptions);
    TencentCircle circle = new TencentCircle(baiduCircle);
    return new com.one.map.map.element.Circle(circle);
  }
  
  @Override
  public void setMyLocationEnable(boolean enable) {
    mTencentMap.setMyLocationEnabled(enable);
  }
  
  @Override
  public void doBestView(BestViewModel model) {
    if (model != null) {
      setPadding(model.padding);
      if (model.bounds.size() > 0) {
        moveTo(model);
      } else {
        moveTo(model.zoomCenter, model.zoomLevel);
      }
    }
  }
  
  @Override
  public com.one.map.map.element.Polyline addPolyline(PolylineOption option) {
    PolylineOptions options = PolylineOptionConvert.convert2TencentPolylineOption(option);
    Polyline polyline = mTencentMap.addPolyline(options);
    TencentPolyline tencentPolyline = new TencentPolyline(polyline);
    return new com.one.map.map.element.Polyline(tencentPolyline);
  }
  
  @Override
  public boolean showInfoWindow(IMarker marker, CharSequence msg) {
    mTencentInfoWindow = new TencentInfoWindow(mContext, mTencentMap, marker);
    mTencentInfoWindow.setMessage(msg);
    return mTencentInfoWindow.showInfoWindow();
  }
  
  @Override
  public void updateInfoWindowMsg(CharSequence msg) {
    if (mTencentInfoWindow != null) {
      mTencentInfoWindow.updateMessage(msg);
    }
  }

  @Override
  public void removeInfoWindow() {
    if (mTencentInfoWindow != null) {
      mTencentInfoWindow.remove();
    }
  }
  
  private void moveTo(LatLng latLng, float zoom) {
    if (latLng != null) {
      com.tencent.tencentmap.mapsdk.maps.model.LatLng center = LatLngConvert
          .convert2TencentLatLng(latLng);
      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(center, zoom);
      mTencentMap.animateCamera(cameraUpdate);
    }
  }
  
  private void moveTo(BestViewModel model) {
    CameraUpdate cameraUpdate;
    if (model.zoomCenter != null) {
      /** 以center为中心点,同时确保所有点都在地图可视区域*/
      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (int i = 0; i < model.bounds.size(); i++) {
        builder.include(new com.tencent.tencentmap.mapsdk.maps.model.LatLng(model.bounds.get(i).latitude, model.bounds.get(i).longitude));
      }
      LatLngBounds bounds = builder.build();
      
      com.tencent.tencentmap.mapsdk.maps.model.LatLng sw = bounds.southwest;
      com.tencent.tencentmap.mapsdk.maps.model.LatLng ne = bounds.northeast;
      
      LatLng swSymmetry = MapUtils.getSymmetry(new LatLng(sw.latitude, sw.longitude), model.zoomCenter);
      LatLng neSymmetry = MapUtils.getSymmetry(new LatLng(ne.latitude, ne.longitude), model.zoomCenter);
      double southwestLat = MapUtils.min(sw.latitude, swSymmetry.latitude, ne.latitude, neSymmetry.latitude);
      double southwestLng = MapUtils
          .min(sw.longitude, swSymmetry.longitude, ne.longitude, neSymmetry.longitude);
      double northeastLat = MapUtils.max(sw.latitude, swSymmetry.latitude, ne.latitude, neSymmetry.latitude);
      double northeastLng = MapUtils
          .max(sw.longitude, swSymmetry.longitude, ne.longitude, neSymmetry.longitude);
      
      com.tencent.tencentmap.mapsdk.maps.model.LatLng southwest = new com.tencent.tencentmap.mapsdk.maps.model.LatLng(southwestLat, southwestLng);
      com.tencent.tencentmap.mapsdk.maps.model.LatLng northeast = new com.tencent.tencentmap.mapsdk.maps.model.LatLng(northeastLat, northeastLng);
      LatLngBounds symmetryBounds = new LatLngBounds(southwest, northeast);
      cameraUpdate = CameraUpdateFactory.newLatLngBoundsRect(symmetryBounds, model.padding.left, model.padding.right, model.padding.top, model.padding.bottom);
    } else {
      /** 以一组点几何中心为中心点, 同时确保所有点都在地图可视区域*/
      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (int i = 0; i < model.bounds.size(); i++) {
        builder.include(new com.tencent.tencentmap.mapsdk.maps.model.LatLng(model.bounds.get(i).latitude, model.bounds.get(i).longitude));
      }
      LatLngBounds bounds = builder.build();
      cameraUpdate = CameraUpdateFactory.newLatLngBoundsRect(bounds, model.padding.left, model.padding.right, model.padding.top, model.padding.bottom);
    }
    mTencentMap.animateCamera(cameraUpdate);
  }
  
  @Override
  public void clearElements() {
    mTencentMap.clear();
  }
  
  
  
  /************* life cycle begin **************/
  @Override
  public void onResume() {
    if (mView != null) {
      mView.onResume();
    }
  }
  
  @Override
  public void onPause() {
    if (mView != null) {
      mView.onPause();
    }
  }
  
  @Override
  public void onStart() {
    if (mView != null) {
      mView.onStart();
    }
  }
  
  
  @Override
  public void onStop() {
    if (mView != null) {
      mView.onStop();
    }
  }
  
  @Override
  public void onRestart() {
    if (mView != null) {
      mView.onRestart();
    }
  }
  
  public void onDestroy() {
    if (mView != null) {
      mView.onDestroy();
    }
  }
  /************* life cycle end **************/
}
