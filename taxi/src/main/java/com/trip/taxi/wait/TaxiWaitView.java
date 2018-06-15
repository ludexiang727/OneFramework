package com.trip.taxi.wait;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.utils.UIUtils;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.taxi.R;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitView extends RelativeLayout implements IWaitView, View.OnClickListener {

  private Context mContext;

  private LinearLayout mTipLayout;
  private TextView mTip;
  private CheckBox mCheckBox;
  private TextView mCancelOrder;
  private IClickListener mClickListener;


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
    mTipLayout = (LinearLayout) view.findViewById(R.id.taxi_wait_add_tip_layout);
    mTip = (TextView) view.findViewById(R.id.taxi_wait_tip);
    mCheckBox = (CheckBox) view.findViewById(R.id.taxi_wait_pick_up_checkbox);
    mCancelOrder = (TextView) view.findViewById(R.id.taxi_wait_cancel_order);

    addTip(FormDataProvider.getInstance().obtainTip());
    mCancelOrder.setOnClickListener(this);
    mTipLayout.setOnClickListener(this);
  }

  @Override
  public void setClickListener(IClickListener listener) {
    mClickListener = listener;
  }

  @Override
  public void onClick(View v) {
    if (mClickListener != null) {
      mClickListener.onClick(v);
    }
  }

  @Override
  public void addTip(int fee) {
    if (fee == 0) {
      mTip.setText(getContext().getString(R.string.taxi_thx_money));
    } else {
      mTip.setText(UIUtils.highlight(String.format(mContext.getString(R.string.taxi_thx_money_format), fee), Color.parseColor("#f05b48")));
    }
  }

  @Override
  public View getWaitView() {
    return this;
  }
}
