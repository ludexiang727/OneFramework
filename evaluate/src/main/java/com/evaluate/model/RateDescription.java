package com.evaluate.model;

/**
 * 每一个星级对应的星星下方的文案
 *
 * @since 16/8/29
 */

public interface RateDescription {
    /**
     * 描述对应的星级
     *
     * @return
     */
    int getRate();

    /**
     * 描述对应的文案
     *
     * @return
     */
    String getText();

    int getTextRes();
}
