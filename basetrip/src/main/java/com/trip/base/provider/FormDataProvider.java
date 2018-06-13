package com.trip.base.provider;

import com.one.map.model.Address;
import com.trip.base.model.IOrder;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class FormDataProvider {

  private Address mStartAddress;
  private Address mEndAddress;

  private IOrder mOrder;

  private long mBookingTime;

  private int mTip = 0;

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

  public int obtainTip() {
    return mTip;
  }

  public void saveTip(int tip) {
    mTip = tip;
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

  public void saveOrder(IOrder order) {
    mOrder = order;
  }

  public IOrder obtainOrder() {
    return mOrder;
  }

  public void clearData() {
    mStartAddress = null;
    mEndAddress = null;
    mBookingTime = 0;
    mTip = 0;
  }
}
