package com.evaluate.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.evaluate.R;

public class CardTitleView extends RelativeLayout {

  private View closeIconContainer;
  private TextView titleTv;
  private CardTitleCloseBtnListener listener;

  public CardTitleView(Context context) {
    this(context, null);
  }

  public CardTitleView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    View root = LayoutInflater.from(getContext())
        .inflate(R.layout.oc_include_card_title, this, true);
    closeIconContainer = root.findViewById(R.id.iv_close_icon_container);
    titleTv = (TextView) root.findViewById(R.id.tv_title);

    closeIconContainer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (listener != null) {
          listener.onCloseBtnClick();
        }
      }
    });
  }

  public void setTitle(String title) {
    titleTv.setText(title);
  }

  public void setTitle(@StringRes int title) {
    titleTv.setText(title);
  }

  public void setCloseIconListener(final CardTitleCloseBtnListener listener) {
    this.listener = listener;
  }

  /**
   * 设置关闭按钮是否可以被点击。
   */
  public void setClosable(boolean closable) {
    closeIconContainer.setEnabled(closable);
  }

  /**
   * 是否可以关闭
   */
  public boolean isCloseAble() {
    return closeIconContainer.isEnabled();
  }

  public interface CardTitleCloseBtnListener {

    void onCloseBtnClick();
  }

}
