package com.one.map.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mobike on 2017/11/22.
 */

public final class LatLngBounds implements Parcelable {
  public final LatLng northeast;
  public final LatLng southwest;
  public static final Creator<LatLngBounds> CREATOR = new Creator<LatLngBounds>() {
    @Override
    public LatLngBounds createFromParcel(Parcel parcel) {
      return new LatLngBounds(parcel);
    }

    @Override
    public LatLngBounds[] newArray(int i) {
      return new LatLngBounds[i];
    }
  };

  LatLngBounds(LatLng ne, LatLng sw) {
    this.northeast = ne;
    this.southwest = sw;
  }

  protected LatLngBounds(Parcel parcel) {
    this.northeast = (LatLng) parcel.readParcelable(
        LatLng.class.getClassLoader());
    this.southwest = (LatLng) parcel.readParcelable(
        LatLng.class.getClassLoader());
  }

  public boolean contains(LatLng latLng) {
    if (latLng == null) {
      return false;
    } else {
      double swLat = this.southwest.latitude;
      double neLat = this.northeast.latitude;
      double swLng = this.southwest.longitude;
      double neLng = this.northeast.longitude;
      double latitude = latLng.latitude;
      double longitude = latLng.longitude;
      return latitude >= swLat && latitude <= neLat && longitude >= swLng && longitude <= neLng;
    }
  }

  public LatLng getCenter() {
    double centerLat = (this.northeast.latitude - this.southwest.latitude) / 2.0D + this.southwest.latitude;
    double centerLng = (this.northeast.longitude - this.southwest.longitude) / 2.0D + this.southwest.longitude;
    LatLng var5 = new LatLng(centerLat, centerLng);
    return var5;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeParcelable(this.northeast, i);
    parcel.writeParcelable(this.southwest, i);
  }

  public String toString() {
    StringBuilder var1 = new StringBuilder();
    var1.append("southwest: ");
    var1.append(this.southwest.latitude);
    var1.append(", ");
    var1.append(this.southwest.longitude);
    var1.append("\n");
    var1.append("northeast: ");
    var1.append(this.northeast.latitude);
    var1.append(", ");
    var1.append(this.northeast.longitude);
    return var1.toString();
  }

  public static final class Builder {
    private double a;
    private double b;
    private double c;
    private double d;
    private boolean e = true;

    public Builder() {
    }

    public LatLngBounds build() {
      LatLng var1 = new LatLng(this.b, this.d);
      LatLng var2 = new LatLng(this.a, this.c);
      return new LatLngBounds(var1, var2);
    }

    public Builder include(LatLng var1) {
      if (var1 == null) {
        return this;
      } else {
        if (this.e) {
          this.e = false;
          this.b = this.a = var1.latitude;
          this.d = this.c = var1.longitude;
        }
        
        double var2 = var1.latitude;
        double var4 = var1.longitude;
        if (var2 < this.a) {
          this.a = var2;
        }
        
        if (var2 > this.b) {
          this.b = var2;
        }
        
        if (var4 < this.c) {
          this.c = var4;
        }
        
        if (var4 > this.d) {
          this.d = var4;
        }
        
        return this;
      }
    }
  }
}
