package com.one.app;

import android.app.Application;
import android.util.Log;
import com.one.framework.app.base.ApplicationDelegate;
import com.one.framework.api.annotation.ServiceProvider;
import com.trip.base.application.BaseApplicationDelegate;

/**
 * Created by ludexiang on 2018/3/27.
 */

@ServiceProvider(value = ApplicationDelegate.class, alias = "one")
public class OneApplicationDelegate extends BaseApplicationDelegate {

  @Override
  public void onCreate(Application application) {
    Log.e("ldx", "OneApplicationDelegate onCreate >>>>>");
  }
}
