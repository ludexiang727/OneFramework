package com.evaluate.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.evaluate.R;
import com.evaluate.UIThreadHandler;
import com.evaluate.model.EvaluateRateTags;
import com.evaluate.model.EvaluateTag;
import com.evaluate.model.Question;
import com.evaluate.model.RateDescription;
import com.evaluate.util.KeyboardHeightProvider;
import com.evaluate.widgets.CardTitleView;
import com.evaluate.widgets.CommentView;
import com.evaluate.widgets.EvaluateRootContainer;
import com.evaluate.widgets.EvaluateTagListView;
import com.evaluate.widgets.LoadingStateView;
import com.evaluate.widgets.QuestionView;
import com.evaluate.widgets.StarView;
import com.evaluate.widgets.TipsView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IEvaluateView} 的实现类
 */
public class EvaluateView implements IEvaluateView, StarView.OnTouchStarChangeListener,
    EvaluateTagListView.OnTagSelectChangeListener,
    CardTitleView.CardTitleCloseBtnListener, CommentView.OnContentChangeListener,
    EvaluateRootContainer.OnTouchWhenInterceptListener, QuestionView.OnQuestionViewActionListener {

  private static final String TAG = "EvaluateView";

  private static final String KEY_HELP_SWITCH = "key_evaluate_help_switch";

  //  private TipsContainer mTipsContainer;
  private TipsView mTipsView;

  private static final int STAR_PERFECT = 5;
  private OnCancelListener mOnCancelListener;
  private boolean mNotDismissHelper;

  private QuestionView mQuestionView;
  private QuestionView.OnQuestionViewActionListener mOnQuestionViewActionListener;

  private SubmitHelper mSubmitHelper;

  private boolean mHasExtendView;
  private RelativeLayout mExtendViewLayout;

  private Runnable buttonDismiss = new Runnable() {
    @Override
    public void run() {
      mSubmitHelper.hide();

      showSubmitSuccessUI();
    }
  };

  private KeyboardHeightProvider.KeyboardHeightObserver mHeightObserver = new KeyboardHeightProvider.KeyboardHeightObserver() {
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
      if (mCommentView != null) {
        Activity activity = mReference.get();
        boolean show = height > 0;

        if (activity != null) {
          Window window = activity.getWindow();
          WindowManager.LayoutParams params = window.getAttributes();
        }
        // 拦截touch事件
        mRootContainer.setInterceptTouchEnable(show);
        mCommentView.onKeyboardHeightChange(height);
        if (!show) {
          clearCommentViewFocus();
        } else {
          hidePopupHelper(true);
        }
      }
    }
  };


  public boolean isQuestionShow() {
    return mQuestionView != null && mQuestionView.isShown();
  }

  enum State {
    Before_Commenting,
    Commenting
  }

  private Mode mMode = null;

  private int[] mNoNeedCommentList;
  /**
   * Edit 模式下需要区分评价前和评价中
   * <br>
   * View 模式无状态区分
   */
  private State mState = State.Before_Commenting;

  private View mView;

  private StarView mStarView;
  private TextView mStarDescription;

  private ViewStub mTagAreaStub;
  private View mTagArea;

  private EvaluateTagListView mTagListView;
  private EvaluateListener mEvaluateListener;

  private OnCloseListener mOnCloseListener;

  private ViewStub mCommentViewStub;
  private CommentView mCommentView;

  /**
   * 根节点
   */
  private EvaluateRootContainer mRootContainer;

  private Map<Integer, RateDescription> mRateDescriptions;
  private Map<Integer, EvaluateRateTags> mRateTags;
  private List<EvaluateTag> mTagList;
  private String mCommentContent;

  private boolean mCommentVisible;
  private boolean mTagListVisible;


  private KeyboardHeightProvider mKeyboardHeightDetector;
  private WeakReference<Activity> mReference;

  public EvaluateView(Activity activity) {
    mReference = new WeakReference<Activity>(activity);
    if (mReference.get() != null) {
      Context context = mReference.get();
      LayoutInflater inflater = LayoutInflater.from(context);
      mView = inflater.inflate(R.layout.oc_evaluate_layout, null);

      init();
      startKeyboardHeightDetector();
    }
  }

  private void startKeyboardHeightDetector() {
    if (mReference.get() != null) {
      mKeyboardHeightDetector = new KeyboardHeightProvider(mReference.get());
      mKeyboardHeightDetector.start();
    }
  }

  private void init() {
    mRootContainer = findView(R.id.evaluate_root_container);
    mRootContainer.setOnTouchWhenInterceptListener(this);

    //星星组件
    mStarView = findView(R.id.evaluate_star_view);
    mStarView.setOnTouchStarChangeListener(this);
    mStarDescription = findView(R.id.evaluate_star_des);

    //标签区域
    mTagAreaStub = findView(R.id.evaluate_tag_area_stub);

    //评价输入框
    mCommentViewStub = findView(R.id.evaluate_comment_area_stub);

    // 评价数据存储
    mRateDescriptions = new HashMap<>();
    mRateTags = new HashMap<>();

    getSubmitButton();
  }

  @Override
  public void onAdd() {

  }

  @Override
  public void onRemove() {
    UIThreadHandler.removeCallback(buttonDismiss);
    if (mKeyboardHeightDetector != null) {
      mKeyboardHeightDetector.close();
    }
  }

  @Override
  public void showPopupHelper() {
    if (mMode == Mode.Rating) {
      onShowTips(mStarView);
    }
  }

  @Override
  public void hidePopupHelper(boolean callback) {
    dismissTipsView();
  }

  @Override
  public void setMode(Mode mode) {
    if (mode == Mode.QuestionThenRating) {
      findView(R.id.evaluate_container).setVisibility(View.GONE);
      if (mQuestionView == null) {
        ViewStub stub = findView(R.id.evaluate_question_view_stub);
        mQuestionView = (QuestionView) stub.inflate();
      }
      mMode = mode;
      return;
    }
    if (mode == Mode.View) {
      mStarView.setTouchEnable(false);
    } else if (mode == Mode.Rating) {
      mStarView.setTouchEnable(true);
    }
    if (mMode == null) {
      findView(R.id.evaluate_container).setVisibility(View.VISIBLE);
    }
    mMode = mode;
  }

  @Override
  public void setTitle(String title) {
  }

  @Override
  public void setTitle(@StringRes int title) {
  }

  @Override
  public void setRate(int level) {
    mStarView.setLevel(level);
    if (level > 0) {
      if (mMode == Mode.Rating) {
        mNotDismissHelper = true;
        onStarChange(false, level);
      }
    }
  }

  @Override
  public void setRateDescriptions(List<RateDescription> data) {
    if (data == null || data.isEmpty()) {
      return;
    }
    for (RateDescription description : data) {
      mRateDescriptions.put(description.getRate(), description);
    }
  }

  @Override
  public void setRateDescriptionVisibility(boolean visible) {
    mStarDescription.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setRateDescription(String text) {
    mStarDescription.setText(text);
  }

  @Override
  public String getRateDescription() {
    return mStarDescription.getText().toString().trim();
  }

  @Override
  public void setRateDescriptionHint(String text) {
    mStarDescription.setHint(text);
  }

  @Override
  public void setRateDescriptionHint(@StringRes int text) {
    mStarDescription.setHint(text);
  }

  @Override
  public void setTags(List<EvaluateTag> tags) {
    if (mMode != Mode.View) {
      return;
    }
    mTagList = new ArrayList<>();
    mTagList.addAll(tags);
    if (mTagListView == null) {
      setTagAreaVisibility(true);
    } else if (mTagListView != null) {
      evaluateFinish(tags);
    }
  }

  @Override
  public void setRateTags(List<EvaluateRateTags> data) {
    if (data == null || data.size() == 0) {
      mStarView.setVisibility(View.GONE);
      if (null != mExtendViewLayout) {
        mExtendViewLayout.findViewById(R.id.evaluate_extend_dividing_line)
            .setVisibility(View.GONE);
      }
      return;
    }
    mStarView.setVisibility(View.VISIBLE);
    if (null != mExtendViewLayout) {
      mExtendViewLayout.findViewById(R.id.evaluate_extend_dividing_line)
          .setVisibility(View.VISIBLE);
    }
    for (EvaluateRateTags tags : data) {
      mRateTags.put(tags.getRate(), tags);
    }
  }

  @Override
  public void setTagAreaVisibility(boolean visible) {
    mTagListVisible = visible;
    if (visible) {
      if ((mMode == Mode.Rating && mState == State.Commenting) || mMode == Mode
          .View || mMode == Mode.QuestionThenRating) {
        if (mTagArea == null) {
          mTagArea = mTagAreaStub.inflate();
          mTagListView = (EvaluateTagListView) mTagArea.findViewById(R.id.evaluate_tags_view);
          mTagListView.setOnTagSelectChangeListener(this);
        } else {
          mTagArea.setVisibility(View.VISIBLE);
        }

        if (mMode == Mode.View && mTagList != null) {
          evaluateFinish(mTagList);
        }
      }
    } else {
      if (mTagArea != null) {
        mTagArea.setVisibility(View.GONE);
      }
    }
  }

  private void evaluateFinish(List<EvaluateTag> tags) {
    if (tags != null && !tags.isEmpty()) {
      for (EvaluateTag tag : tags) {
        tag.setSelected(true);
      }
      mTagListView.updateEvaluateTags(tags);
      mTagListView.setTagSelectable(false);
    } else {
      setTagAreaVisibility(false);
    }
  }

  @Override
  public void setCommentAreaVisibility(boolean visible) {
    mCommentVisible = visible;
    if (visible) {
      if ((mMode == Mode.Rating && mState == State.Commenting) || mMode == Mode
          .View || mMode == Mode.QuestionThenRating) {
        if (mCommentView == null) {
          mCommentViewStub.inflate();
          mCommentView = findView(R.id.evaluate_comment_area);
          mCommentView.setOnContentChangeListener(this);
          mRootContainer.setCanTouchView(mCommentView.getId());
        } else {
          mCommentView.setVisibility(View.VISIBLE);
        }

        if (mMode == Mode.View) {
          mCommentView.setContent(mCommentContent);
        }
      }
    } else {
      if (mCommentView != null) {
        mCommentView.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void setCommentContent(String content) {
    mCommentContent = content;
    if (mCommentView == null) {
      setCommentAreaVisibility(true);
    } else if (mCommentView != null) {
      mCommentView.setContent(content);
    }
  }

  @Override
  public void setEvaluateListener(EvaluateListener listener) {
    mEvaluateListener = listener;
  }

  @Override
  public void showLoadingProgress() {
  }

  private void showLoadingView(@StringRes int string) {
    if (mReference.get() != null) {
      LoadingStateView loadingStateView = new LoadingStateView(mReference.get());
      loadingStateView.setId(R.id.evaluate_dialog);
      loadingStateView.setText(string);

      showDialogView(loadingStateView, null);
    }
  }

  @Override
  public void hideLoadingView() {
//        hideDialogView();
  }

  @Override
  public void hideFailView() {
//        hideDialogView();
  }

  @Override
  public void showSubmitProgress() {
  }

  @Override
  public void showSubmitSuccess() {
    mSubmitHelper.showSubmitted();

    UIThreadHandler.postDelayed(buttonDismiss, 1000);
  }

  @Override
  public void showSubmited() {
    mSubmitHelper.showSubmited();
  }

  private void showSubmitSuccessUI() {
    boolean preLoadingState = false;
    LoadingStateView loadingView = null;
    View oldView = mRootContainer.findViewById(R.id.evaluate_dialog);
    if (oldView != null && oldView instanceof LoadingStateView) {
      loadingView = (LoadingStateView) oldView;
      preLoadingState = true;
    }
    if (loadingView == null && mReference.get() != null) {
      loadingView = new LoadingStateView(mReference.get());
      loadingView.setId(R.id.evaluate_dialog);
    }
    if (mStarView.getLevel() == STAR_PERFECT) {
      loadingView.setText(R.string.oc_evaluate_submit_success_1);
    } else {
      loadingView.setText(R.string.oc_evaluate_submit_success_2);
    }
    loadingView.changeState(LoadingStateView.State.SUCCESS_STATE);
    if (!preLoadingState) {
      showDialogView(loadingView, oldView);
    }
  }

  @Override
  public void showSubmitFail() {
    if (mReference.get() != null) {
      showSubmitFail(mReference.get().getString(R.string.oc_evaluate_submit_failed));
    }
  }

  @Override
  public void showSubmitFail(CharSequence err) {
    mSubmitHelper.showSubmitFail();
    buttonDismiss.run();
  }

  @Override
  public void showLoadingFail(boolean evaluated) {
//    BaseEventPublisher.getPublisher().publish(EventKeys.ServiceEnd.EVENT_EVALUATE_DATA_LOAD_FAIL);
  }

  private <T extends View> T findView(@IdRes int id) {
    return (T) mView.findViewById(id);
  }

  private void showDialogView(View newView, View oldView) {
    View anchorView = findView(R.id.evaluate_container);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
        .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

    anchorView.setVisibility(View.INVISIBLE);

    if (oldView == null) {
      oldView = mRootContainer.findViewById(R.id.evaluate_dialog);
    }
    if (oldView != null && oldView.getParent() == mRootContainer) {
      mRootContainer.removeView(oldView);
    }
    mRootContainer.addView(newView, params);
  }

  @Override
  public void setOnCloseListener(OnCloseListener listener) {
    mOnCloseListener = listener;
  }

  @Override
  public void setOnCancelListener(OnCancelListener listener) {
    mOnCancelListener = listener;
  }

  @Override
  public void close() {
    // 场景：1.正常评价完成2s后关闭，展示评价结果页 2.加载失败，2s后返回评价入口，此时有可能评价入口没有展示过
    // 3.用户手动点击关闭，返回评价入口同2
    hidePopupHelper(true);
    if (mOnCloseListener != null) {
      mOnCloseListener.onClose();
    }
  }

  @Override
  public void setNoTagOrCommentList(int[] levels) {
    mNoNeedCommentList = levels;
  }

  @Override
  public void setQuestions(List<Question> questionList) {
    mQuestionView.setQuestionList(questionList);
    mQuestionView.setOnQuestionViewActionListener(this);
    mQuestionView.showQuestionView();
    if (mEvaluateListener != null) {
      mQuestionView.showEvaluateEntrance(mEvaluateListener.hasEvaluateData());
    }
  }

  @Override
  public void setOnQuestionViewActionListener(
      QuestionView.OnQuestionViewActionListener onQuestionViewActionListener) {
    mOnQuestionViewActionListener = onQuestionViewActionListener;
  }

  @Override
  public void notifyQuestionSubmitSuccess() {
    notifyQuestionSubmitSuccess("");
  }

  @Override
  public void notifyQuestionSubmitSuccess(String message) {
    if (isQuestionShow()) {
      mQuestionView.showSubmitSuccessView(message);
    }
  }

  @Override
  public void notifyQuestionSubmitFail() {
    if (isQuestionShow()) {
      mQuestionView.showSubmitFailView();
    }
  }

  @Override
  public View getSubmitButton() {
    //提交按钮
    TextView button = (TextView) mRootContainer.findViewById(R.id.submit_button_view);
    View submitting = mRootContainer.findViewById(R.id.submitting_view);
    View container = mRootContainer.findViewById(R.id.evaluate_submit_container);
    mSubmitHelper = new SubmitHelper(button, submitting, container);
    mSubmitHelper.init();
    return container;
  }

  @Override
  public void notifyViewSetup() {
    if (mMode == Mode.Rating) {
      showPopupHelper();
    } else if (mMode == Mode.QuestionThenRating) {
      if (mQuestionView != null) {
        mQuestionView.showPopupHelper();
      }
    }
  }

  @Override
  public void onResume() {
    mKeyboardHeightDetector.setKeyboardHeightObserver(mHeightObserver);
  }

  @Override
  public void onPause() {
    mKeyboardHeightDetector.setKeyboardHeightObserver(null);
  }

  @Override
  public void hasExtendView(boolean hasExtend) {
    mHasExtendView = hasExtend;
    if (mHasExtendView && null == mExtendViewLayout) {
      ViewStub extendViewStub = findView(R.id.extend_view);
      mExtendViewLayout = (RelativeLayout) extendViewStub.inflate();
    }
  }

  @Override
  public void addExtendView(View extendView) {
    if (null != mExtendViewLayout) {
      RelativeLayout container = (RelativeLayout) mExtendViewLayout
          .findViewById(R.id.evaluate_extend_container);
      container.removeAllViews();
      if (null != extendView) {
        container.addView(extendView,
            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        mExtendViewLayout.setVisibility(View.VISIBLE);
      } else {
        mExtendViewLayout.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onTagSelectChange(EvaluateTag tag, boolean selected) {
    if (needTagOrComment(mStarView.getLevel())) {
      if (mTagListView.getSelectedTags().size() > 0) {
        enableSubmitButton(true);
      } else {
        if (mCommentVisible) {
          if (TextUtils.isEmpty(mCommentView.getText())) {
            enableSubmitButton(false);
          } else {
            enableSubmitButton(true);
          }
        } else {
          enableSubmitButton(false);
        }
      }
    }

    if (mEvaluateListener != null) {
      mEvaluateListener.onEvaluateTagSelectChange(mStarView.getLevel(), tag, selected);
    }
  }

  public boolean needTagOrComment(int level) {
    if (mNoNeedCommentList != null && mNoNeedCommentList.length > 0) {
      boolean needComment = true;
      for (int star : mNoNeedCommentList) {
        if (level == star) {
          needComment = false;
          break;
        }
      }
      return needComment;
    }
    return level != STAR_PERFECT;
  }

  /**
   * 星星回调
   */
  @Override
  public void onTouchStarChange(int level) {
    onStarChange(true, level);
//    if (mHasExtendView && null != mExtendViewLayout) {
//      mExtendViewLayout.setVisibility(View.GONE);
//    }
  }

  private void onStarChange(boolean animateOnTag, int level) {
    //当用户开始评价时 1.展示提交按钮 2.金牌服务标准隐藏 3.展示评价标签和文案输入 4.更新星级下方文案 5.更新评价标签文案 6.刷新评价标签列表 7.隐藏pop helper
    //切换状态为评价中
    if (level <= 0 || level > STAR_PERFECT) {
      return;
    }
//    if (mState == State.Before_Commenting) {
//    }
    mState = State.Commenting;

    if (!mNotDismissHelper) {
      hidePopupHelper(false);
    } else {
      mNotDismissHelper = false;
    }

    if (mRateDescriptions != null) {
      RateDescription rateDescription = mRateDescriptions.get(level);
      if (rateDescription != null) {
        if (rateDescription.getTextRes() > 0) {
          mStarDescription.setText(rateDescription.getTextRes());
        } else {
          mStarDescription.setText(rateDescription.getText());
        }
      }
    }

    if (mTagListVisible && mRateTags != null) {
      setTagAreaVisibility(mTagListVisible);
      EvaluateRateTags tags = mRateTags.get(level);
      if (tags != null && tags.getTags() != null && tags.getTags().size() > 0) {
        mTagListView.animateOnFirstSetup(animateOnTag);
        mTagListView.updateEvaluateTags(tags.getTags());
        mTagListView.setTagSelectable(true);
      } else {
        mTagListView.clear();
      }
    }

    if (mCommentVisible) {
      setCommentAreaVisibility(mCommentVisible);
    }

    if (false/*needTagOrComment(level)*/) {
      if (!mCommentVisible && !mTagListVisible) {
        enableSubmitButton(true);
      } else {
        if ((mTagListVisible && mTagListView.getSelectedTags().isEmpty())
            || (mCommentView != null && TextUtils
            .isEmpty(mCommentView.getText()))) {
          enableSubmitButton(false);
        } else {
          enableSubmitButton(true);
        }
      }
    } else {
      enableSubmitButton(true);
    }

    if (mEvaluateListener != null) {
      mEvaluateListener.onRateChange(level);
    }
  }

  /**
   * 评论区内容变化回调
   */
  @Override
  public void onContentChange(CharSequence content) {
    if (needTagOrComment(mStarView.getLevel())) {
      String comment = content.toString().trim();
      if (!TextUtils.isEmpty(comment)) {
        enableSubmitButton(true);
      } else {
        if (!mTagListVisible) {
          enableSubmitButton(false);
        } else {
          if (mTagListView != null && mTagListView.getSelectedTags().size() == 0) {
            enableSubmitButton(false);
          } else {
            enableSubmitButton(true);
          }
        }
      }
    }
  }

  private void enableSubmitButton(boolean enable) {
    mSubmitHelper.setEnable(enable);
  }

  private void clearCommentViewFocus() {
    if (mCommentView != null) {
      if (mCommentView.isFocused()) {
        mCommentView.clearFocus();
      }
    }
  }

  /**
   * 评论框获得焦点时，标签列表的点击事件会被屏蔽，但是点击时需要清除评论框焦点，隐藏输入法软键盘
   */
  @Override
  public void onTouchWhenIntercept() {
    clearCommentViewFocus();
  }

  private void showSubmitDisable() {
    if (mEvaluateListener != null) {
      mEvaluateListener.onSubmitDisable();
    }
  }

  private void submit(boolean withProgress) {
    if (mEvaluateListener != null) {
      if (withProgress) {
        showLoadingView(R.string.oc_evaluate_submitting);
      }
      mSubmitHelper.showProgress();

      List<EvaluateTag> tags = null;
      if (mTagListView != null) {
        tags = mTagListView.getSelectedTags();
      }
      String comment = "";
      if (mCommentView != null) {
        comment = mCommentView.getText();
      }
      submitTraceEvent(comment);
      mEvaluateListener.onSubmit(mStarView.getLevel(), tags, comment);
    }
  }

  private void submitTraceEvent(String comment) {
    Map map = new HashMap();
    map.put("comment", comment);
  }

  @Override
  public void onCloseBtnClick() {
    clearCommentViewFocus();
    close();
  }

  class SubmitHelper implements View.OnClickListener {

    final int STATE_NONE = -1;
    final int STATE_NEW = 0;
    final int STATE_SUBMITTING = 1;
    final int STATE_SUBMIT_FAIL = 2;
    final int STATE_SUBMIT_SUCCESS = 3;

    int state = STATE_NONE;

    TextView button;
    View submittingView;
    View container;

    public SubmitHelper(TextView button, View submitting, View container) {
      this.button = button;
      this.submittingView = submitting;
      this.container = container;
    }

    public void init() {
      container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (state != STATE_NEW) {
        return;
      }
      if (button.isEnabled()) {
        submit(false);
      } else {
        showSubmitDisable();
      }
    }

    public void setEnable(boolean enable) {
      button.setEnabled(enable);
      button.setVisibility(View.VISIBLE);
      submittingView.setVisibility(View.GONE);
      container.setVisibility(View.VISIBLE);

      if (state == STATE_NONE) {
        showSubmit();
      }
    }

    public void showSubmit() {
      state = STATE_NEW;
      if (container.getVisibility() != View.VISIBLE) {
        container.setVisibility(View.VISIBLE);
      }
    }

    public void showProgress() {
      state = STATE_SUBMITTING;
      button.setVisibility(View.GONE);
      submittingView.setVisibility(View.VISIBLE);
      playProgress();
    }

    public void showSubmitted() {
      state = STATE_SUBMIT_SUCCESS;
      button.setText(R.string.oc_evaluate_submitted);
      button.setVisibility(View.VISIBLE);
      submittingView.setVisibility(View.GONE);
      stopProgress();
    }

    public void showSubmited() {
      state = STATE_SUBMIT_SUCCESS;
      stopProgress();
      hide();
    }

    public void showSubmitFail() {
      state = STATE_SUBMIT_FAIL;
    }

    public void hide() {
      container.setVisibility(View.INVISIBLE);
    }

    private void playProgress() {
      ImageView progress = (ImageView) submittingView;
      AnimationDrawable frameAnimation = (AnimationDrawable) progress.getDrawable();
      frameAnimation.setCallback(progress);
      frameAnimation.setVisible(true, true);
      frameAnimation.start();
    }

    private void stopProgress() {
      ImageView progress = (ImageView) submittingView;
      AnimationDrawable frameAnimation = (AnimationDrawable) progress.getDrawable();
      frameAnimation.stop();
      frameAnimation.setCallback(null);
      frameAnimation = null;
      progress.setVisibility(View.GONE);
    }

  }

  @Override
  public void onToRatingClick(int level) {
    dismissTipsView();
    if (mOnQuestionViewActionListener != null) {
      mOnQuestionViewActionListener.onToRatingClick(level);
    }
    if (mEvaluateListener != null) {
      mEvaluateListener.onSwitchToEvaluate();
    }
    mQuestionView.setVisibility(View.GONE);
    findView(R.id.evaluate_container).setVisibility(View.VISIBLE);
  }

  private void traceQuestion(int id) {
    Map<String, Object> map = new HashMap<>();
    map.put("evtype", 2);
    map.put("qtid", id);
  }

  @Override
  public void onQuestionShow(int index, Question question) {
    traceQuestion(question.id);
    if (mOnQuestionViewActionListener != null) {
      mOnQuestionViewActionListener.onQuestionShow(index, question);
    }
  }

  @Override
  public void onSelect(int index, Question question, CharSequence selection) {
    if (mOnQuestionViewActionListener != null) {
      mOnQuestionViewActionListener.onSelect(index, question, selection);
    }
    dismissTipsView();
  }

  @Override
  public void onQuestionDone() {
    mQuestionView.showSubmittingView();
    if (mOnQuestionViewActionListener != null) {
      mOnQuestionViewActionListener.onQuestionDone();
    }
  }

  @Override
  public View getView() {
    return mView;
  }

  @Override
  public void onShowTips(final View view) {
  }

  private void dismissTipsView() {
    try {
      if (mTipsView != null) {
//        mTipsView.detachFromContainer();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
