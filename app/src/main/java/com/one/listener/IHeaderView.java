package com.one.listener;

import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IHeaderView {
  void onMove(int offsetX, int offsetY);
  void onUp(boolean bottom2Up, boolean isFling);
  int getHeaderHeight();
  View getView();
}