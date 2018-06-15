package com.trip.base.page;

import static com.one.framework.db.DBTables.AddressTable.END;
import static com.one.framework.db.DBTables.AddressTable.HOME;
import static com.one.framework.db.DBTables.AddressTable.START;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow.OnDismissListener;
import com.one.framework.app.widget.PopWindow;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.framework.dialog.BottomSheetDialog;
import com.one.framework.dialog.BottomSheetDialog.Builder;
import com.one.framework.dialog.DataPickerDialog;
import com.one.framework.dialog.DataPickerDialog.ISelectResultListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.DBUtil;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.log.Logger;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.R;
import com.trip.base.widget.AddressViewLayout;
import com.trip.base.widget.IAddressView;
import com.trip.base.widget.IAddressView.IAddressListener;
import java.util.List;
import java.util.Stack;

/**
 * Created by ludexiang on 2018/6/7.
 */

public abstract class AbsBaseFragment extends BaseFragment implements IRoutePlanMsgCallback, IAddressListener {


  private Stack<PopWindow> mPopStack;

  private IAddressView mAddressView;

  private IChooseResultListener mChooseResultListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mMap.setRoutePlanCallback(this);
    mPopStack = new Stack<>();
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

  @Override
  public void onAddressItemClick(Address address, int type) {
    if (mPopStack != null && !mPopStack.isEmpty()) {
      mPopStack.pop().dissmiss();
    }

    if (type == START || type == END) {
      if (mChooseResultListener != null) {
        mChooseResultListener.onResult(type, address);
      }
    }
    if (type == HOME || type == AddressTable.COMPANY) {
      List<Address> homeOrCompanyLists = DBUtil.queryDataFromAddress(getContext(), type);
      if (homeOrCompanyLists != null && !homeOrCompanyLists.isEmpty()) {
        int updateRow = DBUtil.updateDataToAddress(getContext(), address, type);
        return;
      }
    }
    DBUtil.insertDataToAddress(getContext(), address, type);
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
}
