package com.trip.taxi.presenter;

import android.content.Context;
import com.one.map.map.BitmapDescriptor;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.ITaxiView;
import com.trip.taxi.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class TaxiFormPresenter {

  private ITaxiView mView;
  private Context mContext;

  public TaxiFormPresenter(Context context, ITaxiView view) {
    mContext = context;
    mView = view;
  }

  public void saveAddress(int type, Address address) {
    if (type == 0) {
      FormDataProvider.getInstance().saveStartAddress(address);
    } else {
      FormDataProvider.getInstance().saveEndAddress(address);
    }
  }

  public void showEasyForm() {
    FormDataProvider.getInstance().saveEndAddress(null);
  }

  public void checkAddress() {
    FormDataProvider provider = FormDataProvider.getInstance();
    if (provider.obtainStartAddress() != null && provider.obtainEndAddress() == null) {
      // move map
      mView.moveMapToStartAddress(provider.obtainStartAddress());
    } else if (provider.obtainStartAddress() != null && provider.obtainEndAddress() != null) {
      Address start = provider.obtainStartAddress();
      MarkerOption startOption = new MarkerOption();
      startOption.position = start.mAdrLatLng;
      startOption.title = start.mAdrDisplayName;
      startOption.descriptor = BitmapDescriptorFactory
          .fromResources(mContext.getResources(), R.drawable.base_map_start_icon);

      Address end = provider.obtainEndAddress();
      MarkerOption endOption = new MarkerOption();
      endOption.position = end.mAdrLatLng;
      endOption.title = end.mAdrDisplayName;
      endOption.descriptor = BitmapDescriptorFactory
          .fromResources(mContext.getResources(), R.drawable.base_map_end_icon);

      List<MarkerOption> markers = new ArrayList<MarkerOption>();
      markers.add(startOption);
      markers.add(endOption);
      mView.showFullForm(markers);
    }
  }
}
