package com.evaluate.widgets;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import com.evaluate.R;
import com.evaluate.adapter.AbsRecyclerAdapter;
import com.evaluate.adapter.AbsViewBinder;
import com.evaluate.adapter.HeightCustomizableGridLayoutManager;
import com.evaluate.model.EvaluateTag;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class EvaluateTagListView extends RecyclerView implements
    HeightCustomizableGridLayoutManager.OnHeightMeasureListener {

  private static final String TAG = "EvaluateTag";

  private static final int TYPE_NORMAL = 1;
  private static final int TYPE_PLACEHOLDER = 2;

  private OnTagSelectChangeListener mOnTagSelectChangeListener;

  private EvaluateTagListAdapter mAdapter;
  private boolean mSelectable;

  private int mMaxHeight;

  private RecyclerViewHeightAnimator mAnimator;

  private boolean mNoAnim;
  private boolean mDataSet;
  private boolean mAnimateOnFirstSetup;

  public EvaluateTagListView(Context context) {
    this(context, null);
  }

  public EvaluateTagListView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EvaluateTagListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public void updateEvaluateTags(List<EvaluateTag> list) {
    if (!mAnimateOnFirstSetup) {
      if (!mDataSet) {
        mDataSet = true;
        mNoAnim = true;
      } else {
        mNoAnim = false;
      }
    }
    mAdapter.update(wrapForSelect(list));
  }

  private List<EvaluateTagWrapper> wrapForSelect(List<EvaluateTag> list) {
    List<EvaluateTagWrapper> wrappers = new ArrayList<>();
    // place holder for empty list, workaround for a issue of RecyclerView
    for (EvaluateTag tag : list) {
      wrappers.add(new EvaluateTagWrapper(tag));
    }
    return wrappers;
  }

  private void init() {
    mMaxHeight = getResources().getDimensionPixelSize(R.dimen.evaluate_tag_list_max_height);

    HeightCustomizableGridLayoutManager layoutManager = new
        HeightCustomizableGridLayoutManager(this, getContext(), 3);
    layoutManager.setOnHeightMeasureListener(this);
    setLayoutManager(layoutManager);

    mAdapter = new EvaluateTagListAdapter(getContext());
    setAdapter(mAdapter);
    setOverScrollMode(View.OVER_SCROLL_NEVER);
  }

  public List<EvaluateTag> getSelectedTags() {
    List<EvaluateTagWrapper> list = mAdapter.getData();

    List<EvaluateTag> selectedList = new ArrayList<>();
    for (EvaluateTagWrapper tag : list) {
      if (tag.isSelected()) {
        selectedList.add(tag.mTag);
      }
    }
    return selectedList;
  }

  public void setTagSelectable(boolean b) {
    mSelectable = b;
  }

  @Override
  public int onHeightMeasured(int height) {
    if (mNoAnim) {
      return Math.min(height, mMaxHeight);
    }

    int currentHeight = this.getHeight();
    int target = Math.min(height, mMaxHeight);
    if (mAnimator == null || !mAnimator.isRunning()) {
      if (currentHeight == target) {
        return target;
      }

      mAnimator = new RecyclerViewHeightAnimator(this, currentHeight, target);
      mAnimator.start();
      return mAnimator.getCurrentHeight();
    } else {
      if (target == mAnimator.getTargetHeight()) {
        int animatedHeight = mAnimator.getCurrentHeight();
        return animatedHeight;
      } else {
        mAnimator.end();
        mAnimator = new RecyclerViewHeightAnimator(this, currentHeight, target);
        mAnimator.start();
        return mAnimator.getCurrentHeight();
      }
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mAnimator != null && mAnimator.isRunning()) {
      mAnimator.end();
    }
  }

  private class EvaluateTagListAdapter extends
      AbsRecyclerAdapter<EvaluateTagItemHolder, EvaluateTagWrapper> {

    public EvaluateTagListAdapter(Context context) {
      super(context);
    }

    @Override
    protected EvaluateTagItemHolder createViewHolder(View view) {
      return new EvaluateTagItemHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
//      if (viewType == TYPE_PLACEHOLDER) {
//        return inflater.inflate(R
//            .layout.oc_evaluate_item_placeholder, parent, false);
//      }
//      if (mSelectable) {
      return inflater.inflate(R.layout.oc_evaluate_item_view, parent, false);
//      } else {
//        return inflater.inflate(R.layout.oc_evaluate_item_non_select_view, parent, false);
//      }
    }

    @Override
    public int getItemViewType(int position) {
//      if (position <= 1) {
//        return TYPE_PLACEHOLDER;
//      }
      return TYPE_NORMAL;
    }
  }

  private class EvaluateTagItemHolder extends AbsViewBinder<EvaluateTagWrapper> {

    private TextViewAccessibleForCheck mTextView;

    public EvaluateTagItemHolder(View view) {
      super(view);
    }

    @Override
    protected void getViews() {
      mTextView = getView(R.id.evaluate_tag_text);
    }

    @Override
    public void bind(EvaluateTagWrapper evaluateTag) {
      if (evaluateTag != null && evaluateTag.mTag != null && mTextView != null) {
        mTextView.setText(evaluateTag.getText());
        mTextView.setSelected(evaluateTag.isSelected());
      }
    }

    @Override
    protected void onViewClick(View view, EvaluateTagWrapper data) {
      if (!mSelectable) {
        return;
      }
      boolean selected = !data.isSelected();
      data.setSelected(selected);
      mTextView.setSelected(selected);
      if (mOnTagSelectChangeListener != null) {
        mOnTagSelectChangeListener.onTagSelectChange(data.mTag, selected);
      }
    }
  }

  private class EvaluateTagWrapper {

    private EvaluateTag mTag;

    public EvaluateTagWrapper(EvaluateTag tag) {
      mTag = tag;
    }

    public String getText() {
      return mTag.getText();
    }

    public long getId() {
      return mTag.getId();
    }

    public void setSelected(boolean selected) {
      mTag.setSelected(selected);
    }

    public boolean isSelected() {
      return mTag.isSelected();
    }

    @Override
    public String toString() {
      return "EvaluateTagWrapper{" +
          "tag =" + mTag.getText() +
          "tag.selected =" + mTag.isSelected() +
          '}';
    }
  }

  public void clear() {
    List<EvaluateTagWrapper> list = new ArrayList<>();
    // place holder for empty list, workaround for a issue of RecyclerView
    list.add(new EvaluateTagWrapper(null));
    list.add(new EvaluateTagWrapper(null));
    list.add(new EvaluateTagWrapper(null));
    mAdapter.update(list);
  }

  public void setOnTagSelectChangeListener(OnTagSelectChangeListener listener) {
    mOnTagSelectChangeListener = listener;
  }

  public interface OnTagSelectChangeListener {

    void onTagSelectChange(EvaluateTag tag, boolean selected);
  }

  static class RecyclerViewHeightAnimator extends ValueAnimator implements
      ValueAnimator.AnimatorUpdateListener {

    private float height;
    private float targetHeight;

    private int DURATION = 200;

    private RecyclerView view;

    public RecyclerViewHeightAnimator(RecyclerView view, float height, float targetHeight) {
      this.height = height;
      this.targetHeight = targetHeight;
      this.view = view;
    }

    @Override
    public void start() {
      setFloatValues(height, targetHeight);
      setDuration(DURATION);
      setInterpolator(new LinearInterpolator());
      this.addUpdateListener(this);
      super.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      view.requestLayout();
    }

    public int getTargetHeight() {
      return Math.round(targetHeight);
    }

    public int getCurrentHeight() {
      return Math.round((Float) getAnimatedValue());
    }
  }

  /**
   * 是否在第一次加载数据时做动画，默认做。后续替换数据时都会做动画。需要在设置标签之前调用
   */
  public void animateOnFirstSetup(boolean enable) {
    mAnimateOnFirstSetup = enable;
  }
}
