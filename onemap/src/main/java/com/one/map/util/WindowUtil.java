package com.one.map.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by mobike on 2017/11/30.
 */

public class WindowUtil {
  public static float getScreenDensity() {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    return metrics.density;
  }
}
