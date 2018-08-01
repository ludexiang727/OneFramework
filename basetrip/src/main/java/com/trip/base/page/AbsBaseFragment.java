package com.trip.base.page;

import static com.one.framework.db.DBTables.AddressTable.END;
import static com.one.framework.db.DBTables.AddressTable.HOME;
import static com.one.framework.db.DBTables.AddressTable.START;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow.OnDismissListener;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.widget.PopWindow;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.framework.dialog.BottomSheetDialog;
import com.one.framework.dialog.BottomSheetDialog.Builder;
import com.one.framework.dialog.DataPickerDialog;
import com.one.framework.dialog.DataPickerDialog.ISelectResultListener;
import com.one.framework.log.Logger;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.DBUtil;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.R;
import com.trip.base.common.CommonParams;
import com.trip.base.net.BaseRequest;
import com.trip.base.net.model.NormalAddress;
import com.trip.base.net.model.NormalAdr;
import com.trip.base.widget.AddressViewLayout;
import com.trip.base.widget.IAddressView;
import com.trip.base.widget.IAddressView.IAddressListener;
import java.util.List;
import java.util.Stack;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ludexiang on 2018/6/7.
 */

public abstract class AbsBaseFragment extends BaseFragment implements IRoutePlanMsgCallback, IAddressListener {

  private Stack<PopWindow> mPopStack;

  private IAddressView mAddressView;

  private IChooseResultListener mChooseResultListener;

  protected LocalBroadcastManager mLocalBroadManager;

