package com.one.base;

import com.one.framework.app.base.BizEntranceFragment;
import com.one.framework.app.model.IBusinessContext;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BaseFragment extends BizEntranceFragment {
  private IBusinessContext mBusContext;

  @Override
  public void setBusinessContext(IBusinessContext businessContext) {
    mBusContext = businessContext;
  }
}
