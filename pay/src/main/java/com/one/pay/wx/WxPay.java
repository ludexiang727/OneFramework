package com.one.pay.wx;

import android.content.Context;
import com.one.map.log.Logger;
import com.one.pay.model.PayInfo;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WxPay {
  private static final String APP_ID = "wx822295c9333f22d8"; // 暂时先用
  private Context mContext;
  private IWXAPI mWxApi;

  public WxPay(Context context) {
    mContext = context;
    regToWx();
  }
  public void regToWx() {
    mWxApi = WXAPIFactory.createWXAPI(mContext, null, true);
    mWxApi.registerApp(APP_ID);
  }

  public void wxPay(PayInfo info) {
    Logger.e("ldx", "invoke wxPay >>>>>> " + info);
    PayReq payReq = new PayReq();
    payReq.appId = info.getAppId();
    payReq.partnerId = info.getPartnerId();
    payReq.prepayId = info.getPrePayId();
    payReq.packageValue = info.getPackageName();
    payReq.nonceStr = info.getNoncestr();
    payReq.timeStamp = String.valueOf(info.getTimeStamp());
    payReq.sign = info.getSign();
    mWxApi.sendReq(payReq);
  }
}
