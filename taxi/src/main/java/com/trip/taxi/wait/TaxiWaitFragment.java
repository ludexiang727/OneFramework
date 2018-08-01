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
import com.one.framework.dialog.BottomSheetDialog;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.map.map.MarkerOption;
import com.trip.base.common.CommonParams;
import com.trip.base.common.CommonParams.Service;
import com.trip.base.wait.WaitFragment;
import com.trip.base.wait.IWaitView;
import com.trip.taxi.R;
import com.trip.taxi.net.model.TaxiOrder;
import com.trip.taxi.net.model.TaxiOrderCancel;
import com.trip.taxi.service.ServiceFragment;
import com.trip.taxi.wait.presenter.TaxiWaitPresenter;
import java.util.Locale;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitFragment extends WaitFragment implements ITaxiWaitView {

  private LoadingView mWaitLoadingView;
  private TextView mWaitSeconds;
  private IWaitView mWaitView;
  private SupportDialogFragment mCancelDialog;
  private SupportDialogFragment mNoneDriverDlg;
  private TaxiOrder mTaxiOrder;
  private BottomSheetDialog mTipDlg;
  private View mWaitTopView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mTaxiOrder = (TaxiOrder) bundle.getSerializable(Service.ORDER);
      isFromHistory = bundle.getBoolean(Service.FROM_HISTORY);
      isRecovery = bundle.getBoolean(CommonParams.COMMON_CRASH_RECOVERY, false);
    }
    mWaitPresenter = new TaxiWaitPresenter(getActivity(), mTaxiOrder, this);
    mWaitView = mWaitPresenter.getWaitView();
    mTopbarView.setTitle(R.string.taxi_wait_page_title);
    mTopbarView.setLeft(/*isFromHistory ? R.drawable.one_top_bar_back_selector : */0);
    mMap.removeDriverLine();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mWaitTopView = LayoutInflater.from(getContext()).inflate(R.layout.taxi_wait_top_view_layout, null);
    mWaitLoadingView = (LoadingView) mWaitTopView.findViewById(R.id.taxi_wait_loading_view);
    mWaitSeconds = (TextView) mWaitTopView.findViewById(R.id.taxi_wait_count_down);
    attachToTopContainer(mWaitTopView);
  }

  @Override
  public void waitConfigTime(int waitTime) {
    mWaitLoadingView.setConfigWaitTime(waitTime);
  }

  @Override
  public void updateProgressSweep(int sweep) {
    synchronized (mWaitLoadingView) {
      mWaitLoadingView.updateProgressSweep(sweep);
    }
  }

  @Override
  public void addMarker(MarkerOption option) {
    mMap.addMarker(option);
  }

  private void onTipClick() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.taxi_tip_dialog_layout, null);
    final WheelView tipWheel = (WheelView) view.findViewById(R.id.taxi_wheel_view_tip);
    tipWheel.setItems(mWaitPresenter.getTipItems(), 0);

    mTipDlg = showBottomDialog(view, new OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = tipWheel.getSelectedPosition();
        int tip = mWaitPresenter.getTip(position);
        mWaitView.addTip(tip);
        mWaitPresenter.addTip(tip);
      }
    });
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.taxi_wait_add_tip_layout) {
      onTipClick();
    } else if (id == R.id.taxi_wait_cancel_order) {
      cancelOrder();
    } else if (id == R.id.taxi_wait_pick_up_checkbox) {
      mWaitPresenter.pay4Pickup();
    }
  }

  @Override
  public void onTripping(TaxiOrder order) {
    mMap.stopRadarAnim();
    mWaitLoadingView.release();
    Bundle bundle = new Bundle();
    bundle.putSerializable(Service.ORDER, order);
    forwardWithPop(ServiceFragment.class, bundle);
  }

  @Override
  protected boolean isAddLeftClick() {
    return false;
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
            mWaitPresenter.cancelOrder(false);
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mCancelDialog = builder.create();
    mCancelDialog.show(getFragmentManager(), "");
  }

  /**
   * 无司机接单
   */
  private void noneDriverReceiver() {
    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(
        getActivity()).setTitle(getString(R.string.taxi_support_dlg_title))
        .setMessage(getString(R.string.taxi_none_driver_receive))
        .setNegativeButton(getString(R.string.taxi_wait_cancel_order), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mWaitPresenter.cancelOrder(false);
          }
        })
        .setPositiveButton(getString(R.string.taxi_none_driver_reorder), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mNoneDriverDlg.dismiss();
            mWaitPresenter.cancelOrder(true);
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mNoneDriverDlg = builder.create();
    mNoneDriverDlg.show(getFragmentManager(), "");
  }

  @Override
  public void cancelOrderSuccess(TaxiOrderCancel orderCancel) {
    mMap.stopRadarAnim();
    mWaitPresenter.stopCountDown();
    finishSelf();
  }

  @Override
  public void cancelOrderFinish() {
    mMap.stopRadarAnim();
    mWaitPresenter.stopCountDown();
    finishSelf();
  }

  @Override
  public void countDown(int totalTime, int count) {
    if (isAdded()) {
      int min = count / 60;
      int second = count % 60;
      String waitTime = String.format(Locale.CHINA, "%02d:%02d", min, second);
      mWaitSeconds.setText(waitTime);
      if (count == totalTime) {
        if (mTipDlg != null && mTipDlg.isShowing()) {
          mTipDlg.dismiss();
        }
        detachFromTopContainer(mWaitTopView);
        noneDriverReceiver();
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mWaitPresenter.release();
  }
}
