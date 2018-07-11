package com.one.pay.wx.wxapi;

import android.app.Activity;
import com.one.framework.log.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

  @Override
  public void onReq(BaseReq req) {

  }

  @Override
  public void onResp(BaseResp resp) {
    Logger.d("Pay", "onPayFinish, errCode = " + resp.errCode);
    if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//      AlertDialog.Builder builder = new AlertDialog.Builder(this);
//      builder.setTitle(R.string.app_tip);
//      builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//      builder.show();
    }
  }
}
