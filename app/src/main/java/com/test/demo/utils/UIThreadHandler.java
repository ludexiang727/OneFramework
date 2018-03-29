package com.test.demo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by mobike on 2017/11/17.
 */

public class UIThreadHandler {
  private static Handler sHandler = new Handler(Looper.getMainLooper());
  public static void post(Runnable run) {
    sHandler.post(run);
  }
  
  public static void postDelayed(Runnable run, long duration) {
    sHandler.postDelayed(run, duration);
  }

  public static void removeCallback(Runnable r) {
    sHandler.removeCallbacks(r);
  }
}
