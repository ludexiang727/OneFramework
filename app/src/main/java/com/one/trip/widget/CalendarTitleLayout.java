package com.one.trip.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.utils.TimeUtils;
import com.one.trip.listener.ICalendar;
import com.one.trip.R;
import java.util.Calendar;

/**
 * Created by ludexiang on 2018/4/2.
 */

public class CalendarTitleLayout extends RelativeLayout implements ICalendar, OnClickListener {
  private LayoutInflater mInflater;
  private static final long ONE_DAY = 24 * 60 * 60 * 1000;
  private TextView mTime;
  private TextView mBefore;
  private TextView mNext;
//  private IDayChoose mChoose;
  private long mCurrentShowTime;
  private Calendar mCalendar;

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
    mCalendar = Calendar.getInstance();
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mTime = (TextView) findViewById(R.id.calendar_current_day);
    mBefore = (TextView) findViewById(R.id.calendar_before_day);
    mNext = (TextView) findViewById(R.id.calendar_after_day);
//    mTime.setOnClickListener(this);
    mBefore.setOnClickListener(this);
    mNext.setOnClickListener(this);

    setTime(System.currentTimeMillis());
  }

//  @Override
  public void setTime(long time) {
    mCurrentShowTime = time;
    if (TimeUtils.getDayDiff(mCurrentShowTime) == 0) {
      mBefore.setEnabled(false);
    } else if (TimeUtils.getDayDiff(mCurrentShowTime) >= 4) {
      mNext.setEnabled(false);
    }
//    mTime.setText(TimeUtils.convertMonthMillis(getContext(), time, true, false, true));
  }

//  @Override
//  public void setDayChoose(IDayChoose choose) {
//    mChoose = choose;
//  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.calendar_current_day: {
//        if (mChoose != null) {
//          mChoose.currentChoose();
//        }
        break;
      }
      case R.id.calendar_before_day: {
        mNext.setEnabled(true);
        long time = mCurrentShowTime - ONE_DAY;
        String xxx = TimeUtils.convertMillisToString(getContext(), time, true);
        Log.e("ldx", " before " + xxx);
        setTime(time);
//        if (mChoose != null) {
//          mChoose.before(time);
//        }
        break;
      }
      case R.id.calendar_after_day: {
        mBefore.setEnabled(true);
        String current = TimeUtils.convertMillisToString(getContext(), mCurrentShowTime, true);
        Log.e("ldx", " current " + current);
        long time = mCurrentShowTime + ONE_DAY - getExtra(mCurrentShowTime);
        String xxx = TimeUtils.convertMillisToString(getContext(), time, true);
        Log.e("ldx", " next " + xxx);
        setTime(time);
//        if (mChoose != null) {
//          mChoose.next(time);
//        }
        break;
      }
    }
  }

  private long getExtra(long currentTime) {
    mCalendar.setTimeInMillis(currentTime);
    int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = mCalendar.get(Calendar.MINUTE);
    int second = mCalendar.get(Calendar.SECOND);
    return hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
  }
}
