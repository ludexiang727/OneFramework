package com.trip.taxi.wait.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.UIThreadHandler;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.base.wait.presenter.AbsWaitPresenter;
import com.trip.taxi.R;
import com.trip.taxi.common.Common;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.TaxiService;
import com.trip.taxi.net.model.TaxiOrderDetail;
import com.trip.taxi.net.model.TaxiOrderStatus;
import com.trip.taxi.states.Status.OrderStatus;
import com.trip.taxi.wait.ITaxiWaitView;
import com.trip.taxi.wait.TaxiWaitView;
import java.util.ArrayList;
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
  private int mCurrentTime = -1;
  private byte[] lock = new byte[0];
  private String[] mTipArray;

  private ITaxiWaitView iTaxiWaitView;
  private LocalBroadcastManager mBroadManager;
  private BroadReceiver mReceiver;

  public TaxiWaitPresenter(Context context, final ITaxiWaitView taxiWaitView) {
    mContext = context;
    iTaxiWaitView = taxiWaitView;
    mTipArray = context.getResources().getStringArray(R.array.TaxiTip);
    mWaitView = new TaxiWaitView(context);
    mWaitView.setClickListener(taxiWaitView);
    mTaxiOrder = (TaxiOrder) FormDataProvider.getInstance().obtainOrder();
    mWaitTime = mTaxiOrder.getWaitConfigTime();
    mHandlerThread = new HandlerThread("UPDATE_PROGRESS");
    mHandlerThread.start();
    mHandler = new Handler(mHandlerThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case COUNT_DOWN: {
            while (mCurrentTime < mWaitTime) {
              float sweepAngle = (mCurrentTime * 1f / mWaitTime) * 360;
              iTaxiWaitView.updateSweepAngle(sweepAngle);
              mCurrentTime++;
              UIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                  iTaxiWaitView.countDown(mWaitTime - mCurrentTime);
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
            break;
          }
        }
      }
    };
    initBroadcast();
    TaxiService.loopOrderStatus(context, true, mTaxiOrder.getOrderId());
  }

  private void initBroadcast() {
    mBroadManager = LocalBroadcastManager.getInstance(mContext);
    mReceiver = new BroadReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Common.COMMON_LOOPER_ORDER_STATUS);
    mBroadManager.registerReceiver(mReceiver, filter);
  }

  public List<String> getTipItems() {
    int userSelectTip = FormDataProvider.getInstance().obtainTip();
    List<String> items = new ArrayList<>();
    int position = userSelectTip == 0 ? 0 : userSelectTip / 2 + 1;
    for (int i = position; i < mTipArray.length; i++) {
      if (i == 0) {
        items.add(mTipArray[i]);
      } else {
        items.add(mTipArray[i] + "元");
      }
    }
    return items;
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
              public void onFail(TaxiOrderDetail taxiOrderDetail) {

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

  public int getTip(int selectPosition) {
    if (selectPosition == 0) {
      return 0;
    }
    int tip = Integer.parseInt(mTipArray[selectPosition]);
    FormDataProvider.getInstance().saveTip(tip);
    return tip;
  }

  @Override
  public void cancelOrder() {
    // userid
    TaxiRequest.taxiCancelOrder(mTaxiOrder.getOrderId(), UserProfile.getInstance(mContext).getUserId(),
        "", new IResponseListener<TaxiOrderCancel>() {
      @Override
      public void onSuccess(TaxiOrderCancel taxiOrderCancel) {
        iTaxiWaitView.cancelOrderSuccess(taxiOrderCancel);
        TaxiService.stopService(mContext);
      }

      @Override
      public void onFail(TaxiOrderCancel taxiOrderCancel) {
      }

      @Override
      public void onFinish(TaxiOrderCancel taxiOrderCancel) {

      }
    });
  }

  @Override
  public void addTip(int tip) {
    TaxiRequest.taxiAddTip(mTaxiOrder.getOrderId(), tip, new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {

      }

      @Override
      public void onFail(BaseObject baseObject) {

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

      }

      @Override
      public void onFail(BaseObject baseObject) {

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
      if (Common.COMMON_LOOPER_ORDER_STATUS.equalsIgnoreCase(action)) {
        TaxiOrderStatus orderStatus = (TaxiOrderStatus) intent.getSerializableExtra(Common.COMMON_LOOPER_ORDER);
        handleOrderStatus(OrderStatus.fromStateCode(orderStatus.getStatus()));
      }
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
