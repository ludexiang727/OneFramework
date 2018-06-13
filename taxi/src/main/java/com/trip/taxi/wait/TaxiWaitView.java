package com.trip.taxi.wait;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.app.widget.LoadingView;
import com.trip.taxi.R;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitView extends RelativeLayout implements ITaxiWaitView {

  private Context mContext;
  private LoadingView mWaitLoadingView;
  private TextView mWaitSeconds;


  public TaxiWaitView(Context context) {
    this(context, null);
  }

  public TaxiWaitView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TaxiWaitView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    View view = LayoutInflater.from(context).inflate(R.layout.taxi_wait_view_layout, this, true);
    mWaitLoadingView = (LoadingView) view.findViewById(R.id.taxi_wait_loading_view);
    mWaitSeconds = (TextView) view.findViewById(R.id.taxi_wait_count_down);
  }

  @Override
  public void updateSweepAngle(float sweepAngle) {
    mWaitLoadingView.setSweepAngle(sweepAngle);
  }

  @Override
  public void countDown(int count) {
    String waitTime = String.format(mContext.getString(R.string.taxi_wait_driver_time), count);
    mWaitSeconds.setText(waitTime);
  }

  @Override
  public View getWaitView() {
    return this;
  }
}
