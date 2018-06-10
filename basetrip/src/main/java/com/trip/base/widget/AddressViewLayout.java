package com.trip.base.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.model.Address;
import com.trip.base.R;
import com.trip.base.adapter.AddressAdapter;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class AddressViewLayout extends LinearLayout implements IAddressView, OnClickListener,
    IItemClickListener {

  private TextView mCurLocCity;
  private TextView mCancel;
  private EditText mInputAdrSearch;
  private PullListView mAddressListView;
  private IAddressItemClick mAdrItemClick;
  private AddressAdapter mAddressAdapter;
  private PullScrollRelativeLayout mPullLayout;
  private EditWatcher mWatcher;
  private LinearLayout mNormalAddressLayout;

  public AddressViewLayout(Context context) {
    this(context, null);
  }

  public AddressViewLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AddressViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOrientation(VERTICAL);
    View view = LayoutInflater.from(context).inflate(R.layout.address_selector_layout, this, true);
    mPullLayout = (PullScrollRelativeLayout) view.findViewById(R.id.pull_scroll_layout);
    mAddressListView = (PullListView) view.findViewById(android.R.id.list);
    mCurLocCity = (TextView) view.findViewById(R.id.address_cur_city);
    mCancel = (TextView) view.findViewById(R.id.address_choose_cancel);
    mInputAdrSearch = (EditText) view.findViewById(R.id.address_input_search);
    mNormalAddressLayout = (LinearLayout) view.findViewById(R.id.address_normal_address_set_layout);

    mPullLayout.setScrollView(mAddressListView);
    mPullLayout.setMoveListener(mAddressListView);

    mAddressAdapter = new AddressAdapter(context);
    mAddressListView.setAdapter(mAddressAdapter);
    mWatcher = new EditWatcher();

    mCancel.setOnClickListener(this);
    mAddressListView.setItemClickListener(this);
    mInputAdrSearch.addTextChangedListener(mWatcher);
  }

  @Override
  public void setCurrentLocationCity(String city) {
    mCurLocCity.setText(city);
  }

  @Override
  public void setAddressItemClick(IAddressItemClick clickListener) {
    mAdrItemClick = clickListener;
  }

  @Override
  public void setInputSearchHint(String inputHint) {
    mInputAdrSearch.setHint(inputHint);
  }

  @Override
  public void setInputSearchHint(int inputSearchHint) {
    mInputAdrSearch.setHint(inputSearchHint);
  }

  @Override
  public void setNormalAddress(int type, List<Address> addresses) {
    mAddressAdapter.setType(type);
    mAddressAdapter.setListData(addresses);
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.address_choose_cancel) {
      mAdrItemClick.onDismiss();
    }
  }

  @Override
  public void setNormalAddressVisible(boolean visible) {
    mNormalAddressLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    Address chooseAddress = mAddressAdapter.getItem(position);
    if (mAdrItemClick != null) {
      mAdrItemClick.onAddressItemClick(chooseAddress);
    }
  }

  class EditWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
      mAdrItemClick.searchByKeyWord(mCurLocCity.getText().toString(), sequence, new IPoiSearchListener() {
        @Override
        public void onMapSearchAddress(List<Address> address) {
          if (address != null && !address.isEmpty()) {
            mAddressAdapter.clear();
            mAddressAdapter.setListData(address);
          }
        }
      });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }
}
