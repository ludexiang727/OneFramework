package com.trip.taxi.divider;

import android.support.annotation.Keep;
import android.view.MotionEvent;

@Keep
public interface IMovePublisher {

    /**
     * 在视图的dispatchTouchEvent调用
     *
     * @param event
     * @return
     */
    boolean dispatchTouchEvent(MotionEvent event);

    /**
     * 在视图的onTouchEvent调用
     *
     * @return
     */
    boolean onTouchEvent(MotionEvent event);

    /**
     * 在视图的onInterceptTouchEvent调用
     *
     * @param event
     * @return
     */
    boolean onInterceptTouchEvent(MotionEvent event);

    /**
     * 设置触摸事件的监听
     *
     * @param l
     */
    void setOnMoveListener(OnMoveListener l);

    @Keep
    interface OnMoveListener {
        /**
         * 触摸事件与起始点之间的距离信息
         *
         * @param moveX 在X轴上的移动信息,向左为负向右为正
         * @param moveY 在Y轴上的移动信息,向上为负向下为正
         */
        void onMove(float moveX, float moveY);

        /**
         * 触摸事件结束时的回调
         *
         * @param fling      是不是一个滑动事件
         * @param bottomToUp 是不是一个从下向上的滑动事件
         */
        void onEnd(boolean fling, boolean bottomToUp);
    }
}
