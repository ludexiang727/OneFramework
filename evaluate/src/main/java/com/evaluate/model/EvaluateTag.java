package com.evaluate.model;

public interface EvaluateTag {
    /**
     * 评价页展示的星级标签文字
     */
    String getText();

    /**
     * 如果需要,可作为埋点id
     */
    long getId();

    /**
     * tag 是否选择
     */
    void setSelected(boolean selected);

    /**
     * selected
     */
    boolean isSelected();
}
