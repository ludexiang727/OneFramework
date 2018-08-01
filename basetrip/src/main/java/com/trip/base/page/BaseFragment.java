package com.trip.base.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
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
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.one.framework.app.base.BizEntranceFragment;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;
import com.one.map.IMap.IMarkerClickCallback;
import com.one.map.location.LocationProvider;
import com.one.map.map.element.IMarker;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.trip.base.R;
import com.trip.base.provider.FormDataProvider;
import com.trip.base.widget.BaseLinearLayout;
import com.trip.base.widget.BottomViewLayout;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BaseFragment extends BizEntranceFragment implements IMarkerClickCallback {

  /**
   * 之所以加入Base Parent 为了计算高度已实现地图最佳view
   */
  private BaseLinearLayout mTopContainer;
  private BottomViewLayout mBottomContainer;
  private boolean isAttached = true;
  private static final int REFRESH_MAP = 0X101;

  /**
   * 缓存top bottom container width and height
   */
  protected int[] mTopRect = new int[2];
  protected int[] mBottomRect = new int[2];

  protected static boolean isFirstLayoutDone = false;

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

  protected abstract void mapClearElement();

  @Nullable
  @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View root = inflater.inflate(R.layout.base_fragment_layout, null);
    mTopContainer = root.findViewById(R.id.base_top_container);
    mBottomContainer = root.findViewById(R.id.base_bottom_container);
    mRefreshMapView = root.findViewById(R.id.base_refresh_map);

    final View view = onCreateViewImpl(inflater, mBottomContainer, savedInstanceState);
    if (mBusContext == null) {
      parseBundle();
    }
    if (mBottomContainer.getChildCount() <= 0) {
      isAttached = false;
      view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          view.measure(width, height);
          RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, view.getMeasuredHeight());
          params.bottomMargin = UIUtils.dip2pxInt(getContext(), 10);
          params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
          mBottomContainer.setLayoutParams(params);
          reCalculateHeight();
          view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
      });
      mBottomContainer.addView(view, 0);
    }

//    mBusContext.getMap().displayMyLocation();
    mBusContext.getMap().setIMarkerClickCallback(this);
    mRefreshMapView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Address locationAddress = LocationProvider.getInstance().getLocation();
        FormDataProvider.getInstance().saveStartAddress(locationAddress);
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
              toggleMapView();
              isFirstLayoutDone = true;
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
    mMap.doBestView(bestView);
  }

  /**
   * attach to top container and invoke onSizeChange and callback reCalculate
   */
  protected final void attachToTopContainer(final View view) {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    params.topMargin = UIUtils.dip2pxInt(getContext(), 6);
    mTopContainer.setVisibility(View.VISIBLE);
    mTopContainer.addView(view, mTopContainer.getChildCount(), params);
    topContainerViewAnim(view, false);
  }

  protected final void detachFromTopContainer(final View view) {
    if (mTopContainer.getVisibility() == View.GONE || mTopContainer.getChildCount() <= 0 || view == null) {
      return;
    }
    if (mTopContainer.getChildCount() > 0) {
      for (int i = 0; i < mTopContainer.getChildCount(); i++) {
        View childView = mTopContainer.getChildAt(i);
        if (childView == view) {
          topContainerViewAnim(childView, true);
        } else {
          continue;
        }
      }
    }
  }

  protected final void sharkTopView() {
    ObjectAnimator transX = ObjectAnimator.ofFloat(mTopContainer, "translationX", -10f, 10f);
//    transX.addUpdateListener(new AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float translationX = (Float) animation.getAnimatedValue();
//        mBottomContainer.setTranslationX(translationX);
//      }
//    });
    transX.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        mTopContainer.setTranslationX(0);
//        mBottomContainer.setTranslationX(0);
      }
    });
    transX.setInterpolator(new CycleInterpolator(2));
    transX.setDuration(300);
    transX.start();
  }

  private void topContainerViewAnim(final View view, final boolean isRemove) {
    AnimatorSet set = new AnimatorSet();
    float alphaFrom = isRemove ? 1f : 0f;
    float alphaTo = isRemove ? 0f : 1f;
    float transFrom = isRemove ? 0f : -UIUtils.getViewHeight(view);
    float transTo = isRemove ? -UIUtils.getViewHeight(view) : 0f;
    ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", alphaFrom, alphaTo);
    ObjectAnimator transY = ObjectAnimator.ofFloat(view, "translationY", transFrom, transTo);
    set.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (isRemove) {
          mTopContainer.removeView(view);

          if (mTopContainer.getChildCount() == 0) {
            mTopContainer.setVisibility(View.GONE);
          }
        }
        reCalculateHeight();
      }
    });
    set.setDuration(500);
    set.playTogether(transY, alpha);
    set.start();
  }

  /**
   * 重新测量子view高度
   */
  protected void reCalculateHeight() {
    if (mTopContainer.getChildCount() > 0) {
      int viewWidth = UIUtils.getViewWidth(mTopContainer);
      int viewHeight = UIUtils.getViewHeight(mTopContainer);
      mTopRect[0] = viewWidth;
      mTopRect[1] = mBusContext.getTopbar().getTopbarHeight() + viewHeight;
    } else {
      mTopRect[0] = UIUtils.getScreenWidth(getActivity());
      mTopRect[1] = mBusContext.getTopbar().getTopbarHeight();
    }

    if (isAttached) {
      int viewWidth = UIUtils.getViewWidth(mBottomContainer);
      int viewHeight = UIUtils.getViewHeight(mBottomContainer);
      mBottomRect[0] = viewWidth; // mBottomContainer 宽度 由于是match_parent 故获得的宽度为屏幕宽度
      mBottomRect[1] = viewHeight; // 包含View margin
    } else {
      if (mBottomContainer.getChildCount() > 0) {
        View view = mBottomContainer.getChildAt(0);
        int viewWidth = UIUtils.getViewWidth(view);
        int viewHeight = UIUtils.getViewHeight(view);
        mBottomRect[0] = viewWidth;
        mBottomRect[1] = viewHeight;
      }
    }
    Logger.e("ldx", "top " + mTopRect[1] + " bottom  " + mBottomRect[1]);
    updatePinViewPosition(mTopRect[1], mBottomRect[1]);
    toggleMapView();
  }

  protected void reLayoutLocationPosition(int viewHeight) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRefreshMapView.getLayoutParams();
    params.bottomMargin = viewHeight == 0 ? -mRefreshMapView.getHeight() : viewHeight;
    mRefreshMapView.setLayoutParams(params);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mapClearElement();
  }
}
