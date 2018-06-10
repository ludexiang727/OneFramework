package com.trip.base.widget;

import android.view.View;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.model.Address;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public interface IAddressView {

  void setCurrentLocationCity(String city);

  void setInputSearchHint(String inputHint);

  void setInputSearchHint(int inputSearchHint);

  void setNormalAddress(int type, List<Address> addresses);

  void setAddressItemClick(IAddressItemClick clickListener);

  interface IAddressItemClick {

    void onAddressItemClick(Address address);

    void searchByKeyWord(String curCity, CharSequence key, IPoiSearchListener listener);

    void onDismiss();
  }

  void setNormalAddressVisible(boolean visible);

  View getView();
}
