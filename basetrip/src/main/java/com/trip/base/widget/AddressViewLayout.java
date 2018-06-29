package com.trip.base.widget;

import static com.one.framework.db.DBTables.AddressTable.COMPANY;
import static com.one.framework.db.DBTables.AddressTable.HOME;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.framework.utils.DBUtil;
import com.one.map.IMap.IPoiSearchListener;
import com.one.map.model.Address;
import com.trip.base.R;
import com.trip.base.adapter.AddressAdapter;
import java.util.ArrayList;
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
  private IAddressListener mAdrItemClick;
  private AddressAdapter mAddressAdapter;
  private PullScrollRelativeLayout mPullLayout;
  private ViewStub mViewStub;
  private static RelativeLayout mHomeAddressLayout;
  private static TextView mHome;
  private static TextView mHomeDetail;
  private static RelativeLayout mCompanyAddressLayout;
  private static TextView mCompany;
  private static TextView mCompanyDetail;
  private EditWatcher mWatcher;
  private LinearLayout mNormalAddressLayout;

  @AddressType
  private int mLastAdrType = AddressTable.NONE;

  @AddressType
  private int mAdrType = AddressTable.START;

  public AddressViewLayout(Context context) {
    this(context, null);
  }

  public AddressViewLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  @Override
  public void setAddressType(int type) {
    mAdrType = type;

    if (mAdrType == AddressTable.END) {
      initViewStub();
    }
  }

  public AddressViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOrientation(VERTICAL);
    View view = LayoutInflater.from(context).inflate(R.layout.address_selector_layout, this, true);

    mViewStub = (ViewStub) view.findViewById(R.id.base_address_normal_viewstub);
    mPullLayout = (PullScrollRelativeLayout) view.findViewById(R.id.pull_scroll_layout);
    mAddressListView = (PullListView) view.findViewById(android.R.id.list);
    mCurLocCity = (TextView) view.findViewById(R.id.address_cur_city);
    mCancel = (TextView) view.findViewById(R.id.address_choose_cancel);
    mInputAdrSearch = (EditText) view.findViewById(R.id.address_input_search);

    mPullLayout.setScrollView(mAddressListView);
    mPullLayout.setMoveListener(mAddressListView);

    mAddressAdapter = new AddressAdapter(context);
    mAddressListView.setAdapter(mAddressAdapter);
    mWatcher = new EditWatcher();

    mCancel.setOnClickListener(this);

    mAddressListView.setItemClickListener(this);
    mInputAdrSearch.addTextChangedListener(mWatcher);
  }

  private void initViewStub() {
    View view = mViewStub.inflate();
    mNormalAddressLayout = (LinearLayout) view.findViewById(R.id.address_normal_address_set_layout);
//    mNormalAddressLayout.setVisibility();

    mHomeAddressLayout = (RelativeLayout) view.findViewById(R.id.address_home_normal_layout);
    mHome = (TextView) view.findViewById(R.id.address_normal_home);
    mHomeDetail = (TextView) view.findViewById(R.id.address_normal_home_detail);
    mCompanyAddressLayout = (RelativeLayout) view.findViewById(R.id.address_company_normal_layout);
    mCompany = (TextView) view.findViewById(R.id.address_normal_company);
    mCompanyDetail = (TextView) view.findViewById(R.id.address_normal_company_detail);

    final ImageView homeArrow = (ImageView) view.findViewById(R.id.address_normal_home_change);
    final ImageView companyArrow = (ImageView) view.findViewById(R.id.address_normal_company_change);

    mHomeAddressLayout.setTag(null);
    mHomeAddressLayout.setOnClickListener(this);
    mCompanyAddressLayout.setTag(null);
    mCompanyAddressLayout.setOnClickListener(this);

    StateListDrawable stateList = new StateListDrawable();
//    stateList.

    homeArrow.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // 会走down 和 up 事件，则做一次判断
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            homeArrow.setFocusable(true);
            homeArrow.setFocusableInTouchMode(true);
            break;
          }
          case MotionEvent.ACTION_UP: {
            homeArrow.setFocusable(false);
            mAdrItemClick.onNormalAdrSetting(HOME);
            break;
          }
        }
        return true;
      }
    });

    companyArrow.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            companyArrow.setFocusable(true);
            companyArrow.setFocusableInTouchMode(true);
            break;
          }
          case MotionEvent.ACTION_UP: {
            companyArrow.setFocusable(false);
            mAdrItemClick.onNormalAdrSetting(COMPANY);
            break;
          }
        }
        return true;
      }
    });
  }

  @Override
  public void setCurrentLocationCity(String city) {
    mCurLocCity.setText(city);
  }

  @Override
  public void setAddressItemClick(IAddressListener clickListener) {
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
  public void setNormalAddress(List<Address> addresses) {
    mAddressAdapter.setType(mAdrType);
    List<Address> addressList = new ArrayList<>();
    List<Address> searchAddress = DBUtil.queryDataFromAddress(getContext(), AddressTable.SEARCH_HISTORY);
    if (searchAddress != null && !searchAddress.isEmpty()) {
      addressList.addAll(searchAddress);
    }
    // address 未定位成功之后 addresses 可能为空
    if (addresses != null && !addresses.isEmpty()) {
      addressList.addAll(addresses);
    }
    /**
     * 插入多条 故取最后一条
     */
    List<Address> homeList = DBUtil.queryDataFromAddress(getContext(), AddressTable.HOME);
    if (homeList != null && !homeList.isEmpty()) {
      setNormalAddress(homeList.get(homeList.size() - 1), HOME);
    }
    List<Address> companyList = DBUtil.queryDataFromAddress(getContext(), AddressTable.COMPANY);
    if (companyList != null && !companyList.isEmpty()) {
      setNormalAddress(companyList.get(companyList.size() - 1), COMPANY);
    }
    mAddressAdapter.setListData(addressList);
  }


  private void setNormalAddress(Address address, int type) {
    /**
     * 判断或者原因：
     * 1 从地址选择进来的mAdrType = HOME
     * 2 若地址不为空进来的选择页面mAdrType = END, 从数据库中查取type = HOME
     */
    switch (type) {
      case HOME: {
        if (mHome != null && mHomeDetail != null && mHomeAddressLayout != null) {
          if (address != null && (mAdrType == HOME || address.type == HOME)) {
            mHome.setText(address.mAdrDisplayName);
            mHomeDetail.setText(address.mAdrFullName);
            mHomeAddressLayout.setTag(address);
          }
        }
        break;
      }
      case COMPANY: {
        /**
         * 同 HOME
         */
        if (mCompany != null && mCompanyDetail != null && mCompanyAddressLayout != null) {
          if (address != null && (mAdrType == COMPANY || address.type == COMPANY)) {
            mCompany.setText(address.mAdrDisplayName);
            mCompanyDetail.setText(address.mAdrFullName);
            mCompanyAddressLayout.setTag(address);
          }
        }
        break;
      }
    }
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.address_choose_cancel) {
      mAdrItemClick.onDismiss();
    } else if (v.getId() == R.id.address_company_normal_layout) {
      if (v.getId() == R.id.address_company_normal_layout && mCompanyAddressLayout.getTag() != null) {
        onAddressChoose((Address) mCompanyAddressLayout.getTag());
        return;
      }
      mAdrItemClick.onNormalAdrSetting(COMPANY);
    } else if (v.getId() == R.id.address_home_normal_layout) {
      if (v.getId() == R.id.address_home_normal_layout && mHomeAddressLayout.getTag() != null) {
        onAddressChoose((Address) mHomeAddressLayout.getTag());
        return;
      }
      mAdrItemClick.onNormalAdrSetting(HOME);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    Address chooseAddress = mAddressAdapter.getItem(position);
    onAddressChoose(chooseAddress);
    // 重新创建了地址选择View导致对象不一致
    setNormalAddress(chooseAddress, mAdrType);
  }

  private void onAddressChoose(Address chooseAddress) {
    if (mAdrItemClick != null) {
      if (mLastAdrType != AddressTable.NONE) {
        mAdrType = mLastAdrType;
        mLastAdrType = AddressTable.NONE;
      }
      mAdrItemClick.onAddressItemClick(chooseAddress, mAdrType);
    }
  }

  class EditWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
      if (!TextUtils.isEmpty(sequence)) {
        if (mAdrType == AddressTable.START || mAdrType == AddressTable.END) {
          mLastAdrType = mAdrType;
          mAdrType = AddressTable.SEARCH_HISTORY;
        }
        /**
         * 通过POI搜索的Address 默认type = SEARCH (6)
         */
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
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  @Override
  public void releaseView() {
    mHomeAddressLayout = null;
    mHome = null;
    mHomeDetail = null;
    mCompanyAddressLayout = null;
    mCompany = null;
    mCompanyDetail = null;
  }
}
