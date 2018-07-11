package com.trip.base.wait;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.framework.log.Logger;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.trip.base.R;
import com.trip.base.page.AbsBaseFragment;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.presenter.AbsWaitPresenter;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class WaitFragment extends AbsBaseFragment {

  protected AbsWaitPresenter mWaitPresenter;
  protected Address mStartAdr = FormDataProvider.getInstance().obtainStartAddress();

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mWaitPresenter != null) {
      return mWaitPresenter.getWaitView().getWaitView(container);
    }
    return inflater.inflate(R.layout.base_wait_fragment_layout, container, true);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Logger.e("ldx", "center >>> " + mStartAdr);
    mMap.startRadarAnim(mStartAdr.mAdrLatLng);
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    model.zoomCenter = mStartAdr.mAdrLatLng;
    model.zoomLevel = 18f;
  }

  @Override
  protected void mapClearElement() {
    mMap.stopRadarAnim();
    mMap.clearElements();
  }

}
