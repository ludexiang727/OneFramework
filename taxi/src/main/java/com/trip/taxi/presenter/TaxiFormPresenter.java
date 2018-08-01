package com.trip.taxi.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.model.Evaluate;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.response.IResponseListener;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.trip.base.provider.FormDataProvider;
import com.trip.taxi.ITaxiView;
import com.trip.taxi.R;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.FeeInfo;
import com.trip.taxi.net.model.NearbyDriver;
import com.trip.taxi.net.model.OrderDriver;
import com.trip.taxi.net.model.TaxiEvaluate;
import com.trip.taxi.net.model.TaxiInfo;
import com.trip.taxi.net.model.TaxiNearbyDrivers;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderDetail;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class TaxiFormPresenter {

  private ITaxiView mView;
  private Context mContext;

  private String[] mTipArray;
  private String[] mMarkArray;

  public TaxiFormPresenter(Context context, ITaxiView view) {
    mContext = context;
    mView = view;
    mTipArray = context.getResources().getStringArray(R.array.TaxiTip);
    mMarkArray = context.getResources().getStringArray(R.array.TaxiMarks);
  }

  public void saveAddress(@AddressType int type, Address address) {
    if (type == AddressTable.START) {
      FormDataProvider.getInstance().saveStartAddress(address);
    } else {
      FormDataProvider.getInstance().saveEndAddress(address);
    }
  }

  public List<String> getTipItems() {
    List<String> items = new ArrayList<>();
    for (int i = 0; i < mTipArray.length; i++) {
      if (i == 0) {
        items.add(mTipArray[i]);
      } else {
        items.add(mTipArray[i] + "元");
      }
    }
    return items;
  }

  /**
   * 周边司机
   */
  public void taxiNearBy() {
    Address startAdr = FormDataProvider.getInstance().obtainStartAddress();
    if (startAdr != null) {
      TaxiRequest.taxiNearby(startAdr.mAdrLatLng, 3,
          new IResponseListener<TaxiNearbyDrivers>() {
            @Override
            public void onSuccess(TaxiNearbyDrivers drivers) {
              mView.addNearbyMarks(addMarks(drivers));
            }

            @Override
            public void onFail(int errCod, String message) {

            }

            @Override
            public void onFinish(TaxiNearbyDrivers baseObject) {

            }
          });
    }
  }

  public List<String> getMarkItems() {
    return Arrays.asList(mMarkArray);
  }

  public int getTip(int selectPosition) {
    if (selectPosition == 0) {
      FormDataProvider.getInstance().saveTip(0);
      return 0;
    }
    int tip = Integer.parseInt(mTipArray[selectPosition]);
    FormDataProvider.getInstance().saveTip(tip);
    return tip;
  }

  public int getTipPosition(int tip) {
    if (tip == 0) {
      return 0;
    }
    int position = 0;
    for (int i = 1; i < mTipArray.length; i++) {
      if (Integer.parseInt(mTipArray[i]) == tip) {
        position = i;
        break;
      } else {
        continue;
      }
    }
    return position;
  }

  public void saveBookingTime(long time) {
    FormDataProvider.getInstance().saveBookingTime(time);
  }

  public void showEasyForm() {
    FormDataProvider.getInstance().saveEndAddress(null);
  }

  public void checkAddress() {
    FormDataProvider provider = FormDataProvider.getInstance();
    if (provider.obtainStartAddress() != null && provider.obtainEndAddress() == null) {
      // move map
      mView.moveMapToStartAddress(provider.obtainStartAddress());
    } else if (provider.obtainStartAddress() != null && provider.obtainEndAddress() != null) {
      mView.addMarks(addMarks(provider));
      mView.showFullForm();
    }
  }

  @NonNull
  private List<MarkerOption> addMarks(FormDataProvider provider) {
    Address start = provider.obtainStartAddress();
    MarkerOption startOption = new MarkerOption();
    startOption.position = start.mAdrLatLng;
    startOption.title = start.mAdrDisplayName;
    startOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_start_icon);

    Address end = provider.obtainEndAddress();
    MarkerOption endOption = new MarkerOption();
    endOption.position = end.mAdrLatLng;
    endOption.title = end.mAdrDisplayName;
    endOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.base_map_end_icon);

    List<MarkerOption> markers = new ArrayList<MarkerOption>();
    markers.add(startOption);
    markers.add(endOption);
    return markers;
  }

  private List<MarkerOption> addMarks(TaxiNearbyDrivers drivers) {
    if (drivers != null) {
      List<NearbyDriver> nearbyDrivers = drivers.getNearbyDrivers();
      if (nearbyDrivers != null) {
        List<MarkerOption> options = new ArrayList<>();
        for (NearbyDriver driver : nearbyDrivers) {
          MarkerOption driverOption = new MarkerOption();
          driverOption.position = new LatLng(driver.getLatitude(), driver.getLongitude());
          driverOption.rotate = driver.getBearing();
          driverOption.descriptor = BitmapDescriptorFactory.fromResources(mContext.getResources(), R.drawable.taxi_driver);
          options.add(driverOption);
        }
        return options;
      }
    }
    return null;

  }

  public TaxiOrder copyOrderDetailToTaxiOrder(OrderDetail orderDetail, boolean isFromHistory) {
    String oid = orderDetail.getOrderId();
    long orderCreateTime = orderDetail.getOrderCreateTime();
    long currentServerTime = orderDetail.getCurrentServerTime();
    int waitConfigTime = orderDetail.getWaitConfigTime();
    String startAdrName = orderDetail.getStartPlaceName();
    String endAdrName = orderDetail.getEndPlaceName();
    double startLat = orderDetail.getStartLat();
    double startLng = orderDetail.getStartLng();
    double endLat = orderDetail.getEndLat();
    double endLng = orderDetail.getEndLng();
    String cityCode = orderDetail.getCityCode();
    int orderStatus = orderDetail.getOrderStatus();
    int carType = orderDetail.getCarType();
    int vendorId = orderDetail.getVendorId();
    int payType = orderDetail.getPayType();
    int type = orderDetail.getType();
    OrderDriver driver = null;
    if (orderDetail.getDriver() != null) {
      String driverId = orderDetail.getDriver().getDriverId();
      String driverName = orderDetail.getDriver().getDriverName();
      String driverIcon = orderDetail.getDriver().getDriverIcon();
      long driverReceiverCount = orderDetail.getDriver().getDriverReceiveOrderCount() == null ? 0 : orderDetail.getDriver().getDriverReceiveOrderCount();
      float driverStar =  orderDetail.getDriver().getDriverStar();
      String driverPhone = orderDetail.getDriver().getDriverTel();
      String driverCar = orderDetail.getDriver().getDriverCar();
      String driverCarColor = orderDetail.getDriver().getDriverCarColor();
      String driverCompany = orderDetail.getDriver().getDriverCompany();
      String driverCarNo = orderDetail.getDriver().getDriverCarNo();
      driver = new OrderDriver(driverId, driverName, driverIcon, driverReceiverCount,
          driverStar, driverPhone, driverCar, driverCarColor, driverCompany, driverCarNo);
    }
    TaxiInfo taxiInfo = null;
    if (orderDetail.getCarInfo() != null) {
      int pay4PickUp = orderDetail.getCarInfo().getPay4PickUp();
      List<String> marks = orderDetail.getCarInfo().getMarks();
      int dispatchFee = orderDetail.getCarInfo().getTip();
      int fedBack = orderDetail.getCarInfo().getFeedback();
      Evaluate orderEvaluate = orderDetail.getCarInfo().getEvaluate();
      TaxiEvaluate evaluate = null;
      if (orderEvaluate != null) {
        evaluate = new TaxiEvaluate(orderEvaluate.getUserId(), orderEvaluate.getBizType(),
            orderEvaluate.getDriverId(), orderEvaluate.getOrderId(), orderEvaluate.getContent(),
            orderEvaluate.getTags(), orderEvaluate.getStar());
      }
      taxiInfo = new TaxiInfo(pay4PickUp, marks, dispatchFee, fedBack, evaluate);
      FormDataProvider.getInstance().savePick4Up(pay4PickUp == 1 ? true : false);
      FormDataProvider.getInstance().saveTip(dispatchFee);
      FormDataProvider.getInstance().saveMarks(marks);
    }
    FeeInfo feeInfo = null;
    if (orderDetail.getFeeInfo() != null) {
      long actualTime = orderDetail.getFeeInfo().getActualTime();
      long actualDistance = orderDetail.getFeeInfo().getActualDistance();
      int totalMoney = orderDetail.getFeeInfo().getTotalMoney();
      int actualPayMoney = orderDetail.getFeeInfo().getActualPayMoney();
      int unPayMoney = orderDetail.getFeeInfo().getUnPayMoney();
      int discountMoney = orderDetail.getFeeInfo().getDiscountMoney();
      int refundMoney = orderDetail.getFeeInfo().getRefundMoney();
      feeInfo = new FeeInfo(actualTime, actualDistance, totalMoney, actualPayMoney, unPayMoney, discountMoney, refundMoney);
    }
    Address start = new Address();
    start.mAdrDisplayName = startAdrName;
    start.mAdrLatLng = new LatLng(startLat, startLng);
    start.mCityCode = cityCode;

    Address end = new Address();
    end.mAdrDisplayName = endAdrName;
    end.mAdrLatLng = new LatLng(endLat, endLng);
    end.mCityCode = cityCode;

    if (!isFromHistory) {
      FormDataProvider.getInstance().saveStartAddress(start);
      FormDataProvider.getInstance().saveEndAddress(end);
    }

    TaxiOrder taxiOrder = new TaxiOrder(oid, orderCreateTime, currentServerTime, waitConfigTime);
    TaxiOrderDetail detail = new TaxiOrderDetail(oid, startAdrName, endAdrName, startLat, startLng,
        endLat, endLng, cityCode, driver, taxiInfo, feeInfo, orderStatus, carType, vendorId, payType, type);
    taxiOrder.saveOrderInfo(detail);
    return taxiOrder;
  }
}
