package com.trip.taxi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.log.Logger;
import com.one.map.location.LocationProvider;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.page.BaseFragment;
import com.trip.taxi.widget.IFormView;
import com.trip.taxi.widget.IFormView.IOnHeightChange;
import com.trip.taxi.widget.impl.FormView;

/**
 * Created by ludexiang on 2018/4/16.
 */

@ServiceProvider(value = Fragment.class, alias = "taxi")
public class TaxiFragment extends BaseFragment implements IOnHeightChange {
  private IFormView mFormView;

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_main_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mFormView = (FormView) view.findViewById(R.id.taxi_form_view);
    mFormView.setOnHeightChange(this);
  }

  @Override
  public void onHeightChange(int height) {
    reLayoutLocationPosition(-height);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Logger.e("ldx", "ssssssss " + -mFormView.getFormView().getMeasuredHeight());
    reLayoutLocationPosition(0);
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    LatLng location = LocationProvider.getInstance().getLocation().mAdrLatLng;
    model.zoomCenter = location;
    model.bounds.add(location);
  }
}
