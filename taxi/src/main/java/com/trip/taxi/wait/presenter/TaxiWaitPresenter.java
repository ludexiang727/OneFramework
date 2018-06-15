package com.trip.taxi.wait.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.UIThreadHandler;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.base.wait.presenter.AbsWaitPresenter;
import com.trip.taxi.R;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.TaxiService;
import com.trip.taxi.wait.ITaxiWaitView;
import com.trip.taxi.wait.TaxiWaitView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitPresenter extends AbsWaitPresenter {

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

  public TaxiWaitPresenter(Context context, final ITaxiWaitView taxiWaitView) {
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
    TaxiService.loopOrderStatus(context, true, mTaxiOrder.getOrderId());
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
    TaxiRequest.taxiCancelOrder(mTaxiOrder.getOrderId(), "9529324938770193334272033522",
        "", new IResponseListener<TaxiOrderCancel>() {
      @Override
      public void onSuccess(TaxiOrderCancel taxiOrderCancel) {
        iTaxiWaitView.cancelOrderSuccess(taxiOrderCancel);
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
}
