package com.trip.base.cancel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.trip.base.page.BaseFragment;

public abstract class CancelFragment extends BaseFragment {
  protected boolean isFromHistory;
  protected ImageView mIM;
  protected ImageView mPhone;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }


  @Override
  protected void mapClearElement() {

  }
}
