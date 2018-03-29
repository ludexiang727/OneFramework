package com.evaluate.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class HeightCustomizableGridLayoutManager extends GridLayoutManager {

  private static final String TAG = "MaxHeightLM";

  private OnHeightMeasureListener mOnHeightMeasureListener;
  private RecyclerView mRecyclerView;

  public HeightCustomizableGridLayoutManager(RecyclerView recyclerView, Context context, int
      spanCount) {
    super(context, spanCount);
    mRecyclerView = recyclerView;
  }

//    @Override
//    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.LockScreenOrderStatus state, int widthSpec, int heightSpec) {
//        // calling LayoutManager here is not pretty but that API is already public and it is better
//        // than creating another method since this is internal.
//        int width = RecyclerView.LayoutManager.chooseSize(widthSpec,
//                getPaddingLeft() + getPaddingRight(),
//                ViewCompat.getMinimumWidth(mRecyclerView));
//        int height = RecyclerView.LayoutManager.chooseSize(heightSpec,
//                getPaddingTop() + getPaddingBottom(),
//                ViewCompat.getMinimumHeight(mRecyclerView));
//        if (state.getItemCount() == 0) {
//            height = 0;
//        }
//        setMeasuredDimension(width, height);
//    }

  @Override
  public void setMeasuredDimension(int widthSize, int heightSize) {
    if (mOnHeightMeasureListener != null) {
      heightSize = mOnHeightMeasureListener.onHeightMeasured(heightSize);
    }
    super.setMeasuredDimension(widthSize, heightSize);
  }

  public void setOnHeightMeasureListener(OnHeightMeasureListener listener) {
    mOnHeightMeasureListener = listener;
  }

  public interface OnHeightMeasureListener {

    /**
     * 返回本次measure的高度，并且得到调整后的高度
     *
     * @param height 本次测算的高度
     * @return 指定目标高度
     */
    int onHeightMeasured(int height);
  }

}
