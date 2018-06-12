package com.trip.taxi.presenter;

import android.content.Context;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.ITaxiView;
import com.trip.taxi.R;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class TaxiFormPresenter {

  private ITaxiView mView;
  private Context mContext;

  private String[] mTipArray;
  private String[] mMarkArray;

  public TaxiFormPresenter(Context context, ITaxiView view) {
    mContext = context;
    mView = view;
    mTipArray = context.getResources().getStringArray(R.array.TaxiTip);
    mMarkArray = context.getResources().getStringArray(R.array.TaxiMarks);
  }

  public void saveAddress(int type, Address address) {
    if (type == 0) {
      FormDataProvider.getInstance().saveStartAddress(address);
    } else {
      FormDataProvider.getInstance().saveEndAddress(address);
    }
  }

  public List<String> getTipItems() {
    List<String> items = new ArrayList<>();
    for (int i = 0; i < mTipArray.length; i++) {
      if (i == 0) {
        items.add(mTipArray[i]);
      } else {
        items.add(mTipArray[i] + "元");
      }
    }
    return items;
  }

  public List<String> getMarkItems() {
    return Arrays.asList(mMarkArray);
  }

  public int getTip(int selectPosition) {
    if (selectPosition == 0) {
      return 0;
    }
    int tip = Integer.parseInt(mTipArray[selectPosition]);
    FormDataProvider.getInstance().saveTip(tip);
    return tip;
  }

  public void saveBookingTime(long time) {
    FormDataProvider.getInstance().saveBookingTime(time);
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
