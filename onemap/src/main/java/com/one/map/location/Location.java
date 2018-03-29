package com.one.map.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
public class Location implements Parcelable {

  public static final String TAG = Location.class.getSimpleName();
  public double latitude;
  public double longitude;
  public float radius;
  public String city;
  public String cityCode;
  public String country;
  public String countryCode;
  public String street;
  public String streetCode;
  public String serverBackTime;
  public String adrFullName;
  public String adrDisplayName;
  public float bearing;
  public float speed;
  public float accuracy;


  public static final Creator<Location> CREATOR = new Creator<Location>() {
    @Override
    public Location createFromParcel(Parcel parcel) {
      return new Location(parcel);
    }

    @Override
    public Location[] newArray(int i) {
      return new Location[i];
    }
  };

  public Location() {

  }

  protected Location(Parcel parcel) {
    this.latitude = parcel.readDouble();
    this.longitude = parcel.readDouble();
    this.radius = parcel.readFloat();
    this.city = parcel.readString();
    this.cityCode = parcel.readString();
    this.country = parcel.readString();
    this.countryCode = parcel.readString();
    this.street = parcel.readString();
    this.streetCode = parcel.readString();
    this.serverBackTime = parcel.readString();
    this.adrFullName = parcel.readString();
    this.adrDisplayName = parcel.readString();
    this.bearing = parcel.readFloat();
    this.speed = parcel.readFloat();
    this.accuracy = parcel.readFloat();
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int var2) {
    parcel.writeDouble(this.latitude);
    parcel.writeDouble(this.longitude);
    parcel.writeFloat(this.radius);
    parcel.writeString(this.city);
    parcel.writeString(this.cityCode);
    parcel.writeString(this.country);
    parcel.writeString(this.countryCode);
    parcel.writeString(this.street);
    parcel.writeString(this.streetCode);
    parcel.writeString(this.serverBackTime);
    parcel.writeString(this.adrFullName);
    parcel.writeString(this.adrDisplayName);
    parcel.writeFloat(this.bearing);
    parcel.writeFloat(this.speed);
    parcel.writeFloat(this.accuracy);
  }

  @Override
  public String toString() {
    return "Location{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", radius=" + radius +
        ", city='" + city + '\'' +
        ", cityCode='" + cityCode + '\'' +
        ", country='" + country + '\'' +
        ", countryCode='" + countryCode + '\'' +
        ", street='" + street + '\'' +
        ", streetCode='" + streetCode + '\'' +
        ", serverBackTime='" + serverBackTime + '\'' +
        ", adrFullName='" + adrFullName + '\'' +
        ", adrDisplayName='" + adrDisplayName + '\'' +
        ", bearing=" + bearing +
        ", speed=" + speed +
        '}';
  }
}
