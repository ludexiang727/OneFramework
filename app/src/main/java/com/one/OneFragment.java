package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.base.BaseFragment;
import com.one.framework.api.annotation.ServiceProvider;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/3/27.
 */

@ServiceProvider(value = Fragment.class, alias = "flash")
public class OneFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.one_fragment_layout, null);
  }
}
