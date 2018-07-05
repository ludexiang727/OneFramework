package com.trip.taxi.application;

import android.app.Application;
import com.mobike.mobikeapp.util.SafeUtil;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.base.ApplicationDelegate;
import com.one.framework.net.Api;
import com.one.framework.net.HeaderParams;
import com.one.framework.net.NetworkConfig;
import com.one.framework.net.base.INetworkConfig;
import com.trip.base.application.BaseApplicationDelegate;
import java.io.InputStream;

/**
 * Created by ludexiang on 2018/6/5.
 */
@ServiceProvider(value = ApplicationDelegate.class, alias = "taxi")
public class TaxiApplicationDelegate extends BaseApplicationDelegate {

  @Override
  public void onCreate(Application application) {
    try {
      InputStream inputStream = application.getAssets().open("cert/taxi.bks");
      INetworkConfig networkConfig = new NetworkConfig(application, new HeaderParams(application), inputStream,
          SafeUtil.getBKSString(), true);
      Api.initNetworkConfig(networkConfig);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
