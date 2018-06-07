package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.map.location.LocationProvider;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.one.provider.DataProvider;
import com.test.demo.R;
import com.trip.base.page.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

@ServiceProvider(value = Fragment.class, alias = "flash")
public class FlashFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_fragment_layout, container, true);
    view.findViewById(R.id.test1).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
//    if (DataProvider.getData("flash") != null) {
//      Object obj = DataProvider.getData("flash");
//      List<MarkerOption> options = (List<MarkerOption>) obj;
//      addMarkers(options);
//    } else {
//      addMarkers();
//    }
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {
    LatLng location = LocationProvider.getInstance().getLocation().mAdrLatLng;
    bestView.zoomCenter = location;
    bestView.bounds.add(location);
  }

  private void addMarkers() {
    List<MarkerOption> options = new ArrayList<MarkerOption>();
    for (int i = 0; i < 1; i++) {
      MarkerOption option = new MarkerOption();
      option.position = new LatLng(39.9491159819, 116.4686983824);
      option.isClickable = true;
      option.descriptor = BitmapDescriptorFactory
          .fromResources(getResources(), R.drawable.flash_map_driver);
      options.add(option);
    }
    mBusContext.getMap().addMarkers(options);
    DataProvider.setData("flash", options);
  }

  private void addMarkers(List<MarkerOption> options) {
    for (int i = 0; i < 1; i++) {
      MarkerOption option = options.get(i);
      option.position = new LatLng(option.position.latitude, option.position.longitude);
      option.isClickable = true;
      option.descriptor = BitmapDescriptorFactory
          .fromResources(getResources(), R.drawable.flash_map_driver);
    }
    mBusContext.getMap().addMarkers(options);
  }
}
