package com.trip.base.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.one.framework.app.base.BizEntranceFragment;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IMarkerClickCallback;
import com.one.map.map.element.IMarker;
import com.one.map.model.BestViewModel;
import com.trip.base.R;
import com.trip.base.widget.BottomViewLayout;
import com.trip.base.widget.ContainerRelativeLayout;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BaseFragment extends BizEntranceFragment implements IMarkerClickCallback {

  /**
   * 之所以加入Base Parent 为了计算高度已实现地图最佳view
   */
  private ContainerRelativeLayout mTopContainer;
  private BottomViewLayout mBottomContainer;
  private boolean isAttached = true;
  private static final int REFRESH_MAP = 0X101;
  /**
   * 缓存top bottom container width and height
   */
  protected int[] mTopRect = new int[2];
  protected int[] mBottomRect = new int[2];

  private static boolean isFirstLayoutDone = false;

  private ImageView mRefreshMapView;

  /**
   * 通过此方法创建的View会attach to bottom container
   */
  protected abstract View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState);

  /**
   * 最佳View 框对应的元素
   */
  protected abstract void boundsLatlng(BestViewModel bestView);

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Nullable
  @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View root = inflater.inflate(R.layout.base_fragment_layout, null);
    mTopContainer = root.findViewById(R.id.base_top_container);
    mBottomContainer = root.findViewById(R.id.base_bottom_container);
    mRefreshMapView = root.findViewById(R.id.base_refresh_map);

    View view = onCreateViewImpl(inflater, mBottomContainer, savedInstanceState);
    if (mBusContext == null) {
      parseBundle();
    }
    if (mBottomContainer.getChildCount() <= 0) {
      isAttached = false;
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
      mBottomContainer.addView(view, 0, params);
    }
    mBusContext.getMap().displayMyLocation();
    mBusContext.getMap().setIMarkerClickCallback(this);
    mRefreshMapView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleMapView();
      }
    });
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
    // 开启子线程去测量高度
    final HandlerThread thread = new HandlerThread("MAP_REFRESH");
    thread.start();
    Handler handler = new Handler(thread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case REFRESH_MAP: {
            if (!isFirstLayoutDone) {
              isFirstLayoutDone = true;
              toggleMapView();
            }
            reCalculateHeight();
            thread.quit();
            break;
          }
        }
      }
    };
    handler.sendEmptyMessageDelayed(REFRESH_MAP, 100);
  }

  @Override
  public void onMarkerClick(IMarker marker) {
    Logger.e("ldx", "Top level receive marker click");
  }

  /**
   * 刷新最佳view
   */
  protected void toggleMapView() {
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
   */
  protected void attachToTopContainer(View layout) {
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);
    mTopContainer.addView(layout, 0, params);
    reCalculateHeight();
  }

  /**
   * 重新测量子view高度
   */
  protected void reCalculateHeight() {
    int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

    if (mTopContainer.getChildCount() > 0) {
      mTopContainer.measure(width, height);
      mTopRect[0] = mTopContainer.getMeasuredWidth();
      mTopRect[1] = mBusContext.getTopbar().getTopbarHeight() + mTopContainer.getMeasuredHeight();
    } else {
      mTopRect[0] = UIUtils.getScreenWidth(getActivity());
      mTopRect[1] = mBusContext.getTopbar().getTopbarHeight();
    }

    if (isAttached) {
      mBottomContainer.measure(width, height);
      mBottomRect[0] = mBottomContainer.getWidth(); // mBottomContainer 宽度 由于是match_parent 故获得的宽度为屏幕宽度
      mBottomRect[1] = mBottomContainer.getMeasuredHeight(); // 包含View margin
    } else {
      View view = mBottomContainer.getChildAt(0);
      view.measure(width, height);
      mBottomRect[0] = view.getWidth();
      mBottomRect[1] = view.getHeight();
    }
    toggleMapView();
  }

  protected void reLayoutLocationPosition(int viewHeight) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRefreshMapView.getLayoutParams();
    params.bottomMargin = viewHeight == 0 ? -mRefreshMapView.getHeight() : viewHeight;
    mRefreshMapView.setLayoutParams(params);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mBusContext.getMap().clearElements();
  }
}
