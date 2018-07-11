package com.trip.taxi.wait;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.utils.UIUtils;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.wait.IWaitView;
import com.trip.base.widget.BaseLinearLayout;
import com.trip.base.widget.BaseRelativeLayout;
import com.trip.taxi.R;

/**
 * Created by ludexiang on 2018/6/13.
 */

public class TaxiWaitView extends BaseLinearLayout implements IWaitView, View.OnClickListener {

  private Context mContext;

  private LinearLayout mTipLayout;
  private TextView mTip;
  private CheckBox mCheckBox;
  private RelativeLayout mCancelOrder;
  private IClickListener mClickListener;
  private SupportDialogFragment dialogFragment;

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
    mCancelOrder = (RelativeLayout) view.findViewById(R.id.taxi_wait_cancel_order);

    addTip(FormDataProvider.getInstance().obtainTip());

    mCheckBox.setChecked(FormDataProvider.getInstance().isPay4PickUp());
    addTip(FormDataProvider.getInstance().obtainTip());
    mCancelOrder.setOnClickListener(this);
    mTipLayout.setOnClickListener(this);

    mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean isPay4PickUp = FormDataProvider.getInstance().isPay4PickUp();
        if (isPay4PickUp) {
          // 乘客已经选择达标来接 则不能取消
          showTip();
          mCheckBox.setChecked(isPay4PickUp);
          mCheckBox.setClickable(false);
          return;
        }
        FormDataProvider.getInstance().savePick4Up(true);
        mCheckBox.setChecked(isChecked);

        if (mClickListener != null) {
          mClickListener.onClick(mCheckBox);
        }
      }
    });
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
      setClipChildren(false);
      Drawable drawable = context.getDrawable(R.drawable.base_common_round_rect);
      setOutsideBackground((NinePatchDrawable) drawable);
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.taxi_wait_view_height));
      params.leftMargin = params.rightMargin = margin;
      setLayoutParams(params);
    } else {
      setBackgroundColor(getResources().getColor(android.R.color.white));
    }
  }

  private void showTip() {
    final SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext())
        .setTitle(mContext.getString(R.string.taxi_support_dlg_title))
        .setMessage(mContext.getString(R.string.taxi_tell_driver_pick_up))
        .setPositiveButton(mContext.getString(R.string.taxi_wait_checkbox_i_know),
            new OnClickListener() {
              @Override
              public void onClick(View v) {
                dialogFragment.dismiss();
              }
            })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    dialogFragment = builder.create();
    if (mContext instanceof FragmentActivity) {
      dialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
    }
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
