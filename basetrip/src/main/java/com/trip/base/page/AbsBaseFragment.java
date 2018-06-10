package com.trip.base.page;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import com.one.framework.app.widget.PopWindow;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.IMap.IRoutePlanMsgCallback;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.R;
import com.trip.base.widget.AddressViewLayout;
import com.trip.base.widget.IAddressView;
import com.trip.base.widget.IAddressView.IAddressItemClick;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public abstract class AbsBaseFragment extends BaseFragment implements IRoutePlanMsgCallback {

  protected static final int TYPE_START_ADR = 0;
  protected static final int TYPE_END_ADR = 1;

  @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
  @Retention(RetentionPolicy.RUNTIME)
  @IntDef({TYPE_START_ADR, TYPE_END_ADR})
  private @interface AddressType {
  }

  private PopWindow mPopWindow;

  private IAddressView mAddressView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mMap.setRoutePlanCallback(this);
  }

  @Override
  public void routePlanPoints(List<LatLng> points) {
    toggleMapView();
  }

  @Override
  public void routePlanMsg(String msg, List<LatLng> points) {

  }

  protected void addressSelector(@AddressType final int type, final IChooseResultListener listener) {
    mAddressView = new AddressViewLayout(getContext());
    mAddressView.setInputSearchHint(type == TYPE_START_ADR ? R.string.address_input_search_from : R.string.address_input_search_to);
    mAddressView.setNormalAddressVisible(type == TYPE_START_ADR ? false : true);
    if (HomeDataProvider.getInstance().obtainCurAddress() != null) {
      mAddressView.setCurrentLocationCity(HomeDataProvider.getInstance().obtainCurAddress().mCity);
    }
    mAddressView.setAddressItemClick(new IAddressItemClick() {
      @Override
      public void onAddressItemClick(Address address) {
        if (listener != null) {
          listener.onResult(type, address);
        }
        mPopWindow.dissmiss();
      }

      @Override
      public void searchByKeyWord(String curCity, CharSequence key, IPoiSearchListener poiSearchListener) {
        mBusContext.getMap().poiSearchByKeyWord(curCity, key, poiSearchListener);
      }

      @Override
      public void onDismiss() {
        if (mPopWindow != null) {
          mPopWindow.dissmiss();
        }
      }
    });
    if (type == TYPE_START_ADR) {
      // 根据当前位置获取POI
      mAddressView.setNormalAddress(0, HomeDataProvider.getInstance().obtainPoiAddress(0));
    } else {
      mAddressView.setNormalAddress(1, HomeDataProvider.getInstance().obtainPoiAddress(1));
    }
    mPopWindow = new PopWindow.PopupWindowBuilder(getContext())
        .setView(mAddressView.getView())
        .size(UIUtils.getScreenWidth(getContext()), UIUtils.getScreenHeight(getContext()))
        .setBackgroundDrawable(new ColorDrawable(Color.parseColor("#10000000")))
        .create();
    mPopWindow.showAtLocation(getView(), Gravity.TOP, 0, UIUtils.getStatusbarHeight(getContext()));
  }

  public interface IChooseResultListener {
    void onResult(@AddressType int type, Address address);
  }
}
