package com.trip.taxi.cancel.presenter;

import android.content.Context;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.LatLng;
import com.trip.taxi.R;
import com.trip.taxi.cancel.ICancelView;
import com.trip.taxi.net.model.TaxiOrder;

public class TaxiCancelPresenter {
  private ICancelView mView;
  private Context mContext;

  public TaxiCancelPresenter(Context context, ICancelView view) {
    mContext = context;
    mView = view;
  }


  public void addMarks(TaxiOrder order) {
    MarkerOption startOption = new MarkerOption();
    startOption.position = new LatLng(order.getOrderInfo().getStartLat(), order.getOrderInfo().getStartLng());
    startOption.title = order.getOrderInfo().getStartPlaceName();
    startOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_start_icon);

    MarkerOption endOption = new MarkerOption();
    endOption.position = new LatLng(order.getOrderInfo().getEndLat(), order.getOrderInfo().getEndLng());
    endOption.title = order.getOrderInfo().getEndPlaceName();
    endOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_end_icon);

    mView.addMarks(startOption, endOption);
  }

}
