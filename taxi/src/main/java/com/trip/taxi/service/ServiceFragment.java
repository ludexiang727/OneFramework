package com.trip.taxi.service;

import static com.one.framework.app.pop.PopTabItem.CANCEL_ORDER;
import static com.one.framework.app.pop.PopTabItem.CONNECT_SERVICE;
import static com.one.framework.app.pop.PopTabItem.EMERGENCY_CONTACT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.pop.ITabItemClickListener;
import com.one.framework.app.pop.PopTabItem;
import com.one.framework.app.pop.PopType;
import com.one.framework.app.pop.PopUpService;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.map.location.LocationProvider;
import com.one.map.map.MarkerOption;
import com.one.map.map.element.Marker;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams.Service;
import com.trip.base.page.BaseFragment;
import com.trip.taxi.R;
import com.trip.taxi.end.TaxiEndFragment;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.service.presenter.ServicePresenter;
import java.util.ArrayList;
import java.util.List;

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
  private PopUpService mPopService;
  private OrderStatus mCurrentStatus;
  private Marker mDriverMarker;
  private boolean isAddedMark = false;
  private Marker mStartMarker;
  private Marker mEndMarker;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    boolean isFromHistory = false;
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISITORY);
    }
    mMap.removeDriverLine();
    mServicePresenter = new ServicePresenter(getContext(), mTaxiOrder, this);
    mMap.stopRadarAnim();
    mTopbarView.setTitle(R.string.taxi_service_wait_meet);
    mTopbarView.setLeft(isFromHistory ? R.drawable.one_top_bar_back_selector : 0);
    mTopbarView.setTitleRight(R.string.taxi_service_title_bar_right_more);
    mCurrentStatus = OrderStatus.RECEIVED;
  }

  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case LEFT: {
        super.onTitleItemClick(position);
        break;
      }
      case RIGHT: {
        if (mPopService != null && mPopService.isShowing()) {
          mPopService.dismiss();
          return;
        }
        final List<PopTabItem> items = getTabItems();
        mPopService = PopUpService.instance(getActivity(), PopType.WRAP);
        mPopService.setItems(items, new ITabItemClickListener() {
          @Override
          public void onTabClick(int position) {
            PopTabItem popTabItem = items.get(position);
            switch (popTabItem.itemType) {
              case CANCEL_ORDER: {
                break;
              }
              case EMERGENCY_CONTACT: {
                break;
              }
              case CONNECT_SERVICE: {
                break;
              }
            }
          }
        }).showAsDropDown(mTopbarView.getRightView());
        break;
      }
    }
  }

  @NonNull
  private List<PopTabItem> getTabItems() {
    List<PopTabItem> items = new ArrayList<PopTabItem>();
    if (mCurrentStatus == OrderStatus.RECEIVED || mCurrentStatus == OrderStatus.SETOFF || mCurrentStatus == OrderStatus.READY) {
      PopTabItem cancelOrder = new PopTabItem(getString(R.string.taxi_service_cancel_order));
      cancelOrder.itemType = CANCEL_ORDER;
      items.add(cancelOrder);
    } else {
      PopTabItem emergency = new PopTabItem(getString(R.string.taxi_service_emergency_contact));
      emergency.itemType = PopTabItem.EMERGENCY_CONTACT;
//      emergency.itemIcon = R.drawable.taxi_service_sos;
      items.add(emergency);
    }
    PopTabItem im = new PopTabItem(getString(R.string.taxi_service_connect_service));
    im.itemType = PopTabItem.CONNECT_SERVICE;
    items.add(new PopTabItem(getString(R.string.taxi_service_connect_service)));
    return items;
  }

  @Override
  public boolean onBackPressed() {
    return true;
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_driver_view_layout, container, true);
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

    updateDriverCard();
    topBanner();
  }

  private void topBanner() {
    ImageView imageView = new ImageView(getContext());
    imageView.setImageResource(R.drawable.taxi_service_banner);
    imageView.setScaleType(ScaleType.FIT_XY);
    attachToTopContainer(imageView);
  }

  private void updateDriverCard() {
    OrderDriver driver = mTaxiOrder.getOrderInfo().getDriver();
    mDriverHeaderIcon.loadImageByUrl(null, driver.getDriverIcon(), "");
    mDriverName.setText(driver.getDriverName());
    mDriverStarView.setLevel(driver.getDriverStar().intValue());
  }

  @Override
  public void addDriverMarker(Address driverAdr, Address to, MarkerOption driverMarker) {
    if (mDriverMarker == null) {
      mDriverMarker = mMap.addMarker(driverMarker);
    }
    mDriverMarker.setPosition(driverMarker.position);
//    mMap.removeDriverLine();
    mMap.drivingRoutePlan(driverAdr, to);
  }

  @Override
  public void addMarks(MarkerOption start, MarkerOption end) {
    if (mStartMarker == null && mCurrentStatus != OrderStatus.START) {
      mStartMarker = mMap.addMarker(start);
    }

    if (mEndMarker == null) {
      mEndMarker = mMap.addMarker(end);
    }
  }

  @Override
  public void driverReceive(OrderStatus status) {
    serviceCommon(status);
  }

  @Override
  public void driverSetOff(OrderStatus status) {
    serviceCommon(status);
  }

  @Override
  public void driverReady(OrderStatus status) {
    serviceCommon(status);
    toggleMapView();
    mTopbarView.setTitle(R.string.taxi_service_driver_arrived);
  }

  @Override
  public void driverStart(OrderStatus status) {
    mTopbarView.setTitle(R.string.taxi_service_driver_tripping);
    if (mStartMarker != null) {
      mStartMarker.remove();
    }
    serviceCommon(status);
  }

  @Override
  public void driverEnd(OrderStatus status) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(Service.ORDER, mTaxiOrder);
    forward(TaxiEndFragment.class, bundle);
  }

  private void serviceCommon(OrderStatus status) {
    mCurrentStatus = status;
    // 起终点 marker
    mServicePresenter.addMarks(mTaxiOrder);
    toggleMapView();
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    switch (mCurrentStatus) {
      case RECEIVED:
      case SETOFF: {
        model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getStartLat(), mTaxiOrder.getOrderInfo().getStartLng()));
        model.bounds.add( new LatLng(LocationProvider.getInstance().getLocation().mAdrLatLng.latitude,
                LocationProvider.getInstance().getLocation().mAdrLatLng.longitude));
        if (mDriverMarker != null) {
          model.bounds.add(mDriverMarker.getPosition());
        }
        if (mMap.getLinePoints() != null) {
          model.bounds.addAll(mMap.getLinePoints());
        }
        break;
      }
      case READY: {
        model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getStartLat(), mTaxiOrder.getOrderInfo().getStartLng()));
        model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getEndLat(), mTaxiOrder.getOrderInfo().getEndLng()));
        if (mDriverMarker != null) {
          model.bounds.add(mDriverMarker.getPosition());
        }
        break;
      }
      case START: {
        model.bounds.add(new LatLng(mTaxiOrder.getOrderInfo().getEndLat(), mTaxiOrder.getOrderInfo().getEndLng()));
        model.bounds
            .add(new LatLng(LocationProvider.getInstance().getLocation().mAdrLatLng.latitude,
                LocationProvider.getInstance().getLocation().mAdrLatLng.longitude));
        if (mMap.getLinePoints() != null) {
          model.bounds.addAll(mMap.getLinePoints());
        }
        if (mDriverMarker != null) {
          model.bounds.add(mDriverMarker.getPosition());
        }
        mMap.hideMyLocation();
        break;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mServicePresenter.release();
    if (mDriverMarker != null) {
      mDriverMarker.remove();
      mDriverMarker = null;
    }
  }

}
