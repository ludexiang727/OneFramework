package com.one.map.poi.impl;

import android.content.Context;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.one.map.model.Route;
import com.one.map.poi.IMapPoi;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.DrivingParam;
import com.tencent.lbssearch.object.param.RoutePlanningParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.lbssearch.object.result.DrivingResultObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobike on 2017/11/27.
 */

public class TencentMapPoi implements IMapPoi {
  
  private Context mContext;
  
  public TencentMapPoi(Context context) {
    mContext = context;
  }
  
  @Override
  public void reverseGeo(Address adr, final IMapCallback callback) {
    TencentSearch tencentSearch = new TencentSearch(mContext);
    Address2GeoParam param = new Address2GeoParam().address(adr.mAdrFullName).region(adr.mCity);
    tencentSearch.address2geo(param, new HttpResponseListener() {
      @Override
      public void onSuccess(int i, BaseObject baseObject) {
        if (baseObject == null) {
          return;
        }
        Address2GeoResultObject obj = (Address2GeoResultObject) baseObject;
        if (obj != null) {
          Address2GeoResultObject.Address2GeoResult result = obj.result;
          Address adr = new Address();
          adr.mAdrLatLng = new LatLng(result.location.lat, result.location.lng);
          adr.mCity = result.address_components.city;
          adr.mCountry = result.address_components.nation;
          adr.mStreet = result.address_components.street;
          adr.mStreetCode = result.address_components.street_number;
          if (callback != null) {
            callback.callback(adr);
          }
        }
      }
      
      @Override
      public void onFailure(int i, String s, Throwable throwable) {
        if (callback != null) {
          callback.callback(s);
        }
      }
    });
  }
  
  @Override
  public void drivingRoutePlan(Address from, Address to, final IMapCallback callback) {
    TencentSearch tencentSearch = new TencentSearch(mContext);
    final DrivingParam drivingParam = new DrivingParam();
    drivingParam.from(new Location((float) (from.mAdrLatLng.latitude), (float) (from.mAdrLatLng.longitude)));
    drivingParam.to(new Location((float) (to.mAdrLatLng.latitude), (float) (to.mAdrLatLng.longitude)));
//    LEAST_DISTANCE 距离最短
//    LEAST_FEE 省钱
//    LEAST_TIME 较快捷
//    REAL_TRAFFIC 结合路况
    drivingParam.policy(RoutePlanningParam.DrivingPolicy.REAL_TRAFFIC);
    tencentSearch.getDirection(drivingParam, new HttpResponseListener() {
      @Override
      public void onSuccess(int i, BaseObject baseObject) {
        if (baseObject == null) {
          return;
        }
        DrivingResultObject obj = (DrivingResultObject) baseObject;
        DrivingResultObject.Result result = obj.result;
        List<Route> routes = new ArrayList<>();
        for (DrivingResultObject.Route drRoutes : result.routes) {
          Route route = new Route();
          route.direction = drRoutes.direction;
          route.distance = drRoutes.distance;
          route.duration = drRoutes.duration;
          for (Location location : drRoutes.polyline) {
            route.polyLine.add(new LatLng(location.lat, location.lng));
          }
          if (drRoutes.waypoints != null) {
            for (DrivingResultObject.WayPoint wayPoint : drRoutes.waypoints) {
              route.waypoints.put(wayPoint.title, new LatLng(wayPoint.location.lat, wayPoint.location.lng));
            }
          }
          
          routes.add(route);
        }
        
        if (callback != null) {
          callback.callback(routes);
        }
      }
      
      @Override
      public void onFailure(int i, String s, Throwable throwable) {
        if (callback != null) {
          callback.callback(s);
        }
      }
    });
  }
  
}
