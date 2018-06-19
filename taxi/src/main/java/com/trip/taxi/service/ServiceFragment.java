package com.trip.taxi.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.map.model.BestViewModel;
import com.trip.base.page.BaseFragment;
import com.trip.taxi.R;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.service.presenter.ServicePresenter;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class ServiceFragment extends BaseFragment implements IServiceView, OnClickListener {

  private ServicePresenter mServicePresenter;
  private ShapeImageView mDriverHeaderIcon;
  private ImageView mDriverIM;
  private ImageView mDriverPhone;
  private TextView mDriverName;
  private TextView mDriverCarNo;
  private TextView mDriverCompany;
  private StarView mDriverStarView;
  private TaxiOrder mTaxiOrder;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable("order");
    }
    mServicePresenter = new ServicePresenter(getContext(), mTaxiOrder, this);
    mMap.stopRadarAnim();
    mTopbarView.setTitle(R.string.taxi_service_wait_meet);
    mTopbarView.setLeft(0);
    mTopbarView.setTitleRight(R.string.taxi_service_title_bar_right_more);
  }

  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case RIGHT: {
        break;
      }
    }
  }

  @Override
  public boolean onBackPressed() {
    return true;
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_service_view_layout, container, true);
    initView(view);
    return view;
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.taxi_service_driver_call) {
      Intent intent = new Intent(Intent.ACTION_DIAL,
          Uri.parse("tel:" + mTaxiOrder.getOrderInfo().getDriver().getDriverTel()));
      startActivity(intent);
    }
  }

  private void initView(View view) {
    mDriverHeaderIcon = (ShapeImageView) view.findViewById(R.id.taxi_service_driver_icon);
    mDriverIM = (ImageView) view.findViewById(R.id.taxi_service_driver_im);
    mDriverPhone = (ImageView) view.findViewById(R.id.taxi_service_driver_call);
    mDriverName = (TextView) view.findViewById(R.id.taxi_service_driver_name);
    mDriverCarNo = (TextView) view.findViewById(R.id.taxi_service_driver_car_no);
    mDriverCompany = (TextView) view.findViewById(R.id.taxi_service_driver_company);
    mDriverStarView = (StarView) view.findViewById(R.id.taxi_service_driver_star);

    mDriverPhone.setOnClickListener(this);
  }

  @Override
  public void driverSetOff() {

  }

  @Override
  public void driverReady() {
    mTopbarView.setTitle(R.string.taxi_service_driver_arrived);
  }

  @Override
  public void driverStart() {
    mTopbarView.setTitle(R.string.taxi_service_driver_tripping);
  }

  @Override
  public void driverEnd() {
    mTopbarView.setTitle(R.string.taxi_service_driver_trip_ending);
    mTopbarView.setTitleRight(0);
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mServicePresenter.release();
  }
}
