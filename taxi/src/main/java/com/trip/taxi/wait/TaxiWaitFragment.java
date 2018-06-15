package com.trip.taxi.wait;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.wheelview.WheelView;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.map.log.Logger;
import com.trip.base.page.WaitFragment;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.taxi.R;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.service.ServiceFragment;
import com.trip.taxi.wait.presenter.TaxiWaitPresenter;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitFragment extends WaitFragment implements ITaxiWaitView {

  private LoadingView mWaitLoadingView;
  private TextView mWaitSeconds;
  private IWaitView mWaitView;
  private SupportDialogFragment mCancelDialog;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mWaitPresenter = new TaxiWaitPresenter(getContext(), this);
    mWaitView = mWaitPresenter.getWaitView();
    mTopbarView.setTitle(R.string.taxi_wait_page_title);
    mTopbarView.setLeft(0);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    View topView = LayoutInflater.from(getContext())
        .inflate(R.layout.taxi_wait_top_view_layout, null);
    mWaitLoadingView = (LoadingView) topView.findViewById(R.id.taxi_wait_loading_view);
    mWaitSeconds = (TextView) topView.findViewById(R.id.taxi_wait_count_down);
    attachToTopContainer(topView);
    mWaitPresenter.startCountDown();
  }

  @Override
  public void updateSweepAngle(float sweepAngle) {
    mWaitLoadingView.setSweepAngle(sweepAngle);
  }

  private void onTipClick() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.taxi_tip_dialog_layout, null);
    final WheelView tipWheel = (WheelView) view.findViewById(R.id.taxi_wheel_view_tip);
    tipWheel.setItems(mWaitPresenter.getTipItems(), 0);

    showBottomDialog(view, new OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = tipWheel.getSelectedPosition();
        int tip = mWaitPresenter.getTip(position);
        mWaitView.addTip(tip);
      }
    });
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.taxi_wait_add_tip_layout) {
//      onTipClick();
      onTripping();
    } else if (id == R.id.taxi_wait_cancel_order) {
      cancelOrder();
    }
  }

  @Override
  public void onTripping() {
    mMap.stopRadarAnim();
    forward(ServiceFragment.class);
  }

  /**
   * 拦截返回键
   * @return
   */
  @Override
  public boolean onBackPressed() {
    return true;
  }

  private void cancelOrder() {
    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(
        getActivity()).setTitle(getString(R.string.taxi_wait_cancel_order))
        .setMessage(getString(R.string.taxi_cancel_confirm_msg))
        .setNegativeButton(getString(R.string.one_cancel), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCancelDialog.dismiss();
          }
        })
        .setPositiveButton(getString(R.string.one_confirm), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCancelDialog.dismiss();
            mWaitPresenter.cancelOrder();
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mCancelDialog = builder.create();
    mCancelDialog.show(getFragmentManager(), "");
  }

  @Override
  public void cancelOrderSuccess(TaxiOrderCancel orderCancel) {
    FormDataProvider.getInstance().clearData();
    mMap.stopRadarAnim();
    mWaitPresenter.stopCountDown();
    onBackHome();
  }

  @Override
  public void countDown(int count) {
    if (isAdded()) {
      String waitTime = String.format(getString(R.string.taxi_wait_driver_time), count);
      mWaitSeconds.setText(waitTime);
      if (count == 0) {
        // 没有司机接单 回全表单页面 todo

      }
    }
  }
}
