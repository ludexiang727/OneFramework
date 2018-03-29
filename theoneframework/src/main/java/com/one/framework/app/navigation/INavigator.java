package com.one.framework.app.navigation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface INavigator {
  Fragment getFragment(Context context, Intent intent);
  void fillPage(Fragment fragment, int contentId);
}
