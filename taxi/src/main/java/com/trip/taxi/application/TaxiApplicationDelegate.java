package com.trip.taxi.application;

import android.app.Application;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.base.ApplicationDelegate;
import com.trip.base.application.BaseApplicationDelegate;

/**
 * Created by ludexiang on 2018/6/5.
 */
@ServiceProvider(value = ApplicationDelegate.class, alias = "taxi")
public class TaxiApplicationDelegate extends BaseApplicationDelegate {

  @Override
  public void onCreate(Application application) {

  }
}
