package com.one.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.one.framework.app.base.BizEntranceFragment;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IMarkerClickCallback;
import com.one.map.map.element.IMarker;
import com.one.map.model.BestViewModel;
import com.one.widget.ContainerRelativeLayout;
import com.one.widget.ContainerRelativeLayout.IHeightChangeListener;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BaseFragment extends BizEntranceFragment implements IMarkerClickCallback,
    IHeightChangeListener {
  /**
   * 之所以加入Base Parent 为了计算高度已实现地图最佳view
   */
  private ContainerRelativeLayout mTopContainer;
  private ContainerRelativeLayout mBottomContainer;
  private boolean isAttached = true;
  /**
   * 缓存top bottom container width and height
   */
  protected int[] mTopRect = new int[2];
  protected int[] mBottomRect = new int[2];

  /**
   * 通过此方法创建的View会attach to bottom container
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  protected abstract View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState);

  /**
   * 最佳View 框对应的元素
   * @param bestView
   */
  protected abstract void boundsLatlng(BestViewModel bestView);

  @Nullable
  @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View root = inflater.inflate(R.layout.base_fragment_layout, null);
    mTopContainer = root.findViewById(R.id.base_top_container);
    mBottomContainer = root.findViewById(R.id.base_bottom_container);
    View view = onCreateViewImpl(inflater, mBottomContainer, savedInstanceState);
    if (mBusContext == null) {
      parseBundle();
    }
    if (mBottomContainer.getChildCount() <= 0) {
      isAttached = false;
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
      mBottomContainer.addView(view, 0, params);
    }

    mTopContainer.setHeightListener(this);
    mBottomContainer.setHeightListener(this);
    mBusContext.getMap().setIMarkerClickCallback(this);
    return root;
  }

  private void parseBundle() {
    Bundle bundle = getArguments();
    if (bundle != null) {
      mBusContext = (IBusinessContext) bundle.getSerializable("BusinessContext");
    }
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onMarkerClick(IMarker marker) {
    Logger.e("ldx", "Top level receive marker click");
  }

  @Override
  public void onHeightChange() {
    reCalculateHeight();
  }

  /**
   * 刷新最佳view
   */
  private void toggleMapView() {
    Logger.e("ldx", "TopHeight " + mTopRect[1] + " BottomHeight " + mBottomRect[1]);
    BestViewModel bestView = new BestViewModel();
    bestView.padding.left += 0;
    bestView.padding.top += mTopRect[1];
    bestView.padding.right += 0;
    bestView.padding.bottom += mBottomRect[1];
    boundsLatlng(bestView);
    mBusContext.getMap().doBestView(bestView);
  }
  /**
   * attach to top container and invoke onSizeChange and callback reCalculate
   * @param layout
   */
  protected void attachToTopContainer(View layout) {
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    mTopContainer.addView(layout, 0, params);
  }

  /**
   * 重新测量子view高度
   */
  private void reCalculateHeight() {
    Logger.e("ldx", "BaseFragment reCalculateHeight>>>>>>>");
    mTopContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (mTopContainer.getChildCount() > 0) {
          int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          mTopContainer.measure(width, height);
          mTopRect[0] = mTopContainer.getMeasuredWidth();
          mTopRect[1] = mBusContext.getTopbar().getTopbarHeight() + mTopContainer.getMeasuredHeight();
        } else {
          mTopRect[0] = UIUtils.getScreenWidth(getActivity());
          mTopRect[1] = mBusContext.getTopbar().getTopbarHeight();
        }

        toggleMapView();
        mTopContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    });

    mBottomContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        if (isAttached) {
          mBottomContainer.measure(width, height);
          mBottomRect[0] = mBottomContainer.getMeasuredWidth();
          mBottomRect[1] = mBottomContainer.getMeasuredHeight();
        } else {
          View view = mBottomContainer.getChildAt(0);
          view.measure(width, height);
          mBottomRect[0] = view.getMeasuredWidth();
          mBottomRect[1] = view.getMeasuredHeight();
        }
        toggleMapView();
        mBottomContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mBusContext.getMap().clearElements();
  }
}
