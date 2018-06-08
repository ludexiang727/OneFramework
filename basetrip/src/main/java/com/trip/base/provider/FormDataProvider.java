package com.trip.base.provider;

import com.one.map.model.Address;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class FormDataProvider {

  private Address mStartAddress;
  private Address mEndAddress;

  private long mBookingTime;

  private FormDataProvider() {

  }

  private static final class FormDataFactory {
    private static FormDataProvider sProvider;
    public static FormDataProvider getInstance() {
      if (sProvider == null) {
        sProvider = new FormDataProvider();
      }
      return sProvider;
    }
  }

  public static FormDataProvider getInstance() {
    return FormDataFactory.getInstance();
  }

  public void saveStartAddress(Address start) {
    mStartAddress = start;
  }

  public void saveEndAddress(Address end) {
    mEndAddress = end;
  }

  public void saveBookingTime(long time) {
    mBookingTime = time;
  }

  public Address obtainStartAddress() {
    return mStartAddress;
  }

  public Address obtainEndAddress() {
    return mEndAddress;
  }

  public long obtainBookingTime() {
    return  mBookingTime;
  }
}
