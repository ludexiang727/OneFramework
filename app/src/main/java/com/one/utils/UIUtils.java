package com.one.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mobike on 2017/12/26.
 */

public class UIUtils {

  private static long sLastClickTime = 0;
  private static final long DURATION = 500L;

  private static Integer sScreenWidth = null;
  private static Integer sScreenHeight = null;

  /**
   * 检验是否是快速点击
   */
  public static boolean isFastDoubleClick() {
    long curTime = System.currentTimeMillis();
    boolean fastClick = curTime - sLastClickTime < DURATION;
    sLastClickTime = curTime;
    return fastClick;
  }

  /**
   * 获取屏幕宽度
   */
  public static int getScreenWidth(Context ctx) {
    if (sScreenWidth != null) {
      return sScreenWidth;
    }

    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    sScreenWidth = wm.getDefaultDisplay().getWidth();
    sScreenHeight = wm.getDefaultDisplay().getHeight();
    return sScreenWidth;
  }

  /**
   * 获取屏幕高度
   */
  public static int getScreenHeight(Context ctx) {
    if (sScreenHeight != null) {
      return sScreenHeight;
    }

    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    sScreenWidth = wm.getDefaultDisplay().getWidth();
    sScreenHeight = wm.getDefaultDisplay().getHeight();
    return sScreenHeight;
  }

  private static Float sDensity = null;

  private static float getDensity(Context ctx) {
    if (sDensity == null) {
      sDensity = ctx.getResources().getDisplayMetrics().density;
    }
    return sDensity;
  }

  /**
   * 将一个dip值转化为px值
   */
  public static float dip2px(Context ctx, float dip) {
    return dip * getDensity(ctx);
  }

  /**
   * 将一个dip值转化为px值
   */
  public static int dip2pxInt(Context ctx, float dip) {
    return (int) (dip * getDensity(ctx) + 0.5);
  }

  private static Integer sStatusBarHeight = null;

  /**
   * 获取StatusBar的高度
   */
  public static int getStatusbarHeight(Context context) {
    if (sStatusBarHeight != null) {
      return sStatusBarHeight;
    }

    Resources resources = context.getResources();
    int resId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resId <= 0) {
      return (sStatusBarHeight = dip2pxInt(context, 25));
    }

    try {
      return (sStatusBarHeight = resources.getDimensionPixelSize(resId));
    } catch (Resources.NotFoundException e) {
      return (sStatusBarHeight = dip2pxInt(context, 25));
    }

  }

  /**
   * 打印整个视图的信息,用户调试
   */
  public static void dumpViewHierarchy(String tag, View view) {
    if (view == null || TextUtils.isEmpty(tag)) {
      return;
    }
    /** 定义一个location数组,用于复用*/
    Log.i(tag, "invoke stack trace", new Throwable());
    int[] location = new int[2];
    StringBuilder builder = new StringBuilder();
    dumpViewHierarchyImpl(tag, view, builder, location);
  }

  private static void dumpViewHierarchyImpl(String tag, View view, StringBuilder builder,
      int[] location) {
    builder.setLength(0);
    /** 视图本身的toString信息*/
    builder.append("view: ").append(view);
    builder.append("\n");
    /** 位置信息*/
    view.getLocationOnScreen(location);
    builder.append(", location on screen").append(Arrays.toString(location));
    view.getLocationInWindow(location);
    builder.append(", location in window ").append(Arrays.toString(location));
    /** 可见性*/
    builder.append(", visible: ").append(view.getVisibility() == View.VISIBLE);
    builder.append(", visibility").append(view.getVisibility());
    /** 透明度信息*/
    builder.append(", alpha: ").append(view.getAlpha());
    /** 平移信息*/
    builder.append(", translationX").append(view.getTranslationX());
    builder.append(", translationY").append(view.getTranslationY());
    /** 缩放信息*/
    builder.append(", scaleX").append(view.getScaleX());
    builder.append(", scaleY").append(view.getScaleY());
    /** 宽高信息*/
    builder.append(", width").append(view.getWidth());
    builder.append(", height").append(view.getHeight());
    builder.append("\n");

    Log.i(tag, builder.toString());

    if (!(view instanceof ViewGroup)) {
      return;
    }

    ViewGroup viewGroup = (ViewGroup) view;
    int childCount = viewGroup.getChildCount();
    for (int i = 0; i < childCount; i++) {
      dumpViewHierarchyImpl(tag, viewGroup.getChildAt(i), builder, location);
    }
  }

  public static void setEditTextHint(EditText editText, String txt, int size) {
    SpannableString ss = new SpannableString(txt);
    AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
    ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    editText.setHint(new SpannedString(ss));
  }

  public static CharSequence highlight(String input, String format, String formatColor) {
    Pattern pattern = Pattern.compile(format);
    Matcher matcher = pattern.matcher(input);

    Stack<Range> matches = new Stack<>();
    while (matcher.find()) {
      matches.push(new Range(matcher.start(), matcher.end()));
    }

    SpannableStringBuilder builder = new SpannableStringBuilder(input);
    int color = Color.parseColor(formatColor);
    while (matches.size() > 0) {
      Range range = matches.pop();
      builder.delete(range.start, range.start + 1);
      builder.delete(range.end - 2, range.end - 1);
      builder.setSpan(new ForegroundColorSpan(color), range.start, range.end - 2,
          Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    return builder;
  }

  public static CharSequence highlight(String input, String format) {
    return highlight(input, format, "#F05B48");
  }

  static class Range {
    public final int start;
    public final int end;

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }
}
