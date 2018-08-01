package com.trip.taxi.cancel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.map.map.MarkerOption;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.cancel.CancelFragment;
import com.trip.base.common.CommonParams.Service;
import com.trip.taxi.R;
import com.trip.taxi.cancel.presenter.TaxiCancelPresenter;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiEvaluate;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderDetail;

public class TaxiCancelFragment extends CancelFragment implements ICancelView {
  private ShapeImageView mDriverHeaderIcon;
  private TaxiOrder mTaxiOrder;
  private TextView mDriverName;
  private TextView mDriverCarNo;
  private TextView mDriverCompany;
  private StarView mDriverStarView;
  private TaxiCancelPresenter mCancelPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISTORY);
      TaxiOrderDetail taxiOrderDetail = mTaxiOrder.getOrderInfo();
    }

    mTopbarView.setTitle(R.string.taxi_cancel_trip_title);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitleRight(0);
    mMap.hideMyLocation();
    mCancelPresenter = new TaxiCancelPresenter(getContext(), this);
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_cancel_trip_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mDriverHeaderIcon = (ShapeImageView) view.findViewById(R.id.taxi_service_driver_icon);
    mDriverName = (TextView) view.findViewById(R.id.taxi_service_driver_name);
    mDriverCarNo = (TextView) view.findViewById(R.id.taxi_service_driver_car_no);
    mDriverCompany = (TextView) view.findViewById(R.id.taxi_service_driver_company);
    mDriverStarView = (StarView) view.findViewById(R.id.taxi_service_driver_star);
    mIM = view.findViewById(R.id.taxi_service_driver_im);
    mPhone = view.findViewById(R.id.taxi_service_driver_call);

    mIM.setVisibility(View.INVISIBLE);
    mPhone.setVisibility(View.INVISIBLE);
    updateDriverCard();

    mCancelPresenter.addMarks(mTaxiOrder);
  }

  @Override
  public void addMarks(MarkerOption start, MarkerOption end) {
    mMap.addMarker(start);
    mMap.addMarker(end);
  }

  private void updateDriverCard() {
    OrderDriver driver = mTaxiOrder.getOrderInfo().getDriver();
    if (driver != null) {
      mDriverHeaderIcon.loadImageByUrl(null, driver.getDriverIcon(), "default");
      mDriverName.setText(driver.getDriverName());
      mDriverCompany.setText(driver.getDriverCompany());
      mDriverCarNo.setText(driver.getDriverCarNo());
      mDriverStarView.setLevel((int) driver.getDriverStar());
    }
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {
    if (mTaxiOrder != null && mTaxiOrder.getOrderInfo() != null) {
      bestView.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getStartLat(), mTaxiOrder.getOrderInfo().getStartLng()));
      bestView.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getEndLat(), mTaxiOrder.getOrderInfo().getEndLng()));
    }
  }

  @Override
  protected void mapClearElement() {
    super.mapClearElement();
    mMap.clearElements();
  }
}
