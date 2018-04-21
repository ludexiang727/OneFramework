package com.one.adapter.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.one.adapter.impl.CalendarAdapter.CalendarHolder;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.app.widget.TripButton;
import com.one.model.CalendarModel;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/4/2.
 */

public class CalendarAdapter extends AbsBaseAdapter<CalendarModel, CalendarHolder> {
  public CalendarAdapter(Context context) {
    super(context);
  }

  @Override
  protected CalendarHolder createHolder() {
    return new CalendarHolder();
  }

  @Override
  protected void initView(View view, CalendarHolder holder) {
    holder.busLine = (TextView) view.findViewById(R.id.bus_line);
    holder.startTime = (TextView) view.findViewById(R.id.start_time);
    holder.startAdr = (TextView) view.findViewById(R.id.start_adr);
    holder.endTime = (TextView) view.findViewById(R.id.end_time);
    holder.endAdr = (TextView) view.findViewById(R.id.end_adr);
    holder.walkNav = (TextView) view.findViewById(R.id.walk_nav);
//    holder.buy = (TripButton) view.findViewById(R.id.buy);
  }

  @Override
  protected void bindData(CalendarModel bean, CalendarHolder holder, int position) {

  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.calendar_item_layout, null);
  }

  class CalendarHolder {

    TextView busLine;
    TextView startTime;
    TextView startAdr;
    TextView endTime;
    TextView endAdr;
    TextView walkNav;
    TripButton buy;
  }
}
