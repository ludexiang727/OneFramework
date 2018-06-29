package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.map.model.BestViewModel;
import com.test.demo.R;
import com.trip.base.page.BaseFragment;

/**
 * Created by ludexiang on 2018/4/20.
 */

public class FirstTestFragment extends BaseFragment {

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.sub_view_fragment_layout, container, true);
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {

  }

  @Override
  protected void mapClearElement() {

  }
}
