package com.trip.taxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.app.widget.wheelview.WheelView;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.dialog.BottomSheetDialog;
import com.one.framework.dialog.DataPickerDialog.ISelectResultListener;
import com.one.map.location.LocationProvider;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.trip.base.page.AbsBaseFragment;
import com.trip.base.page.AbsBaseFragment.IChooseResultListener;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.presenter.TaxiFormPresenter;
import com.trip.taxi.widget.IFormView;
import com.trip.taxi.widget.IFormView.IFormListener;
import com.trip.taxi.widget.IFormView.IOnHeightChange;
import com.trip.taxi.widget.impl.FormView;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

@ServiceProvider(value = Fragment.class, alias = "taxi")
public class TaxiFragment extends AbsBaseFragment implements ITaxiView, IOnHeightChange,
    IFormListener, IChooseResultListener {

  private static final String ADDRESS_INTENT_ACTION = "INTENT_CURRENT_LOCATION_ADDRESS";
  private IFormView mFormView;
  private LocalBroadcastManager mBroadcast;
  private BroadReceiver mReceiver;
  private TaxiFormPresenter mPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = new TaxiFormPresenter(getContext(), this);
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_main_layout, container, true);
    initView(view);
    initBroadcast();
    return view;
  }

  private void initView(View view) {
    mFormView = (FormView) view.findViewById(R.id.taxi_form_view);
    mFormView.setOnHeightChange(this);
    mFormView.setFormListener(this);
  }

  private void initBroadcast() {
    mBroadcast = LocalBroadcastManager.getInstance(getContext());
    mReceiver = new BroadReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(ADDRESS_INTENT_ACTION);
    mBroadcast.registerReceiver(mReceiver, filter);
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
  public void onMarkClick() {

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
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
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
      LatLng location = LocationProvider.getInstance().getLocation().mAdrLatLng;
      model.zoomCenter = location;
      model.bounds.add(location);
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
    mTopbarView.setLeft(R.drawable.base_top_bar_back_selector);
    mNavigator.lockDrawerLayout(true);
    mFormView.showLoading(true);
    pinViewHide(true);
    toggleMapView();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mBroadcast.unregisterReceiver(mReceiver);
  }

  private class BroadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (ADDRESS_INTENT_ACTION.equalsIgnoreCase(action)) {
        Address address = (Address) intent.getSerializableExtra("current_location_address");
        mPresenter.saveAddress(0, address);
        mFormView.setStartPoint(address.mAdrDisplayName);
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
