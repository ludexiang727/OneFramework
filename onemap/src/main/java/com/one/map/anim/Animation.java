package com.one.map.anim;

/**
 * 动画类, 只支持Baidu的三种动画属性, 从地面生长、跳动、天上掉下属性
 */
public class Animation {

    public enum MarkerAnimation {
        /**
         * 没效果
         */
        none,
        /**
         * 从天上掉下
         */
        drop,

        /**
         * 从地面生长
         */
        grow,

        /**
         * 跳动
         */
        jump
    }

    public Animation(MarkerAnimation markerAnimation) {
        this.markerAnimation = markerAnimation;
    }

    public MarkerAnimation getMarkerAnimation() {
        return markerAnimation;
    }

    private MarkerAnimation markerAnimation;
}