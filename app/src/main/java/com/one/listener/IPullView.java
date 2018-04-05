package com.one.listener;

import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IPullView {

  /**
   * 获取滚动的高度
   * @return
   */
  int getScrollingY();
  View getView();
}
