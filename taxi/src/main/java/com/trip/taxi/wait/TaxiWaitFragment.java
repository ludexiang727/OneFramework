package com.trip.taxi.wait;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.trip.base.page.WaitFragment;
import com.trip.taxi.wait.presenter.TaxiWaitPresenter;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitFragment extends WaitFragment {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mWaitPresenter = new TaxiWaitPresenter(getContext());
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mWaitPresenter.startCountDown();
  }
}
