package com.trip.taxi.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.map.model.BestViewModel;
import com.trip.base.page.BaseFragment;
import com.trip.taxi.R;
import com.trip.taxi.service.presenter.ServicePresenter;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class ServiceFragment extends BaseFragment implements IServiceView {

  private ServicePresenter mServicePresenter;
  private ShapeImageView mDriverHeaderIcon;
  private ImageView mDriverIM;
  private ImageView mDriverPhone;
  private TextView mDriverName;
  private TextView mDriverCarNo;
  private TextView mDriverCompany;
  private StarView mDriverStarView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mServicePresenter = new ServicePresenter(getContext(), this);
    mTopbarView.setTitle(R.string.taxi_wait_page_title);
    mTopbarView.setLeft(0);
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_service_view_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mDriverHeaderIcon = (ShapeImageView) view.findViewById(R.id.taxi_service_driver_icon);
    mDriverIM = (ImageView) view.findViewById(R.id.taxi_service_driver_im);
    mDriverPhone = (ImageView) view.findViewById(R.id.taxi_service_driver_call);
    mDriverName = (TextView) view.findViewById(R.id.taxi_service_driver_name);
    mDriverCarNo = (TextView) view.findViewById(R.id.taxi_service_driver_car_no);
    mDriverCompany = (TextView) view.findViewById(R.id.taxi_service_driver_company);
    mDriverStarView = (StarView) view.findViewById(R.id.taxi_service_driver_star);
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {

  }
}
