package com.evaluate.widgets;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.evaluate.R;
import com.evaluate.model.Question;
import java.util.ArrayList;
import java.util.List;

public class QuestionView extends RelativeLayout {
    private OnQuestionViewActionListener mOnQuestionViewActionListener;

    private QuestionManager mQuestionManager;

    private View mQuestionContainer;
    private TextView mQuestionContentView;
    private LinearLayout mQuestionOptionsContainer;

    private View mGoRate;
    private LoadingStateView mLoadingView;
    private View mFailView;

    public QuestionView(Context context) {
        super(context);
        init(context);
    }

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.oc_evaluate_question_layout, this, true);
        mQuestionContainer = getView(R.id.evaluate_question_view);
        mQuestionContentView = getView(R.id.evaluate_question_content);
        mQuestionOptionsContainer = getView(R.id.evaluate_question_options_container);

        mGoRate = getView(R.id.evaluate_question_go_rating);
        mGoRate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnQuestionViewActionListener != null) {
                    mOnQuestionViewActionListener.onToRatingClick(0);
                }
            }
        });

        mLoadingView = getView(R.id.evaluate_question_submit_loading);
        mFailView = getView(R.id.evaluate_question_submit_fail);
        getView(R.id.evaluate_question_submit_fail_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailView.setVisibility(View.GONE);
                if (mOnQuestionViewActionListener != null) {
                    mOnQuestionViewActionListener.onQuestionDone();
                }
            }
        });
    }

    private <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
    }

    public void setQuestionList(List<Question> questionList) {
        if (questionList == null || questionList.size() == 0) {
            return;
        }
        mQuestionManager = new QuestionManager(questionList);
    }

    public void showQuestionView() {
        if (mQuestionManager == null) {
            return;
        }
        final Question question = mQuestionManager.pop();
        if (question == null) {
            return;
        }
        final int index = mQuestionManager.getIndex();
        if (!TextUtils.isEmpty(question.question)) {
//            mQuestionContentView.setText(HighlightUtil.highlight(question.question.toString()));
        }

        mQuestionOptionsContainer.removeAllViews();

        int optionCount = question.options != null ? question.options.length : 0;
        int[] icons = question.icons;
        for (int i = 0; i < optionCount && i < 3; i++) {
            CharSequence option = question.options[i];
            int iconStatus = -1;
            if (null != icons && i < icons.length) {
                // icon取值范围: 1差评  2中评   3好评
                iconStatus = icons[i];
            }
            createOptionView(question, index, option, iconStatus);
        }

        if (mOnQuestionViewActionListener != null) {
            mOnQuestionViewActionListener.onQuestionShow(index, question);
        }
    }

    private void showTips() {
        if (mOnQuestionViewActionListener != null && mQuestionOptionsContainer != null
                && VISIBLE == mQuestionOptionsContainer.getVisibility()) {
            mOnQuestionViewActionListener.onShowTips(mQuestionOptionsContainer);
        }
    }


    private void createOptionView(final Question question, final int index, final CharSequence option, int iconStatus) {
        View optionView;
        if (!TextUtils.isEmpty(question.reply) && option.equals(question.reply)) {
            optionView = LayoutInflater.from(getContext()).inflate(R.layout
                    .oc_question_option_selected_view, mQuestionOptionsContainer, false);

        } else {
            optionView = LayoutInflater.from(getContext()).inflate(R.layout
                    .oc_question_option_view, mQuestionOptionsContainer, false);
        }
        updateIconView(optionView, iconStatus);

        TextView optionText = (TextView) optionView.findViewById(R.id.evaluate_question_text);
        optionText.setText(option);

        if (TextUtils.isEmpty(question.reply)) {
            optionView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnQuestionViewActionListener != null) {
                        mOnQuestionViewActionListener.onSelect(index, question, option);
                    }
                    if (mQuestionManager.hasMoreQuestion()) {
                        showQuestionView();
                    } else {
                        if (mOnQuestionViewActionListener != null) {
                            mOnQuestionViewActionListener.onQuestionDone();
                        }
                    }
                }
            });
        }
        mQuestionOptionsContainer.addView(optionView);
    }

    private void updateIconView(View container, int status) {
        ImageView iconView = (ImageView) container.findViewById(R.id.evaluate_question_icon);
        if (status >= 1 && status <= 3) {
            iconView.setVisibility(VISIBLE);
            iconView.getDrawable().setLevel(status);
        } else {
            iconView.setVisibility(GONE);
        }
    }

    public void setOnQuestionViewActionListener(OnQuestionViewActionListener onQeustionViewActionListener) {
        mOnQuestionViewActionListener = onQeustionViewActionListener;
    }

    public void showEvaluateEntrance(boolean show) {
        mGoRate.setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.evaluate_question_go_rating_icon).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.to_evaluate_container).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showSubmitSuccessView() {
        showSubmitSuccessView(getContext().getString(R.string.oc_question_thanks));
    }

    public void showSubmitSuccessView(String message) {
        if (TextUtils.isEmpty(message)) {
            message = getContext().getString(R.string.oc_question_thanks);
        }
        mLoadingView.changeState(LoadingStateView.State.SUCCESS_STATE);
        mLoadingView.setText(message);
    }

    public void showSubmitFailView() {
        mLoadingView.setVisibility(View.GONE);
        mFailView.setVisibility(View.VISIBLE);
    }

    public void showSubmittingView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.setText(R.string.oc_question_submitting);
        mQuestionContainer.setVisibility(View.INVISIBLE);
    }

    public void showPopupHelper() {
        showTips();
    }

    private class QuestionManager {
        private List<Question> mQuestionList;
        private int index = -1;

        QuestionManager(List<Question> questionList) {
            mQuestionList = new ArrayList<>();
            mQuestionList.addAll(questionList);
            index = -1;
        }

        public Question pop() {
            if (mQuestionList.size() > 0) {
                index++;
                return mQuestionList.remove(0);
            } else {
                return null;
            }
        }

        public int getIndex() {
            return index;
        }

        public boolean hasMoreQuestion() {
            return mQuestionList.size() > 0;
        }
    }

    /**
     * 与提问组件的交互回调
     */
    public interface OnQuestionViewActionListener {
        /**
         * 点击"去评级"
         */
        void onToRatingClick(int level);

        /**
         * 当一个问题被展示时
         *
         * @param index    问题的index
         * @param question 被展示的问题
         */
        void onQuestionShow(int index, Question question);

        /**
         * 当选择了某一个问题的回答时
         *
         * @param index     问题的 index
         * @param question  问题
         * @param selection 被选择的选项
         */
        void onSelect(int index, Question question, CharSequence selection);

        /**
         * 当问题全部回答完毕
         */
        void onQuestionDone();

        void onShowTips(View view);
    }
}
