package com.one.base;

import android.app.Application;
import com.one.framework.app.base.ApplicationDelegate;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BaseApplicationDelegate extends ApplicationDelegate {

  @Override
  public abstract void onCreate(Application application);
}
