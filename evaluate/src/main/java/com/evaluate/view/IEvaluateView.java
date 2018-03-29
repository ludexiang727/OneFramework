package com.evaluate.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import com.evaluate.model.EvaluateRateTags;
import com.evaluate.model.EvaluateTag;
import com.evaluate.model.Question;
import com.evaluate.model.RateDescription;
import com.evaluate.widgets.QuestionView;
import java.util.List;

/**
 * 评价组件UI接口定义，该组件包含两种模式（{@link Mode#Rating} 和 {@link Mode#View}）<br>
 * 部分接口在两种模式中间存在互斥性，具体可参考具体方法的注释。
 */

public interface IEvaluateView {

    enum Mode {
        QuestionThenRating,
        Rating,
        View
    }

    void onAdd();

    void onRemove();

    /**
     * 展示popup helper
     *
     * @return
     */
    void showPopupHelper();

    /**
     * 隐藏popup helper
     *
     * @param callback 是否触发dismiss回调
     * @return
     */
    void hidePopupHelper(boolean callback);

    /**
     * 设置View的模式，暂时有两种模式：<br>
     * 1. {@link Mode#Rating} 编辑模式，即未评价状态。<br>
     * 2. {@link Mode#View} 查看模式，即已评价状态，查看模式下，没法对 View 进行操作，也不会触发{@link EvaluateListener}回调。<br>
     * 默认是Edit模式。
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * 设置标题
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * 设置标题
     *
     * @param title
     */
    void setTitle(@StringRes int title);

    /**
     * 设置评价等级,View模式生效
     *
     * @param level
     */
    void setRate(int level);

    /**
     * 设置星级对应的描述文案，一个星级对应一个 {@link RateDescription}，Edit模式。
     *
     * @param data
     */
    void setRateDescriptions(List<RateDescription> data);

    /**
     * 设置星级评论文案是否可见，默认可见
     *
     * @param visible
     */
    void setRateDescriptionVisibility(boolean visible);

    /**
     * View模式下星级下方的文案，有默认文案。
     *
     * @param text
     */
    void setRateDescription(String text);

    /**
     * 获取星级对应的描述文案。
     *
     * @return
     */
    String getRateDescription();

    /**
     * Edit模式下星级下方的hint文案，有默认文案。
     *
     * @param text
     */
    void setRateDescriptionHint(String text);

    /**
     * Edit模式下星级下方的hint文案，有默认文案。
     *
     * @param text
     */
    void setRateDescriptionHint(@StringRes int text);

    /**
     * 设置星级对应的标签数据，一个星级对应一个 {@link EvaluateRateTags}，Edit模式。
     *
     * @param data
     */
    void setRateTags(List<EvaluateRateTags> data);

    /**
     * 设置已评价的标签数据，View模式。
     *
     * @param tags
     */
    void setTags(List<EvaluateTag> tags);

    /**
     * 设置标签区域是否可见，默认不可见
     *
     * @param visible
     */
    void setTagAreaVisibility(boolean visible);

    /**
     * 设置评论区域是否可见，默认不可见
     *
     * @param visible
     */
    void setCommentAreaVisibility(boolean visible);

    /**
     * 设置评论区内容，View模式
     *
     * @param content
     */
    void setCommentContent(String content);

    /**
     * 设置评价控件的回调
     *
     * @param listener
     */
    void setEvaluateListener(EvaluateListener listener);

    /**
     * 展示提交进度条
     */
    void showSubmitProgress();

    /**
     * 展示提交成功
     */
    void showSubmitSuccess();

    void showSubmited();

    /**
     * 展示提交失败,点击重试会重新触发 {@link EvaluateListener#onSubmit(int, List, String)}
     */
    void showSubmitFail();

    /**
     * 展示提交失败,点击重试会重新触发 {@link EvaluateListener#onSubmit(int, List, String)}
     */
    void showSubmitFail(CharSequence err);

    /**
     * 展示加载进度
     */
    void showLoadingProgress();

    /**
     * 隐藏loading界面，展示正常界面。
     */
    void hideLoadingView();

    /**
     * 隐藏失败界面，展示正常界面。
     */
    void hideFailView();

    /**
     * 展示加载失败,点击重试会重新触发
     */
    void showLoadingFail(boolean evaluated);

    /**
     * 添加关闭评价浮层的回调。<br>
     * 只会在特定的场景下关闭评价浮层：1.点击左上角的关闭icon 2.提交失败时选择关闭。
     *
     * @param listener
     */
    void setOnCloseListener(OnCloseListener listener);

    /**
     * 添加请求失败时的取消回调
     *
     * @param listener
     */
    void setOnCancelListener(OnCancelListener listener);

    /**
     * 主动关闭评价浮层。一些特定场景需要调用此方法，如用户点击返回键时。
     */
    void close();

    /**
     * 设置一个星级列表，在此列表中的星级不需要评论或者选择标签
     *
     * @param levels
     */
    void setNoTagOrCommentList(int[] levels);

    /**
     * 设置问题列表
     *
     * @param questionList
     */
    void setQuestions(List<Question> questionList);

    /**
     * 设置问题组件回调监听
     *
     * @param onQuestionViewActionListener
     */
    void setOnQuestionViewActionListener(QuestionView.OnQuestionViewActionListener
        onQuestionViewActionListener);

    /**
     * 告知Viewo问题提交成功，展现成功ui
     */
    void notifyQuestionSubmitSuccess();

    /**
     * 告知View问题提交成功，展现成功ui
     *
     * @param message 展示成功文案
     */
    void notifyQuestionSubmitSuccess(String message);


    /**
     * 告知View问题提交失败，展现失败ui
     */
    void notifyQuestionSubmitFail();

    /**
     * 获取提交按钮的View
     */
    View getSubmitButton();

    /**
     * 通知数据已经填充到 UI
     */
    void notifyViewSetup();

    void onResume();

    void onPause();

    /**
     * 是否添加附加View(只支持五星评价)
     *
     * @param hasExtend
     */
    void hasExtendView(boolean hasExtend);

    /**
     * 添加附加View
     *
     * @param extendView
     */
    void addExtendView(View extendView);


    View getView();
    /**
     * 评价操作回调，包括星级回调、标签选择回调以及确认提交回调
     */
    interface EvaluateListener {
        /**
         * 星级选择变化时回调
         *
         * @param rate
         */
        void onRateChange(int rate);

        /**
         * 当评价标签选中状态变化时回调
         *
         * @param rate
         * @param tag
         * @param select
         */
        void onEvaluateTagSelectChange(int rate, @NonNull EvaluateTag tag, boolean select);

        /**
         * 当提交按钮被点击时回调
         *
         * @param rate    1-5
         * @param tags    默认为null
         * @param comment 默认为""
         */
        void onSubmit(int rate, @Nullable List<EvaluateTag> tags, @NonNull String comment);

        /**
         * 加载业务线需要的数据。
         */
        void onLoadData();

        /**
         * 触发提交限制规则，导致无法提交，如：评星为非5，但是没有选择标签。
         */
        void onSubmitDisable();

        /**
         * 在完成问题回答之后，调起评价
         */
        void onSwitchToEvaluate();

        /**
         * 是否有评价数据、已评数据
         *
         * @return
         */
        boolean hasEvaluateData();
    }

    interface OnCloseListener {
        void onClose();
    }

    interface OnCancelListener {
        void onCancel();
    }
}
