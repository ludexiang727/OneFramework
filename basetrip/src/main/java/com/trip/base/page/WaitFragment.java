package com.trip.base.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.map.model.BestViewModel;
import com.trip.base.R;
import com.trip.base.wait.presenter.AbsWaitPresenter;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class WaitFragment extends BaseFragment {

  protected AbsWaitPresenter mWaitPresenter;

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mWaitPresenter != null) {
      return mWaitPresenter.getWaitView().getWaitView();
    }
    return inflater.inflate(R.layout.base_wait_fragment_layout, container, true);
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {

  }
}
