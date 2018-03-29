package com.evaluate.model;

import android.support.annotation.StringRes;
import java.util.List;

/**
 * 每一个星级对应的标签列表数据，其中包含标签列表上方的文案。
 *
 * @since 16/8/29
 */

public interface EvaluateRateTags {
    /**
     * 数据对应的星级
     *
     * @return
     */
    int getRate();

    /**
     * 该数据的引导文案
     *
     * @return
     */
    String getText();

    /**
     * 数据对应的标签
     *
     * @return
     */
    List<EvaluateTag> getTags();

    @StringRes
    int getTextRes();
}
