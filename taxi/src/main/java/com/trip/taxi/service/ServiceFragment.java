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
import android.widget.TextView;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.pop.ITabItemClickListener;
import com.one.framework.app.pop.PopTabItem;
import com.one.framework.app.pop.PopType;
import com.one.framework.app.pop.PopUpService;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.StarView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.TimeUtils;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.location.LocationProvider;
import com.one.map.map.MarkerOption;
import com.one.map.map.element.Marker;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.common.CommonParams.Service;
import com.trip.base.page.BaseFragment;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.R;
import com.trip.taxi.end.TaxiEndFragment;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
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
  private static final int SELF_CANCELED = 1;
  private static final int DRIVER_CANCELED = SELF_CANCELED << 1;
  private ServicePresenter mServicePresenter;
  private ShapeImageView mDriverHeaderIcon;
  private ImageView mDriverIM;
  private ImageView mDriverPhone;
  private TextView mDriverName;
  private TextView mDriverCarNo;
  private TextView mDriverCompany;
  private StarView mDriverStarView;
  private TaxiOrder mTaxiOrder;
  private OrderStatus mCurrentStatus;
  private Marker mDriverMarker;
  private Marker mStartMarker;
  private Marker mEndMarker;
  private boolean infoWindowShowing;
  private long currentTime;
  private long interval;
  private SupportDialogFragment mCancelDialog;
  private View mBannerView;

  private boolean isStartTrip;
  private int mBestViewMargin;
  private boolean isFromHistory = false;
  private PopUpService mPopService;

  private long mDriverReadyTime;

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

    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISTORY);
    }
    mMap.removeDriverLine();
    mMap.stopRadarAnim();
    mMap.registerPlanCallback(this);
    mTopbarView.setTitle(R.string.taxi_service_wait_meet);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitleRight(R.string.taxi_service_title_bar_right_more);
    mCurrentStatus = OrderStatus.RECEIVED;
    mBestViewMargin = getResources().getDimensionPixelOffset(R.dimen.taxi_service_best_view_margin);
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
        onBackPressed();
        break;
      }
      case RIGHT: {
        final List<PopTabItem> items = getTabItems();
        mPopService = new PopUpService(getActivity(), PopType.WRAP);
        mPopService.setItems(items, new ITabItemClickListener() {
          @Override
          public void onTabClick(int position) {
            PopTabItem popTabItem = items.get(position);
            switch (popTabItem.itemType) {
              case CANCEL_ORDER: {
                cancelOrder(SELF_CANCELED,false, getString(R.string.taxi_cancel_confirm_msg));
                break;
              }
              case EMERGENCY_CONTACT: {
                break;
              }
              case CONNECT_SERVICE: {
                // 联系客服
                connectService();
                break;
              }
            }
          }
        });
        mPopService.showAsDropDown(mTopbarView.getRightView());
        break;
      }
    }
  }

  @Override
  public boolean onBackPressed() {
    mapClearElement();
    if (!isFromHistory) {
      FormDataProvider.getInstance().clearData();
      onBackInvoke();
      return true;
    } else {
      finishSelf();
      return true;
    }
  }

  /**
   * 如果是司机取消订单则需要显示全表单
   * @param isShowFullForm
   */
  private void cancelOrder(final int cancelType, final boolean isShowFullForm, String message) {
    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(
        getActivity()).setTitle(getString(R.string.taxi_wait_cancel_order))
        .setMessage(message)
        .setNegativeButton(getString(R.string.one_cancel), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCancelDialog.dismiss();
            if (cancelType == DRIVER_CANCELED) { // 司机取消
              FormDataProvider.getInstance().clearData();
              HomeDataProvider.getInstance().saveOrderDetail(null);
              finishSelf();
            }
          }
        })
        .setPositiveButton(getString(R.string.one_confirm), new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (cancelType == SELF_CANCELED) {
              mCancelDialog.dismiss();
              mServicePresenter.cancelOrder(isShowFullForm);
            } else {
              HomeDataProvider.getInstance().saveOrderDetail(null);
              finishSelf();
            }
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mCancelDialog = builder.create();
    mCancelDialog.show(getFragmentManager(), "");
  }

  /**
   * 联系客服
   * @return
   */
  private void connectService() {
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15010844540"));
    startActivity(intent);
  }

  @NonNull
  private List<PopTabItem> getTabItems() {
    List<PopTabItem> items = new ArrayList<PopTabItem>();
    if (mCurrentStatus == OrderStatus.RECEIVED || mCurrentStatus == OrderStatus.SET_OFF
        || mCurrentStatus == OrderStatus.READY) {
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
    items.add(im);
    return items;
  }

  @Override
  public void cancelOrderSuccess(TaxiOrderCancel taxiOrderCancel) {
    mMap.removeDriverLine();
    mTopbarView.popBackListener();
    finishSelf();
  }

  @Override
  public void routePlanPoints(List<LatLng> points) {
    toggleMapView();
  }

  @Override
  public void routePlanMsg(String msg, List<LatLng> points) {
    switch (mCurrentStatus) {
      case RECEIVED:
      case SET_OFF: {
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

  /**
   * 司机等待时间
   * @return
   */
  private CharSequence createInfoWindowTime() {
    if (currentTime == 0) {
      currentTime = System.currentTimeMillis();
    } else {
      interval += 1000;
    }
    long waitTime = currentTime - mDriverReadyTime + interval;
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
    mBannerView = LayoutInflater.from(getContext()).inflate(R.layout.taxi_service_banner_layout, null);
    ShapeImageView banner = mBannerView.findViewById(R.id.taxi_banner);
    banner.loadImageByUrl(null, "http://img12.360buyimg.com/cms/jfs/t799/76/717269560/247006/7915acb9/5540ad7dNe9b60017.jpg", "default");
    banner.setAdjustViewBounds(true);
    banner.setMaxWidth(UIUtils.getScreenWidth(getContext()));
    banner.setMaxHeight(UIUtils.getScreenWidth(getContext()) / 3);
    attachToTopContainer(mBannerView);
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

  /**
   * 司机取消订单
   * @param status
   */
  @Override
  public void driverCancel(OrderStatus status) {
    cancelOrder(DRIVER_CANCELED, true, getString(R.string.taxi_driver_cancel_order));
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
  public void driverReady(OrderStatus status, long driverReadyTime) {
    mDriverReadyTime = driverReadyTime;
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
    detachFromTopContainer(mBannerView);
    serviceCommon(status);
  }

  @Override
  public void driverEnd(OrderStatus status) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(Service.ORDER, mTaxiOrder);
    forwardWithPop(TaxiEndFragment.class, bundle);
  }

  private void serviceCommon(OrderStatus status) {
    if (mPopService != null && mPopService.isShowing()) {
      mPopService.dismiss();
    }
    mCurrentStatus = status;
    // 起终点 marker
    mServicePresenter.addMarks(mTaxiOrder);
    toggleMapView();
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    model.padding.left = model.padding.right = mBestViewMargin;
    switch (mCurrentStatus) {
      case RECEIVED:
      case SET_OFF: {
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

    if (mStartMarker != null) {
      mStartMarker.remove();
      mStartMarker = null;
    }

    if (mEndMarker != null) {
      mEndMarker.remove();
      mEndMarker = null;
    }

    mMap.removeDriverLine();
    mMap.clearElements();
    mMap.unRegisterPlanCallback(this);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mServicePresenter.release();
  }

}
