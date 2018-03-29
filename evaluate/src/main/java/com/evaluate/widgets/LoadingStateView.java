package com.evaluate.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.evaluate.R;
import com.evaluate.loading.CircularProgressBar;


/**
 * loading框
 */
public class LoadingStateView extends FrameLayout {

  private CircularProgressBar progressBar;
  private TextView loadingTv;

  public LoadingStateView(Context context) {
    super(context);
    initView();
  }

  public LoadingStateView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    View root = inflate(getContext(), R.layout.oc_loading_view_layout, this);
    progressBar = (CircularProgressBar) root.findViewById(R.id.evaluate_round_progress_bar);
    loadingTv = (TextView) root.findViewById(R.id.evaluate_tv_loading);
  }


  /**
   * 设置显示文案
   */
  public void setText(String loadingText) {
    if (TextUtils.isEmpty(loadingText)) {
      loadingTv.setVisibility(View.GONE);
    } else {
      loadingTv.setText(loadingText);
      loadingTv.setVisibility(View.VISIBLE);
    }
  }

  public void changeState(State state) {
    if (state == State.SUCCESS_STATE) {
      progressBar.changeToSuccess();
    } else {
      progressBar.changeToLoading();
    }
  }

  /**
   * 设置显示文案
   */
  public void setText(@StringRes int loadingText) {
    loadingTv.setText(loadingText);
    loadingTv.setVisibility(View.VISIBLE);
  }

  public enum State {
    LOADING_STATE, SUCCESS_STATE;
  }
}
