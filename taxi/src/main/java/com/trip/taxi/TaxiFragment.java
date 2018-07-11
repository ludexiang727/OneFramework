package com.trip.taxi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.app.widget.wheelview.WheelView;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.dialog.DataPickerDialog.ISelectResultListener;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.UIThreadHandler;
import com.one.framework.utils.UIUtils;
import com.one.map.location.LocationProvider;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.trip.base.common.CommonParams;
import com.trip.base.page.AbsBaseFragment;
import com.trip.base.page.AbsBaseFragment.IChooseResultListener;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.end.TaxiEndFragment;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.presenter.TaxiFormPresenter;
import com.trip.taxi.service.ServiceFragment;
import com.trip.taxi.wait.TaxiWaitFragment;
import com.trip.taxi.widget.IFormView;
import com.trip.taxi.widget.IFormView.IFormListener;
import com.trip.taxi.widget.IFormView.IOnHeightChange;
import com.trip.taxi.widget.impl.FormView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

@ServiceProvider(value = Fragment.class, alias = "taxi")
public class TaxiFragment extends AbsBaseFragment implements ITaxiView, IOnHeightChange,
    IFormListener, IChooseResultListener {

  private IFormView mFormView;
  private TaxiFormPresenter mPresenter;
  private int mParamsMargin;
  private SupportDialogFragment mHaveTripDlg;
  private String mUserCustomTag;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = new TaxiFormPresenter(getContext(), this);
    mParamsMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    mMap.clearElements();
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_main_layout, container, true);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mFormView = (FormView) view.findViewById(R.id.taxi_form_view);
    mFormView.setOnHeightChange(this);
    mFormView.setFormListener(this);
    mMap.showMyLocation();
  }

  @Override
  public void onHeightChange(int height) {
    if (height == -1) {
      // 关闭reverse geo because 来回切换会触发 map onMoveChange -> onMoveFinish invoke reverse geo
      mPinView.isToggleLoading(false);
      reCalculateHeight();
      UIThreadHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (mFormView.getFormType() == IFormView.EASY_FORM) {
            mPinView.isToggleLoading(true); // 开启reverse geo
          }
        }
      }, 1000);
    } else {
      reLayoutLocationPosition(-height);
    }
  }

  @Override
  public void moveMapToStartAddress(Address address) {
    BestViewModel model = new BestViewModel();
    model.zoomCenter = address.mAdrLatLng;
    mMap.doBestView(model);
  }

  /**
   * reverse geo 设置地址
   * @param intent
   */
  @Override
  protected void handleReceiveLocAddress(Intent intent) {
    Address address = (Address) intent.getSerializableExtra(CommonParams.COMMON_CURRENT_LOCATION_ADDRESS);
    mPresenter.saveAddress(AddressTable.START, address);
    mFormView.setStartPoint(address.mAdrDisplayName);
  }

  @Override
  protected void handleReceiveRecovery(Intent intent) {
    if (isAdded() && getActivity()!= null && !getActivity().isFinishing()) {
      OrderDetail orderDetail = (OrderDetail) intent.getSerializableExtra(CommonParams.COMMON_RECOVERY_DATA);
      OrderStatus orderStatus = OrderStatus.fromStateCode(orderDetail.getOrderStatus());
      showHaveTripDialog(orderDetail, orderStatus);
    }
  }

  @Override
  protected void handleReceiveHistory(OrderDetail orderDetail) {
    OrderStatus orderStatus = OrderStatus.fromStateCode(orderDetail.getOrderStatus());
    handleRecoveryData(orderDetail, orderStatus, true);
  }

  @Override
  public void onStartClick() {
    addressSelector(AddressTable.START, this);
  }

  @Override
  public void onEndClick() {
    addressSelector(AddressTable.END, this);
  }

  @Override
  public void onTimeClick() {
    dataPickerSelector(2, new ISelectResultListener() {
      @Override
      public void onTimeSelect(long time, String showTime) {
        mPresenter.saveBookingTime(time);
        mFormView.setTime(time, showTime);
      }
    });
  }

  @Override
  public void onTipClick() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.taxi_tip_dialog_layout, null);
    final WheelView tipWheel = (WheelView) view.findViewById(R.id.taxi_wheel_view_tip);
    int tip = FormDataProvider.getInstance().obtainTip();
    tipWheel.setItems(mPresenter.getTipItems(), mPresenter.getTipPosition(tip));
    showBottomDialog(view, new OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = tipWheel.getSelectedPosition();
        int tip = mPresenter.getTip(position);
        mFormView.setMoney(tip);
        mFormView.showLoading(true);
      }
    });
  }

  @Override
  public void onMarkClick(View markView) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.taxi_mark_dialog_layout, null);
    final LinearLayout markViewParent = (LinearLayout) view.findViewById(R.id.taxi_mark_view_parent);
    final EditText customTag = (EditText) view.findViewById(R.id.taxi_mark_custom_tag);
    customTag.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        customTag.setFocusable(true);
        customTag.setFocusableInTouchMode(true);
        customTag.requestFocus();
        return false;
      }
    });
    if (!TextUtils.isEmpty(mUserCustomTag)) {
      customTag.setText(mUserCustomTag);
    }
    int rowIndex = -1;
    List<String> marks = mPresenter.getMarkItems();
    LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.weight = 1;
    params.rightMargin = params.leftMargin = mParamsMargin;
    params.topMargin = params.bottomMargin = mParamsMargin;
    params.gravity = Gravity.CENTER;
    final List<TextView> selectedView = new ArrayList<>();
    LinearLayout rowLayout = null;
    for (String mark : marks) {
      if (rowLayout == null || rowLayout.getChildCount() == 3) {
        rowIndex++;
        if (rowIndex >= 4) { //只展示4行
          break;
        }
        rowLayout = new LinearLayout(getContext());
        markViewParent.addView(rowLayout, rowIndex);
      }
      final TextView itemView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.taxi_mark_item_layout, null);
      Object marksStr = markView.getTag();
      if (marksStr != null && marksStr instanceof String) {
        for (String text : ((String) marksStr).split(",")) {
          if (mark.equals(text)) {
            itemView.setSelected(true);
            selectedView.add(itemView);
          }
        }
      }
      itemView.setText(mark);
      itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (selectedView.size() >= 3 && !itemView.isSelected()) {
            return;
          }
          itemView.setSelected(!itemView.isSelected());
          if (itemView.isSelected()) {
            selectedView.add(itemView);
          } else {
            selectedView.remove(itemView);
          }
        }
      });
      rowLayout.addView(itemView, params);
    }
    showBottomDialog(view, new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!selectedView.isEmpty()) {
          StringBuffer buffer = new StringBuffer();
          for (TextView textView : selectedView) {
            buffer.append(textView.getText()).append(",");
          }
          String customMarker = customTag.getText().toString();
          if (!TextUtils.isEmpty(customMarker)) {
            mUserCustomTag = customMarker;
            buffer.append(customMarker).append(",");
          }
          mFormView.setMsg(buffer.substring(0, buffer.toString().length() - 1));
        } else {
          mFormView.setMsg("");
        }
      }
    });
  }

  @Override
  public void forward(TaxiOrder order) {
    gotoWaitFragment(order, false);
  }

  @Override
  public void onNormalAdrSetting(int type) {
    if (type == AddressTable.HOME) {
      addressSelector(AddressTable.HOME, this);
    } else {
      addressSelector(AddressTable.COMPANY, this);
    }
  }

  @Override
  public boolean onBackPressed() {
    if (mFormView.getFormType() == IFormView.FULL_FORM) {
      mMap.clearElements();
      mMap.showMyLocation();
      mTopbarView.titleBarReset();
      pinViewHide(false);
      mNavigator.lockDrawerLayout(false);
      mPresenter.showEasyForm();
      mFormView.setEndPoint("");
      mFormView.setFormType(IFormView.EASY_FORM);
      FormDataProvider.getInstance().clearData();
      return true;
    }
    return super.onBackPressed();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    mPresenter.checkAddress();
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    if (mFormView.getFormType() == IFormView.FULL_FORM) {
      model.bounds.add(LocationProvider.getInstance().getLocation().mAdrLatLng);
      model.bounds.add(FormDataProvider.getInstance().obtainStartAddress().mAdrLatLng);
      model.bounds.add(FormDataProvider.getInstance().obtainEndAddress().mAdrLatLng);
      if (mMap.getLinePoints() != null) {
        model.bounds.addAll(mMap.getLinePoints());
      }
    } else {
      Address location = LocationProvider.getInstance().getLocation();
      Address startAdr = FormDataProvider.getInstance().obtainStartAddress();
      model.zoomCenter = startAdr != null ? startAdr.mAdrLatLng : location.mAdrLatLng;
      model.zoomLevel = 16.788f;
    }
  }

  @Override
  public void addMarks(List<MarkerOption> options) {
    mMap.addMarkers(options);
    mMap.drivingRoutePlan(FormDataProvider.getInstance().obtainStartAddress(),
        FormDataProvider.getInstance().obtainEndAddress());
  }

  @Override
  public void showFullForm() {
    int tip = FormDataProvider.getInstance().obtainTip();
    List<String> marksList = FormDataProvider.getInstance().obtainMarks();
    StringBuilder buffer = new StringBuilder();
    for (String mark: marksList) {
      buffer.append(mark).append(",");
    }
    boolean isPayPickUp = FormDataProvider.getInstance().isPay4PickUp();
    mFormView.setFormType(IFormView.FULL_FORM);
    mFormView.setMoney(tip);
    if (buffer.length() > 0) {
      mFormView.setMsg(buffer.substring(0, buffer.toString().length() - 1));
    } else {
      mFormView.setMsg("");
    }
    mFormView.setPay4PickUp(isPayPickUp);
    mTopbarView.setTitleClickListener(new ITopTitleListener() {
      @Override
      public void onTitleItemClick(ClickPosition position) {
        if (position == ClickPosition.LEFT) {
          onBackPressed();
        }
      }
    });
    mTopbarView.setTitle(R.string.taxi_confirm_page_title);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mNavigator.lockDrawerLayout(true);
    mFormView.showLoading(true); // 显示loading 并预估
    pinViewHide(true);
    toggleMapView();
  }

  private void showHaveTripDialog(final OrderDetail orderDetail, final OrderStatus orderStatus) {
    final SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext())
        .setTitle(getString(R.string.taxi_support_dlg_title))
        .setMessage(getString(R.string.taxi_have_unfinish_order_message))
        .setPositiveButton(getString(R.string.taxi_have_unfinish_order_go), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mHaveTripDlg.dismiss();
            handleRecoveryData(orderDetail, orderStatus, false);
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"))
        .setNegativeButton(getString(R.string.taxi_have_unfinish_order_no), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mHaveTripDlg.dismiss();
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.taxi_have_trip_unfinish_layout, null, false);
            TextView topInfo = view.findViewById(R.id.top_info_layout);
            topInfo.setText(UIUtils.highlight(getString(R.string.taxi_have_trip_click_forward)));
            view.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                detachFromTopContainer(view);
                // jump
                OrderDetail detail = HomeDataProvider.getInstance().obtainOrderDetail();
                if (detail != null) {
                  handleRecoveryData(detail, OrderStatus.fromStateCode(detail.getOrderStatus()), false);
                }
                HomeDataProvider.getInstance().saveOrderDetail(null);
              }
            });
            attachToTopContainer(view);
          }
        });
    mHaveTripDlg = builder.create();
    mHaveTripDlg.show(getFragmentManager(), "");
  }

  private void handleRecoveryData(OrderDetail order, OrderStatus status, boolean isFromHistory) {
    switch (status) {
      case CREATE: {
        TaxiOrder taxiOrder = mPresenter.copyOrderDetailToTaxiOrder(order, isFromHistory);
        gotoWaitFragment(taxiOrder, isFromHistory);
        break;
      }
      case RECEIVED:
      case SETOFF:
      case READY:
      case START: {
        pinViewHide(true);
        TaxiOrder taxiOrder = mPresenter.copyOrderDetailToTaxiOrder(order, isFromHistory);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CommonParams.Service.FROM_HISITORY, isFromHistory);
        bundle.putSerializable(CommonParams.Service.ORDER, taxiOrder);
        forward(ServiceFragment.class, bundle);
        break;
      }
      case COMPLAINT:
      case AUTOPAYING:
      case CONFIRM:
      case ARRIVED: {
        pinViewHide(true);
        TaxiOrder taxiOrder = mPresenter.copyOrderDetailToTaxiOrder(order, isFromHistory);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CommonParams.Service.FROM_HISITORY, isFromHistory);
        bundle.putSerializable(CommonParams.Service.ORDER, taxiOrder);
        forward(TaxiEndFragment.class, bundle);
        break;
      }
    }
  }

  private void gotoWaitFragment(TaxiOrder taxiOrder, boolean isFromHistory) {
    mTopbarView.popBackListener();
    pinViewHide(true);
    Bundle bundle = new Bundle();
    bundle.putBoolean(CommonParams.Service.FROM_HISITORY, isFromHistory);
    bundle.putSerializable(CommonParams.Service.ORDER, taxiOrder);
    forward(TaxiWaitFragment.class, bundle);
  }

  /**
   * 地址选择回调
   * @param type
   * @param address
   */
  @Override
  public void onResult(int type, Address address) {
    mPresenter.saveAddress(type, address);
    if (type == AddressTable.START) {
      mFormView.setStartPoint(address.mAdrDisplayName);
    } else {
      mFormView.setEndPoint(address.mAdrDisplayName);
    }
    mPresenter.checkAddress();
  }

  @Override
  protected void mapClearElement() {

  }
}
