package com.trip.base.wait.presenter;

import com.trip.base.wait.IWaitView;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/13.
 */

public abstract class AbsWaitPresenter {
  public abstract IWaitView getWaitView();

  public void startCountDown() {
  }

  public abstract void stopCountDown();

  public abstract List<String> getTipItems();

  public abstract int getTip(int position);

  public abstract void cancelOrder(boolean isShowFullForm);

  public abstract void addTip(int tip);

  public abstract void pay4Pickup();

  public abstract void release();
}
