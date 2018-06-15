package com.trip.taxi.service.presenter;

import android.content.Context;
import com.trip.taxi.service.IServiceView;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class ServicePresenter {
  private IServiceView mView;
  public ServicePresenter(Context context, IServiceView view) {
    mView = view;
  }
}
