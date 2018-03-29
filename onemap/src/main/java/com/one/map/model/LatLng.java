package com.one.map.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mobike on 2017/11/22.
 */

public final class LatLng implements Parcelable {
  private static final String TAG = LatLng.class.getSimpleName();
  public final double latitude;
  public final double longitude;
  public final double latitudeE6;
  public final double longitudeE6;
  public static final Creator<LatLng> CREATOR = new Creator<LatLng>() {
    @Override
    public LatLng createFromParcel(Parcel parcel) {
      return new LatLng(parcel);
    }
  
    @Override
    public LatLng[] newArray(int i) {
      return new LatLng[i];
    }
  };
  
  public LatLng(double lat, double lng) {
//    if(!Double.isNaN(lat) && !Double.isNaN(lat) && !Double.isInfinite(lng) && !Double.isInfinite(lng)) {
//      double var5 = lat * 1000000.0D;
//      double var7 = lng * 1000000.0D;
//      this.latitudeE6 = var5;
//      this.longitudeE6 = var7;
//      this.latitude = var5 / 1000000.0D;
//      this.longitude = var7 / 1000000.0D;
//    } else {
//      this.latitudeE6 = 0.0D;
//      this.longitudeE6 = 0.0D;
//      this.latitude = 0.0D;
//      this.longitude = 0.0D;
//    }
    if(-180.0D <= lng && lng < 180.0D) {
      this.longitude = lng;
    } else {
      this.longitude = ((lng - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
    }
  
    this.latitude = Math.max(-90.0D, Math.min(90.0D, lat));
  
    this.latitudeE6 = latitude * 1000000.0D;
    this.longitudeE6 = longitude * 1000000.0D;
  }
  
  protected LatLng(Parcel parcel) {
    this.latitude = parcel.readDouble();
    this.longitude = parcel.readDouble();
    this.latitudeE6 = parcel.readDouble();
    this.longitudeE6 = parcel.readDouble();
  }
  
  public String toString() {
    String var1 = new String("latitude: ");
    var1 = var1 + this.latitude;
    var1 = var1 + ", longitude: ";
    var1 = var1 + this.longitude;
    return var1;
  }
  
  public int describeContents() {
    return 0;
  }
  
  public void writeToParcel(Parcel parcel, int var2) {
    parcel.writeDouble(this.latitude);
    parcel.writeDouble(this.longitude);
    parcel.writeDouble(this.latitudeE6);
    parcel.writeDouble(this.longitudeE6);
  }
}
