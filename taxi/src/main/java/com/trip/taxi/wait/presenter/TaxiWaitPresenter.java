package com.trip.taxi.wait.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.TimeUtils;
import com.one.framework.utils.ToastUtils;
import com.one.framework.utils.UIThreadHandler;
import com.one.map.log.Logger;
import com.one.map.map.BitmapDescriptorFactory;
import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import com.trip.base.common.CommonParams;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.base.wait.presenter.AbsWaitPresenter;
import com.trip.taxi.R;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.TaxiService;
import com.trip.taxi.net.model.TaxiOrderDetail;
import com.trip.taxi.net.model.TaxiOrderStatus;
import com.trip.taxi.wait.ITaxiWaitView;
import com.trip.taxi.wait.TaxiWaitView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitPresenter extends AbsWaitPresenter {

  private Context mContext;
  private static final int COUNT_DOWN = 1;
  private IWaitView mWaitView;
  private TaxiOrder mTaxiOrder;
  private int mWaitTime;
  private HandlerThread mHandlerThread;
  private Handler mHandler;
  private int mCurrentTime = 0;
  private byte[] lock = new byte[0];

  private ITaxiWaitView iTaxiWaitView;
  private LocalBroadcastManager mBroadManager;
  private BroadReceiver mReceiver;
  private List<String> mTipItems = new ArrayList<>();
  private int mTipLength;
  private long mOrderCreateTime;

  public TaxiWaitPresenter(Context context, TaxiOrder order, final ITaxiWaitView taxiWaitView) {
    mContext = context;
    iTaxiWaitView = taxiWaitView;
    String[] tipArray = context.getResources().getStringArray(R.array.TaxiTip);
    mTipLength = tipArray.length;
    for (String tipItem: tipArray) {
      mTipItems.add(tipItem);
    }
    mWaitView = new TaxiWaitView(context);
    mWaitView.setClickListener(taxiWaitView);
    mTaxiOrder = order;
    mWaitTime = mTaxiOrder.getWaitConfigTime();
    mHandlerThread = new HandlerThread("UPDATE_PROGRESS");
    mHandlerThread.start();
    mHandler = new Handler(mHandlerThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case COUNT_DOWN: {
            iTaxiWaitView.waitConfigTime(mWaitTime);
            handleCountDownTime();
            break;
          }
        }
      }
    };
    initBroadcast();
    TaxiService.loopOrderStatus(context, true, mTaxiOrder.getOrderId());

    /** 初始化的时候 */
    int userSelectTip = FormDataProvider.getInstance().obtainTip();
    int position = userSelectTip == 0 ? 0 : mTipItems.indexOf(String.valueOf(userSelectTip));
    for (int i = position; position == 0 ? i > 0 : i >= 0; i--) {
      mTipItems.remove(i);
    }
  }

  private void handleCountDownTime() {
    long currentTime = System.currentTimeMillis(); // ms
    int waitTime = 0;
    if (mOrderCreateTime != 0) {
      waitTime = (int) ((currentTime - mOrderCreateTime) / 1000); // s
      mCurrentTime = waitTime;
      iTaxiWaitView.updateProgressSweep(mCurrentTime);
    }
    while (mCurrentTime < mWaitTime) {
      mCurrentTime++;
      UIThreadHandler.post(new Runnable() {
        @Override
        public void run() {
          iTaxiWaitView.countDown(mWaitTime, mCurrentTime);
        }
      });
      synchronized (lock) {
        try {
          lock.wait(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void initBroadcast() {
    mBroadManager = LocalBroadcastManager.getInstance(mContext);
    mReceiver = new BroadReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(CommonParams.COMMON_LOOPER_ORDER_STATUS);
    mBroadManager.registerReceiver(mReceiver, filter);
  }

  private void handleOrderStatus(OrderStatus status) {
    switch (status) {
      case RECEIVED: {
        TaxiRequest.taxiOrderDetail(UserProfile.getInstance(mContext).getUserId(),
            mTaxiOrder.getOrderId(), new IResponseListener<TaxiOrderDetail>() {
              @Override
              public void onSuccess(TaxiOrderDetail taxiOrderDetail) {
                mTaxiOrder.saveOrderInfo(taxiOrderDetail);
                iTaxiWaitView.onTripping(mTaxiOrder);
              }

              @Override
              public void onFail(int errCode, String errMsg) {

              }

              @Override
              public void onFinish(TaxiOrderDetail taxiOrderDetail) {

              }
            });

        break;
      }
      case CANCELED: {
        // 司机取消
        break;
      }
    }
  }

  /**
   * 获取添加的小费
   * @return
   */
  public List<String> getTipItems() {
    List<String> tip = new ArrayList<>();
    int userSelectTip = FormDataProvider.getInstance().obtainTip();
    for (int i = 0; i < mTipItems.size(); i++) {
      if (userSelectTip == 0 && i == 0) {
        tip.add(mTipItems.get(0));
      } else {
        tip.add(String.format(mContext.getString(R.string.taxi_end_pay_money), mTipItems.get(i)));
      }
    }
    return tip;
  }

  /**
   * 用户选择的小费
   * @param selectPosition
   * @return
   */
  public int getTip(int selectPosition) {
    // mTipItems 有可能第一条是 不加小费
    if (mTipItems.size() == mTipLength && selectPosition == 0) {
      FormDataProvider.getInstance().saveTip(0);
      return 0;
    }

    String userSelect = mTipItems.get(selectPosition);
    int tip = Integer.parseInt(userSelect);
    FormDataProvider.getInstance().saveTip(tip);

    for (int i = selectPosition; i >= 0; i--) {
      if (i == mTipItems.size() - 1) {
        continue;
      }
      mTipItems.remove(i);
    }
    return tip;
  }

  @Override
  public void cancelOrder(final boolean isShowFullForm) {
    // userid
    TaxiRequest.taxiCancelOrder(mTaxiOrder.getOrderId(), UserProfile.getInstance(mContext).getUserId(),
        "", new IResponseListener<TaxiOrderCancel>() {
      @Override
      public void onSuccess(TaxiOrderCancel taxiOrderCancel) {
        if (!isShowFullForm) {
          FormDataProvider.getInstance().clearData();
        }
        HomeDataProvider.getInstance().saveOrderDetail(null);
        iTaxiWaitView.cancelOrderSuccess(taxiOrderCancel);
      }

      @Override
      public void onFail(int errCode, String errMsg) {
      }

      @Override
      public void onFinish(TaxiOrderCancel taxiOrderCancel) {
        // 停止轮询服务
        TaxiService.stopService();
        iTaxiWaitView.cancelOrderFinish();
      }
    });
  }

  /**
   * 增加小费
   * @param tip
   */
  @Override
  public void addTip(int tip) {
    TaxiRequest.taxiAddTip(mTaxiOrder.getOrderId(), tip, new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        // 增加小费成功
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.taxi_wait_add_tip_success));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, mContext.getString(R.string.taxi_wait_add_tip_success), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFail(int errCode, String message) {
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.taxi_wait_error));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  @Override
  public void pay4Pickup() {
    TaxiRequest.taxiWaitPay4PickUp(mTaxiOrder.getOrderId(), new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.taxi_wait_pick_up));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, mContext.getString(R.string.taxi_wait_pick_up), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFail(int errCode, String errMsg) {
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.taxi_wait_error));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  private class BroadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (CommonParams.COMMON_LOOPER_ORDER_STATUS.equalsIgnoreCase(action)) {
        TaxiOrderStatus orderStatus = (TaxiOrderStatus) intent.getSerializableExtra(CommonParams.COMMON_LOOPER_ORDER);
        handleOrderStatus(OrderStatus.fromStateCode(orderStatus.getStatus()));
        if (mOrderCreateTime == 0) {
          mOrderCreateTime = orderStatus.getOrderCreateTime(); // 订单创建时间
          startCountDown();
        }
      }
    }
  }

  @Override
  public void addStartMarker() {
    Address start = FormDataProvider.getInstance().obtainStartAddress();
    if (start != null) {
      MarkerOption startOption = new MarkerOption();
      startOption.position = start.mAdrLatLng;
      startOption.title = start.mAdrDisplayName;
      startOption.descriptor = BitmapDescriptorFactory
          .fromResources(mContext.getResources(), R.drawable.base_map_start_icon);
      iTaxiWaitView.addMarker(startOption);
    }
  }

  @Override
  public void startCountDown() {
    mHandler.sendEmptyMessage(COUNT_DOWN);
  }

  @Override
  public void stopCountDown() {
    mHandlerThread.quit();
  }

  @Override
  public IWaitView getWaitView() {
    return mWaitView;
  }

  public void release() {
    if (mBroadManager != null) {
      mBroadManager.unregisterReceiver(mReceiver);
    }
  }
}
