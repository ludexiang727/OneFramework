package com.trip.base.widget;

import android.support.annotation.Keep;
import android.view.View;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.model.Address;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */
@Keep
public interface IAddressView {

  void setCurrentLocationCity(String city);

  void setInputSearchHint(String inputHint);

  void setInputSearchHint(int inputSearchHint);

  void setAddressType(@AddressType int type);

  void setNormalAddress(List<Address> addresses);

  void setAddressItemClick(IAddressListener clickListener);

  @Keep
  interface IAddressListener {

    void onAddressItemClick(Address address, @AddressType int type);

    void searchByKeyWord(String curCity, CharSequence key, IPoiSearchListener listener);

    void onNormalAdrSetting(int type);

    void onDismiss();
  }

  void releaseView();

  View getView();
}