  private BroadReceiver mReceiver;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPopStack = new Stack<>();
    initBroadcast();
    EventBus.getDefault().register(this);
    mMap.registerPlanCallback(this);
  }

  private void initBroadcast() {
    mLocalBroadManager = LocalBroadcastManager.getInstance(getActivity());
    mReceiver = new BroadReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(CommonParams.COMMON_ADDRESS_INTENT_ACTION);
    filter.addAction(CommonParams.COMMON_RECOVERY_ACTION);
    mLocalBroadManager.registerReceiver(mReceiver, filter);
  }

  private class BroadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (CommonParams.COMMON_ADDRESS_INTENT_ACTION.equalsIgnoreCase(action)) {
        handleReceiveLocAddress(intent);
      } else if (CommonParams.COMMON_RECOVERY_ACTION.equals(action)) {
        handleReceiveRecovery(intent);
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void event(Object obj) { // 历史行程 和 登录成功的时候会 toggle EventBus
    Logger.e("ldx", "event bus >>> obj " + obj);
    if (obj instanceof  OrderDetail) {
      OrderDetail orderDetail = (OrderDetail) obj;
      handleReceiveHistory(orderDetail);
    } else if (obj instanceof Boolean) {
      String token = UserProfile.getInstance(getContext()).getTokenValue();
      String cityCode = LocationProvider.getInstance().getCityCode();
      if (!TextUtils.isEmpty(token)) {
        BaseRequest.baseNormalQuery(token, cityCode, new IResponseListener<NormalAddress>() {
          @Override
          public void onSuccess(NormalAddress normalAddress) {
            if (normalAddress != null) {
              List<NormalAdr> normalAdrs = normalAddress.getAddress();
              for (NormalAdr normalAdr : normalAdrs) {
                Address address = new Address();
                address.mCityCode = normalAdr.getCityCode();
                address.mAdrDisplayName = normalAdr.getPoiName();
                address.mAdrFullName = normalAdr.getAddressDetail();
                address.mAdrLatLng = new LatLng(Double.parseDouble(normalAdr.getLatitude()),
                    Double.parseDouble(normalAdr.getLongitude()));
                if (normalAdr.getTag() == 1) {
                  onAddressItemClick(address, AddressTable.HOME, false);
                } else if (normalAdr.getTag() == 2) {
                  onAddressItemClick(address, AddressTable.COMPANY, false);
                }
              }
            }
          }

          @Override
          public void onFail(int errCod, String message) {

          }

          @Override
          public void onFinish(NormalAddress normalAddress) {

          }
        });
      }
    }
  }


  protected void handleReceiveLocAddress(Intent intent) {
    // do noting
  }

  protected void handleReceiveRecovery(Intent intent) {
    // do noting
  }

  protected void handleReceiveHistory(OrderDetail orderDetail) {
    // do nothing
  }

  @Override
  public void routePlanPoints(List<LatLng> points) {
    toggleMapView();
  }

  @Override
  public void routePlanMsg(String msg, List<LatLng> points) {

  }

  /**
   * @param view dialog 展示的view BottomSheetDialog 包含titlebar 取消 和 确认
   * @param listener
   * @return
   */
  protected BottomSheetDialog showBottomDialog(View view, View.OnClickListener listener) {
    BottomSheetDialog bottomDialog = new Builder(getContext())
        .setContentView(view)
        .setPositiveButton(getString(R.string.one_confirm), listener)
        .create();
    bottomDialog.show();
    return bottomDialog;
  }

  /**
   * @param timeRange 显示几天
   */
  protected void dataPickerSelector(int timeRange, ISelectResultListener listener) {
    DataPickerDialog dataPickerDialog = new DataPickerDialog(getContext(), timeRange)
        .setSelectResultListener(listener);
    dataPickerDialog.show();
  }

  protected void addressSelector(@AddressType final int type, final IChooseResultListener listener) {
    mChooseResultListener = listener;
    mAddressView = new AddressViewLayout(getContext());
    if (type == START || type == END) {
      int editHint = type == START ? R.string.address_input_search_from : R.string.address_input_search_to;
      mAddressView.setInputSearchHint(editHint);
    } else if (type == HOME || type == AddressTable.COMPANY) {
      String hint = type == HOME ? String.format(getString(R.string.address_input_address_hint),
          getString(R.string.address_normal_home_address)) : String
          .format(getString(R.string.address_input_address_hint),
              getString(R.string.address_normal_company_address));
      mAddressView.setInputSearchHint(hint);
    }
    mAddressView.setAddressType(type);
    if (HomeDataProvider.getInstance().obtainCurAddress() != null) {
      mAddressView.setCurrentLocationCity(HomeDataProvider.getInstance().obtainCurAddress().mCity);
    }
    mAddressView.setAddressItemClick(this);
    if (type == START) {
      // 根据当前位置获取POI
      mAddressView.setNormalAddress(HomeDataProvider.getInstance().obtainPoiAddress(0));
    } else {
      mAddressView.setNormalAddress(HomeDataProvider.getInstance().obtainPoiAddress(1));
    }
    PopWindow popWindow = new PopWindow.PopupWindowBuilder(getContext())
        .setView(mAddressView.getView())
        .size(UIUtils.getScreenWidth(getContext()), UIUtils.getScreenHeight(getContext()))
        .setBackgroundDrawable(new ColorDrawable(Color.parseColor("#10000000")))
        .setOnDissmissListener(new OnDismissListener() {
          @Override
          public void onDismiss() {
            if (type == END) {
              mAddressView.releaseView();
            }
          }
        })
        .create();
    popWindow.showAtLocation(getView(), Gravity.TOP, 0, UIUtils.getStatusbarHeight(getContext()));
    mPopStack.push(popWindow);
  }

  public void onAddressItemClick(Address address, int type, boolean isUpdate) {
    if (mPopStack != null && !mPopStack.isEmpty()) {
      mPopStack.pop().dissmiss();
    }

    if (type == START || type == END) {
      if (mChooseResultListener != null) {
        mChooseResultListener.onResult(type, address);
      }
    }
    if (type == HOME || type == AddressTable.COMPANY) {
      String token = UserProfile.getInstance(getContext()).getTokenValue();
      String cityCode = LocationProvider.getInstance().getCityCode();

      if (isUpdate) {
        BaseRequest.baseNormalUpdate(token, cityCode, address.mAdrLatLng.latitude,
            address.mAdrLatLng.longitude, type == HOME ? 1 : 2, address.mAdrDisplayName,
            address.mAdrFullName,
            new IResponseListener<BaseObject>() {
              @Override
              public void onSuccess(BaseObject baseObject) {
                Logger.e("ldx", "update Normal Address Success ");
              }

              @Override
              public void onFail(int errCod, String message) {
                Logger.e("ldx", "update Normal Address Fail ");
              }

              @Override
              public void onFinish(BaseObject baseObject) {

              }
            });
      }
      List<Address> homeOrCompanyLists = DBUtil.queryDataFromAddress(getContext(), type);
      if (homeOrCompanyLists != null && !homeOrCompanyLists.isEmpty()) {
        int updateRow = DBUtil.updateDataToAddress(getContext(), address, type);
        return;
      }
    }
    DBUtil.insertDataToAddress(getContext(), address, type);
  }

  @Override
  public void onAddressItemClick(Address address, int type) {
    onAddressItemClick(address, type, true);
  }

  @Override
  public void searchByKeyWord(String curCity, CharSequence key, IPoiSearchListener listener) {
    mMap.poiSearchByKeyWord(curCity, key, listener);
  }

  @Override
  public void onNormalAdrSetting(int type) {

  }

  @Override
  public void onDismiss() {
    if (mPopStack != null && !mPopStack.isEmpty()) {
      mPopStack.pop().dissmiss();
    }
  }

  public interface IChooseResultListener {
    void onResult(@AddressType int type, Address address);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mMap.unRegisterPlanCallback(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
}
