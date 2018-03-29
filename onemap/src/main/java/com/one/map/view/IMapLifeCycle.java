package com.one.map.view;

import android.support.annotation.Keep;

/**
 * Created by mobike on 2017/11/27.
 */

@Keep
public interface IMapLifeCycle {
  
  void onRestart();
  
  void onStart();
  
  void onResume();
  
  void onPause();
  
  void onStop();
  
  void onDestroy();
}
