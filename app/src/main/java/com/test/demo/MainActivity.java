package com.test.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.evaluate.model.DefaultRateDescriptions;
import com.evaluate.model.EvaluateRateTags;
import com.evaluate.model.EvaluateTag;
import com.evaluate.view.EvaluateView;
import com.evaluate.view.IEvaluateView;
import com.evaluate.view.IEvaluateView.EvaluateListener;
import com.evaluate.view.IEvaluateView.Mode;
import com.evaluate.widgets.CardTitleView;
import com.evaluate.widgets.CardTitleView.CardTitleCloseBtnListener;
import com.test.demo.anim.PopWindowViewAnimator;
import com.test.demo.animators.ViewAnimator;
import com.test.demo.animators.ViewAnimator.DefaultAnimatorListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EvaluateListener {


  class Tag implements EvaluateTag {

    private boolean isSelected;
    private String text;
    private long id;

    public Tag(String text, long id) {
      this.text = text;
      this.id = id;
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public long getId() {
      return id;
    }

    @Override
    public void setSelected(boolean selected) {
      isSelected = selected;
    }

    @Override
    public boolean isSelected() {
      return isSelected;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    evaluateView = new EvaluateView(this);
    evaluateView.setEvaluateListener(this);
    initPop();
//    testTags();
  }

  private void testTags() {
    List<EvaluateTag> oneTags = new ArrayList<EvaluateTag>();
    for (int i = 0; i < 6; i++) {
      EvaluateTag tag = new Tag("This is one " + i, i);
      oneTags.add(tag);
    }
    List<EvaluateTag> twoTags = new ArrayList<EvaluateTag>();
    for (int i = 0; i < 6; i++) {
      EvaluateTag tag = new Tag("This is two " + i, i);
      twoTags.add(tag);
    }
    List<EvaluateTag> threeTags = new ArrayList<EvaluateTag>();
    for (int i = 0; i < 6; i++) {
      EvaluateTag tag = new Tag("This is three " + i, i);
      threeTags.add(tag);
    }
    List<EvaluateTag> fourTags = new ArrayList<EvaluateTag>();
    for (int i = 0; i < 6; i++) {
      EvaluateTag tag = new Tag("This is four " + i, i);
      fourTags.add(tag);
    }
    List<EvaluateTag> fiveTags = new ArrayList<EvaluateTag>();
    for (int i = 0; i < 6; i++) {
      EvaluateTag tag = new Tag("This is five " + i, i);
      fiveTags.add(tag);
    }
    EvaluateRateTags one = new RateTags(1, oneTags);
    EvaluateRateTags two = new RateTags(2, twoTags);
    EvaluateRateTags three = new RateTags(3, threeTags);
    EvaluateRateTags four = new RateTags(4, fourTags);
    EvaluateRateTags five = new RateTags(5, fiveTags);
    List<EvaluateRateTags> rateTags = new ArrayList<>();
    rateTags.add(one);
    rateTags.add(two);
    rateTags.add(three);
    rateTags.add(four);
    rateTags.add(five);
    evaluateView.setTagAreaVisibility(true);
    evaluateView.setRateTags(rateTags);
    evaluateView.setRateDescriptions(DefaultRateDescriptions.getRateDescriptionList());
  }

  class RateTags implements EvaluateRateTags {

    int rate;
    List<EvaluateTag> tags = new ArrayList<>();

    public RateTags(int rate, List<EvaluateTag> tags) {
      this.rate = rate;
      this.tags.addAll(tags);
    }

    @Override
    public int getRate() {
      return rate;
    }

    @Override
    public String getText() {
      return null;
    }

    @Override
    public List<EvaluateTag> getTags() {
      return tags;
    }

    @Override
    public int getTextRes() {
      return 0;
    }
  }


  public void onChange(View v) {

  }

  public void onJump(View v) {
    startActivity(new Intent(this, TranslateActivity.class));
  }

  private PopupWindow mPopWindow;
  IEvaluateView evaluateView;

  private void initPop() {
    mPopWindow = new PopupWindow(evaluateView.getView(), LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);
    mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a0000000")));
    mPopWindow.setOutsideTouchable(true);
    mPopWindow.setAnimationStyle(R.style.PopupWindow);
    evaluateView.setMode(Mode.Rating);
    evaluateView.hasExtendView(true);
    CardTitleView titleView = new CardTitleView(this);
    titleView.setTitle(R.string.evaluate_title);
    evaluateView.addExtendView(titleView);
    titleView.setCloseIconListener(new CardTitleCloseBtnListener() {
      @Override
      public void onCloseBtnClick() {
        dismiss();
      }
    });
    testTags();
  }

  public void onEvaluate(View v) {
    if (mPopWindow != null && !mPopWindow.isShowing()) {
      mPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
      ViewAnimator pop = new PopWindowViewAnimator(true);
      pop.setDuration(500);
      pop.attachView(mPopWindow.getContentView());
      pop.start();
    }
  }

  private void dismiss() {
    if (mPopWindow != null && mPopWindow.isShowing()) {
      ViewAnimator pop = new PopWindowViewAnimator(false);
      pop.setDuration(500);
      pop.attachView(mPopWindow.getContentView());
      pop.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          mPopWindow.dismiss();
        }
      });

      pop.start();
    }
  }

  @Override
  public void onRateChange(int rate) {

  }

  @Override
  public void onEvaluateTagSelectChange(int rate, @NonNull EvaluateTag tag, boolean select) {

  }

  @Override
  public void onSubmit(int rate, @Nullable List<EvaluateTag> tags, @NonNull String comment) {
    evaluateView.setMode(IEvaluateView.Mode.View);
    evaluateView.setTags(tags);
    evaluateView.setRate(rate);
    evaluateView.showSubmited();
  }

  @Override
  public void onLoadData() {

  }

  @Override
  public void onSubmitDisable() {

  }

  @Override
  public void onSwitchToEvaluate() {

  }

  @Override
  public boolean hasEvaluateData() {
    return false;
  }
}
