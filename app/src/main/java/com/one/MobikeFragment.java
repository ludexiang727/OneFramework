package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.one.base.BaseFragment;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.map.element.IMarker;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.test.demo.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

@ServiceProvider(value = Fragment.class, alias = "mobike")
public class MobikeFragment extends BaseFragment {

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.mobike_main_layout, container, true);
    view.findViewById(R.id.mobike_start_scan_code).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        forward(FirstTestFragment.class);
      }
    });
    return view;
  }


  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    addMarkers();
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {

    model.bounds.add(new LatLng(39.9481495509, 116.4676630497));
  }

  private void addMarkers() {
    List<MarkerOption> options = new ArrayList<MarkerOption>();
    for (int i = 0; i < 1; i++) {
      MarkerOption option = new MarkerOption();
      option.position = new LatLng(39.9481495509, 116.4676630497);
      option.isClickable = true;
      option.descriptor = BitmapDescriptorFactory
          .fromResources(getResources(), R.drawable.mobike_home_marker_classic);
      options.add(option);
    }
    mBusContext.getMap().addMarkers(options);
  }

  @Override
  public void onMarkerClick(IMarker marker) {
    super.onMarkerClick(marker);
  }
}
