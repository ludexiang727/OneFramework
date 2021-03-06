package com.one.pay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import com.one.map.log.Logger;
import com.one.pay.dialog.PayBottomDlg;
import com.one.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.pay.model.PayInfo;
import com.one.pay.model.PayModel;
import java.lang.ref.SoftReference;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class Pay {

  private SoftReference<Activity> mReference;

  private Pay(Activity activity) {
    mReference = new SoftReference<Activity>(activity);
  }

  public static Pay getInstance(Activity activity) {
    return PayFactory.instance(activity);
  }

  private final static class PayFactory {

    private static Pay sPay;

    public static Pay instance(Activity activity) {
      if (sPay == null) {
        sPay = new Pay(activity);
      }
      return sPay;
    }
  }

  public void showPayBottom(PayModel model, IPayCallback listener) {
    if (mReference.get() == null) {
      return;
    }
    PayBottomDlg payBottomDlg = new PayBottomDlg(mReference.get(), listener, model);
    payBottomDlg.setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        // 支付成功 将mContext = null 否则 内存泄露
//        mContext = null;
//        mReference.clear();
      }
    });
    payBottomDlg.show();
  }

  /**
   * 直接发起支付
   * @param info
   * @param listener
   */
  public void pay(PayInfo info, IPayCallback listener) {
    Logger.e("Pay", "Pay ..... " + mReference.get());
    if (mReference.get() == null) {
      return;
    }
    IPay pay = new PayBottomDlg(mReference.get(), listener, info);
    Logger.e("Pay", "Pay ..... " + info.getPayChannel() + " info >>>> " + info);
    switch (info.getPayChannel()) {
      case IPay.PAY_ZFB: {
        pay.onAliPay();
        break;
      }
      case IPay.PAY_WX: {
        pay.onWxPay();
        break;
      }
    }
  }

  public void updatePayBottomList(int position) {
//    payBottomDlg.updatePayList(position);
  }
}
