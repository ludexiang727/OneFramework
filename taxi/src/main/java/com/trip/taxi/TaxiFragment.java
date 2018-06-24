package com.trip.taxi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.one.map.location.LocationProvider;
import com.one.map.log.Logger;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
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

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = new TaxiFormPresenter(getContext(), this);
    mParamsMargin = (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_main_layout, container, true);
    initView(view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void initView(View view) {
    mFormView = (FormView) view.findViewById(R.id.taxi_form_view);
    mFormView.setOnHeightChange(this);
    mFormView.setFormListener(this);
    mMap.displayMyLocation();
  }

  @Override
  public void onHeightChange(int height) {
    if (height == -1) {
      reCalculateHeight();
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

  @Override
  protected void handleReceiveLocAddress(Intent intent) {
    Address address = (Address) intent.getSerializableExtra(CommonParams.COMMON_CURRENT_LOCATION_ADDRESS);
    mPresenter.saveAddress(0, address);
    mFormView.setStartPoint(address.mAdrDisplayName);
  }

  @Override
  protected void handleReceiveRecovery(Intent intent) {
    OrderDetail orderDetail = (OrderDetail) intent.getSerializableExtra(CommonParams.COMMON_RECOVERY_DATA);
    OrderStatus orderStatus = OrderStatus.fromStateCode(orderDetail.getOrderStatus());
    showHaveTripDialog(orderDetail, orderStatus);
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
    tipWheel.setItems(mPresenter.getTipItems(), 0);
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
    final LinearLayout markViewParent = (LinearLayout) view
        .findViewById(R.id.taxi_mark_view_parent);
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
      final TextView itemView = (TextView) LayoutInflater.from(getContext())
          .inflate(R.layout.taxi_mark_item_layout, null);
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
          mFormView.setMsg(buffer.substring(0, buffer.toString().length() - 1));
        } else {
          mFormView.setMsg("");
        }
      }
    });
  }

  @Override
  public void forward(TaxiOrder order) {
    mPresenter.saveOrder(order);
    forward(TaxiWaitFragment.class);
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
      mMap.displayMyLocation();
      mTopbarView.titleBarReset();
      pinViewHide(false);
      mNavigator.lockDrawerLayout(false);
      mPresenter.showEasyForm();
      mFormView.setEndPoint("");
      mFormView.setFormType(IFormView.EASY_FORM);
      return true;
    }
    return super.onBackPressed();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    Logger.e("ldx", "TaxiUserVisible hint .... " + isVisibleToUser);
  }

  @Override
  protected void boundsLatlng(BestViewModel model) {
    if (isRootFragment) { // todo 建议用其他方法来区别是否在首页
      if (mFormView.getFormType() == IFormView.FULL_FORM) {
        model.bounds.add(LocationProvider.getInstance().getLocation().mAdrLatLng);
        model.bounds.add(FormDataProvider.getInstance().obtainStartAddress().mAdrLatLng);
        model.bounds.add(FormDataProvider.getInstance().obtainEndAddress().mAdrLatLng);
        if (mMap.getLinePoints() != null) {
          model.bounds.addAll(mMap.getLinePoints());
        }
      } else {
        LatLng location = LocationProvider.getInstance().getLocation().mAdrLatLng;
        model.zoomCenter = location;
        model.zoomLevel = 16.788f;
      }
    }
  }

  @Override
  public void showFullForm(List<MarkerOption> markers) {
    mFormView.setFormType(IFormView.FULL_FORM);
    mMap.drivingRoutePlan(FormDataProvider.getInstance().obtainStartAddress(),
        FormDataProvider.getInstance().obtainEndAddress());
    mMap.addMarkers(markers);
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
    mFormView.showLoading(true);
    pinViewHide(true);
    toggleMapView();
  }

  private void showHaveTripDialog(final OrderDetail orderDetail, final OrderStatus orderStatus) {
    final SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext())
        .setTitle(getString(R.string.taxi_have_unfinish_order_title))
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
          }
        });
    mHaveTripDlg = builder.create();
    mHaveTripDlg.show(getFragmentManager(), "");
  }

  private void handleRecoveryData(OrderDetail order, OrderStatus status, boolean isFromHistory) {
    switch (status) {
      case CREATE: {
        forward(TaxiWaitFragment.class);
        break;
      }
      case RECEIVED:
      case SETOFF:
      case READY:
      case START: {
        pinViewHide(true);
        TaxiOrder taxiOrder = mPresenter.copyOrderDetailToTaxiOrder(order);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CommonParams.Service.FROM_HISITORY, isFromHistory);
        bundle.putSerializable(CommonParams.Service.ORDER, taxiOrder);
        forward(ServiceFragment.class, bundle);
        break;
      }
      case CONFIRM:
      case ARRIVED: {
        pinViewHide(true);
        TaxiOrder taxiOrder = mPresenter.copyOrderDetailToTaxiOrder(order);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CommonParams.Service.FROM_HISITORY, isFromHistory);
        bundle.putSerializable(CommonParams.Service.ORDER, taxiOrder);
        forward(TaxiEndFragment.class, bundle);
        break;
      }
    }
  }

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

}
