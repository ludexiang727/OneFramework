package com.one.pay.wx;

import android.content.Context;
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
    mWxApi = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
    mWxApi.registerApp(APP_ID);
  }

  public void wxPay() {
    PayReq req = new PayReq();
    req.appId = APP_ID;
    req.prepayId = "";
//    req.
    mWxApi.sendReq(req);
  }
}
