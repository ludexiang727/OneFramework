package com.one.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import com.one.listener.ICalendar;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/4/2.
 */

public class CalendarTitleLayout extends RelativeLayout implements ICalendar {
  private LayoutInflater mInflater;

  public CalendarTitleLayout(Context context) {
    this(context, null);
  }

  public CalendarTitleLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CalendarTitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mInflater = LayoutInflater.from(context);
    mInflater.inflate(R.layout.calendar_title_bar_layout, this, true);
  }
}
