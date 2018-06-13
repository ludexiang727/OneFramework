package com.trip.taxi.wait.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.UiThread;
import com.one.framework.utils.UIThreadHandler;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.base.wait.presenter.AbsWaitPresenter;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.wait.ITaxiWaitView;
import com.trip.taxi.wait.TaxiWaitView;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitPresenter extends AbsWaitPresenter {

  private static final int COUNT_DOWN = 1;
  private ITaxiWaitView mWaitView;
  private TaxiOrder mTaxiOrder;
  private int mWaitTime;
  private HandlerThread mHandlerThread;
  private Handler mHandler;
  private int mCurrentTime = -1;
  private byte[] lock = new byte[0];


  public TaxiWaitPresenter(Context context) {
    mWaitView = new TaxiWaitView(context);
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
            while (mCurrentTime <= mWaitTime) {
              float sweepAngle = (mCurrentTime * 1f / mWaitTime) * 360;
              mWaitView.updateSweepAngle(sweepAngle);
              UIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                  mWaitView.countDown(mWaitTime - mCurrentTime);
                }
              });
              mCurrentTime++;
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
  }

  @Override
  public void startCountDown() {
    mHandler.sendEmptyMessage(COUNT_DOWN);
  }

  public void stopCountDown() {
    mHandlerThread.quit();
  }

  @Override
  public IWaitView getWaitView() {
    return mWaitView;
  }
}
