package com.one.map.location;

import android.os.Bundle;
import android.support.annotation.Keep;
import com.one.map.model.Address;

/**
 * Created by mobike on 2017/11/17.
 */
@Keep
public interface ILocation {

  /**
   * start location
   */
  int onStart();

  /**
   * stop location
   */
  void onStop();

  Address getCurrentLocation();

  @Keep
  interface ILocReceive {

    void onLocReceive(Address adr);
  }

  void setLocListener(ILocReceive locReceive);

  void onSaveInstanceState(Bundle outState);

  void setLocation(Location location);

}
