package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.base.BaseFragment;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.map.model.BestViewModel;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/4/16.
 */

@ServiceProvider(value = Fragment.class, alias = "taxi")
public class TaxiFragment extends BaseFragment {

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.taxi_main_layout, container, true);
    return view;
  }


  @Override
  protected void boundsLatlng(BestViewModel model) {

  }
}
