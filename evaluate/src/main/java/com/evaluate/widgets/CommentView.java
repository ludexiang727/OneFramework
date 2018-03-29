package com.evaluate.widgets;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.evaluate.R;


/**
 * 评价输入框
 */
public class CommentView extends FrameLayout implements View.OnFocusChangeListener, TextWatcher {

  private static final int MAX_TEXT_LENGTH = 60;
  private static final int LINE_THRESHOLD = 4;
  private static final int LINE_FEED_THREADSHOLD = 3;
  private static final int LINE_THRESHOLD_MAX = 6;
  private static final String TAG = "comment";

  private EditText mInputView;
  private TextView mLimitCountView;
  private int mLastLineCount = 1;

  private EditTextHeightAnimator mAnimator;
  private TextView mContentView;
  private OnContentChangeListener mOnContentChangeListener;
  private int targetHeight;

  public CommentView(Context context) {
    super(context);
    init();
  }

  public CommentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CommentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.oc_evaluate_comment_input_view, this);
    mInputView = (EditText) findViewById(R.id.evaluate_comment_view);
    mInputView.setOnFocusChangeListener(this);
    mInputView.addTextChangedListener(this);
    mLimitCountView = (TextView) findViewById(R.id.evaluate_comment_text_limit_view);
    mLimitCountView.setText(getResources().getInteger(R.integer.evaluate_max_length) + "");

    mContentView = (TextView) findViewById(R.id.evaluate_comment_content_view);
  }

  boolean hasFocus = false;

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      this.hasFocus = true;
      mLimitCountView.setVisibility(View.VISIBLE);
    } else {
      this.hasFocus = false;
      InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
          .INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(mInputView.getWindowToken(), 0);
      updateHeightOnFocusLoss();
      mLimitCountView.setVisibility(View.GONE);
    }
  }

  public String getText() {
    return mInputView.getText().toString().trim();
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    int count = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '\n') {
        count++;
      }
    }
    if (count > LINE_FEED_THREADSHOLD) {
      count--;
      s.delete(s.length() - 1, s.length());
    }
    if (s.length() > MAX_TEXT_LENGTH) {
//            currentText = s.;
    } else {
      updateHeightOnFocus();
    }

    int remaindLen = MAX_TEXT_LENGTH - s.length();
    mLimitCountView.setText((remaindLen >= 0 ? remaindLen : 0) + "");
    if (mOnContentChangeListener != null) {
      mOnContentChangeListener.onContentChange(s.toString());
    }
  }

  private void updateHeightOnFocus() {
    if (mAnimator != null && mAnimator.isStarted() && mAnimator.isRunning()) {
      mAnimator.end();
    }
    int lineCount = expandLine();
    int targetLineCount = Math.max(lineCount, LINE_THRESHOLD);

    if (targetLineCount != mLastLineCount) {
      playAnim(targetLineCount);
    }
  }

  private int expandLine() {
    int lineCount = mInputView.getLineCount();
    if (lineCount >= LINE_THRESHOLD) {
      lineCount = LINE_THRESHOLD;
    }
    return lineCount;
  }

  private void updateHeightOnFocusLoss() {
    if (mAnimator != null && mAnimator.isStarted() && mAnimator.isRunning()) {
      mAnimator.end();
    }
    playAnim(
        mInputView.getLineCount() > LINE_THRESHOLD ? LINE_THRESHOLD : mInputView.getLineCount());
  }

  private void playAnim(final int targetLineCount) {
    mAnimator = new EditTextHeightAnimator(mInputView, mLastLineCount, targetLineCount);
    mAnimator.setOnFinishListener(new EditTextHeightAnimator.OnFinishListener() {
      @Override
      public void onFinish() {
        mLastLineCount = targetLineCount;
      }
    });
    mAnimator.start();
  }

  @Override
  public void clearFocus() {
    super.clearFocus();
    mInputView.clearFocus();
  }

  @Override
  public boolean isFocused() {
    return mInputView.isFocused();
  }

  public void setContent(String content) {
    mInputView.setVisibility(View.GONE);
    mLimitCountView.setVisibility(View.GONE);
    if (!TextUtils.isEmpty(content)) {
      mContentView.setText("\"" + content + "\"");
      mContentView.setVisibility(View.VISIBLE);
    }
  }

  public void onKeyboardHeightChange(int height) {
    if (height > 0 && hasFocus) {
      updateHeightOnFocus();
    }
  }

  public int getFullHeight() {
    if (mAnimator != null) {
      return mAnimator.getTargetHeight();
    }
    return (int) (2 * mContentView.getLineHeight());
  }

  public interface OnContentChangeListener {

    void onContentChange(CharSequence content);
  }

  public void setOnContentChangeListener(OnContentChangeListener listener) {
    mOnContentChangeListener = listener;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  /**
   * 根据EditText的行数来做高度变化的动画
   */
  static class EditTextHeightAnimator extends ValueAnimator implements ValueAnimator
      .AnimatorUpdateListener, Animator.AnimatorListener {

    private int mDeltaHeight;
    private EditText mView;
    private int mOriginalHeight;

    private OnFinishListener mEndListener;

    public EditTextHeightAnimator(EditText view, int fromLine, int toLine) {
      mView = view;

      int lineHeight = view.getLineHeight();
      int lineSpace = 0;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        lineSpace = (int) view.getLineSpacingExtra();
      } else {
      }

      int deltaLine = toLine - fromLine;
      mDeltaHeight = deltaLine * (lineHeight + lineSpace);
      mOriginalHeight = fromLine * (lineHeight + lineSpace) + mView.getPaddingBottom() + mView
          .getPaddingTop();
    }

    @Override
    public void start() {
      setIntValues(0, mDeltaHeight);
      setDuration(Math.abs(mDeltaHeight) * 2);
      setInterpolator(new LinearInterpolator());
      this.addUpdateListener(this);
      this.addListener(this);
      super.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      mView.setHeight(mOriginalHeight + (int) animation.getAnimatedValue());
    }

    public void setOnFinishListener(OnFinishListener l) {
      mEndListener = l;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
      if (mEndListener != null) {
        mEndListener.onFinish();
        if (mView.getLineCount() < LINE_THRESHOLD) {
          mView.setMaxLines(LINE_THRESHOLD);
        } else {
          mView.setMaxLines(LINE_THRESHOLD_MAX);
        }
        mView.requestLayout();
      }
//            BaseEventPublisher.getPublisher().publish(EventKeys.ServiceEnd
//                    .EVENT_EVALUATE_KEYBOARD_SHOW, 0);
    }

    @Override
    public void end() {
      super.end();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public int getTargetHeight() {
      return mDeltaHeight;
    }

    public interface OnFinishListener {

      void onFinish();
    }
  }

}
