package com.trip.base.provider;

import com.one.map.log.Logger;
import com.one.map.model.Address;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class FormDataProvider {

  private Address mStartAddress;
  private Address mEndAddress;

  private long mBookingTime;

  private int mTip = 0;

  private boolean mPay4PickUp;
  private List<String> mMarks = new ArrayList<>();

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
    Logger.e("ldx", "startAddress " + start);
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

  public void savePick4Up(boolean isPay4PickUp) {
    mPay4PickUp = isPay4PickUp;
  }

  public void saveMarks(List<String> marks) {
    marks.clear();
    mMarks.addAll(marks);
  }

  public boolean isPay4PickUp() {
    return mPay4PickUp;
  }

  /**
   * 获取taxi 捎话内容
   * @return
   */
  public List<String> obtainMarks() {
    return mMarks;
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

  /**
   * ServiceFragment EndFragment 点击返回首页时会清空
   * @return
   */
  public void clearData() {
    mStartAddress = null;
    mEndAddress = null;
    mBookingTime = 0;
    mTip = 0;
    mPay4PickUp = false;
    mMarks.clear();
  }
}
