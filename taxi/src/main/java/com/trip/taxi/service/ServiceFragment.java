package com.trip.taxi.service;

import static com.one.framework.app.pop.PopTabItem.CANCEL_ORDER;
import static com.one.framework.app.pop.PopTabItem.CONNECT_SERVICE;
import static com.one.framework.app.pop.PopTabItem.EMERGENCY_CONTACT;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.one.framework.utils.SystemUtils;
import com.one.framework.utils.TimeUtils;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.location.LocationProvider;
import com.one.map.log.Logger;
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

public class ServiceFragment extends BaseFragment implements IServiceView, IRoutePlanMsgCallback, OnClickListener {
  private static final int FORMAT_COLOR = Color.parseColor("#f05b48");
  private static final int UPDATE_INFO_WINDOW = 0x110;
  private static final int ONE_HOUR = 60 * 60 * 1000;
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
  private boolean infoWindowShowing;
  private long currentTime;
  private long backgroundSystemTime;// 后台系统时间
  private long interval;

  private boolean isStartTrip;

  private Handler mHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case UPDATE_INFO_WINDOW: {
          if (isAdded() && isVisible()) {
            CharSequence infoMsg = createInfoWindowTime();
            mMap.updateInfoWindowMsg(infoMsg);
            if (!isStartTrip) {
              updateInfoWindow();
            } else {
              mHandler.removeCallbacksAndMessages(null);
            }
          }
          break;
        }
      }
    }
  };

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
    mMap.stopRadarAnim();
    mMap.registerPlanCallback(this);
    mTopbarView.setTitle(R.string.taxi_service_wait_meet);
    mTopbarView.setLeft(isFromHistory ? R.drawable.one_top_bar_back_selector : 0);
    mTopbarView.setTitleRight(R.string.taxi_service_title_bar_right_more);
    mCurrentStatus = OrderStatus.RECEIVED;
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_driver_view_layout, container, true);
    initView(view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mServicePresenter = new ServicePresenter(getContext(), mTaxiOrder, this);
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
  public void routePlanPoints(List<LatLng> points) {
    toggleMapView();
  }

  @Override
  public void routePlanMsg(String msg, List<LatLng> points) {
    switch (mCurrentStatus) {
      case RECEIVED:
      case SETOFF: {
        CharSequence infoMsg = UIUtils.highlight(msg, FORMAT_COLOR);
        // 在起始点上展示info window
        if (!infoWindowShowing) {
          infoWindowShowing = mMap.showInfoWindow(mDriverMarker.getSourceMarker(), infoMsg);
        } else {
          mMap.updateInfoWindowMsg(infoMsg);
        }
        break;
      }
      case READY: {
        if (currentTime == 0) {
          CharSequence infoMsg = createInfoWindowTime();
          mMap.showInfoWindow(mDriverMarker.getSourceMarker(), infoMsg);
          updateInfoWindow();
        }
        break;
      }
      case START: {
        isStartTrip = true;
        CharSequence infoMsg = UIUtils.highlight(msg, FORMAT_COLOR);
        // 在起始点上展示info window
        if (!infoWindowShowing) {
          infoWindowShowing = mMap.showInfoWindow(mDriverMarker.getSourceMarker(), infoMsg);
        } else {
          mMap.updateInfoWindowMsg(infoMsg);
        }
        break;
      }
    }
  }

  /**
   * 等待乘客上车 更新infoWindow
   */
  private void updateInfoWindow() {
    Message message = new Message();
    message.obj = System.currentTimeMillis();
    message.what = UPDATE_INFO_WINDOW;
    // 去掉delay 因为获取ReceiveOrder curSystemTime 是2s轮询
    mHandler.sendMessageDelayed(message, currentTime == 0 ? 0 : 1000);
  }

  private CharSequence createInfoWindowTime() {
    if (currentTime == 0) {
      currentTime = System.currentTimeMillis();
      // 后台系统时间
      backgroundSystemTime = mTaxiOrder.getCurrentServerTime();
    } else {
      interval += 1000;
    }
    long waitTime = backgroundSystemTime - currentTime + interval;
    String formatTime = TimeUtils.longToString(waitTime, waitTime > ONE_HOUR ? "HH:mm:ss" : "mm:ss");
    String time = String.format(getContext().getString(R.string.taxi_service_driver_wait_time), formatTime);
    return UIUtils.highlight(time, FORMAT_COLOR);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.taxi_service_driver_call) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTaxiOrder.getOrderInfo().getDriver().getDriverTel()));
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
    mDriverCompany.setText(driver.getDriverCompany());
    mDriverStarView.setLevel((int) driver.getDriverStar());
  }

  @Override
  public void addDriverMarker(Address driverAdr, Address to, MarkerOption driverMarker) {
    if (mDriverMarker == null) {
      mDriverMarker = mMap.addMarker(driverMarker);
    }
    mDriverMarker.rotate(driverMarker.rotate);
    mDriverMarker.setPosition(driverMarker.position);
//    mMap.removeDriverLine();
    mMap.drivingRoutePlan(driverAdr, to, true);
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
    isStartTrip = true;
    serviceCommon(status);
  }

  @Override
  public void driverEnd(OrderStatus status) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(Service.ORDER, mTaxiOrder);
    forwardWithPop(TaxiEndFragment.class, bundle);
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
        model.bounds.add(new LatLng(LocationProvider.getInstance().getLocation().mAdrLatLng.latitude,
                LocationProvider.getInstance().getLocation().mAdrLatLng.longitude));
        if (mMap.getLinePoints() != null) {
          model.bounds.addAll(mMap.getLinePoints());
        }
        if (mDriverMarker != null) {
          model.bounds.add(mDriverMarker.getPosition());
        }
        if (mStartMarker != null) {
          mStartMarker.remove();
        }
        mMap.hideMyLocation();
        break;
      }
    }
  }

  @Override
  protected void mapClearElement() {
    if (mDriverMarker != null) {
      mDriverMarker.remove();
      mDriverMarker = null;
    }
    mMap.unRegisterPlanCallback(this);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mServicePresenter.release();
  }

}
