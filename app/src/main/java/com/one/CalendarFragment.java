package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.adapter.AbsBaseAdapter;
import com.one.adapter.impl.CalendarAdapter;
import com.one.base.BaseFragment;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.model.BizInfo;
import com.one.framework.app.model.IBusinessContext;
import com.one.listener.ICalendar;
import com.one.model.CalendarModel;
import com.one.widget.CalendarTitleLayout;
import com.one.widget.CustomerSwipeRefreshLayout;
import com.one.widget.PullListView;
import com.one.widget.PullScrollRelativeLayout;
import com.one.widget.SwipeListView;
import com.test.demo.R;
import com.test.demo.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/2.
 */
@ServiceProvider(value = Fragment.class, alias = "calendar")
public class CalendarFragment extends BaseFragment {

  private PullScrollRelativeLayout mPullLayout;
  private AbsBaseAdapter mAdapter;
  private PullListView mSwipeListView;
  private ICalendar mCalendar;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.panther_calendar_layout, null);
    initView(view);
    return view;
  }


  private void initView(View view) {
    mPullLayout = (PullScrollRelativeLayout) view.findViewById(R.id.pull_scroll_layout);
    mCalendar = (CalendarTitleLayout) view.findViewById(R.id.calendar_title_layout);
    mSwipeListView = (PullListView) view.findViewById(android.R.id.list);

    mPullLayout.setScrollView(mSwipeListView);
    mPullLayout.setMoveListener(mSwipeListView);
    mAdapter = new CalendarAdapter(getActivity());
    mSwipeListView.setAdapter(mAdapter);

    mAdapter.setListData(testDemo());
  }

  /// testDemo

  private List<CalendarModel> testDemo() {
    List<CalendarModel> lists = new ArrayList<CalendarModel>();
    for (int i=0;i< 10;i++) {
      CalendarModel model = new CalendarModel();
      model.busLine = "南宫" + i + "号线";
      model.endAdr = "东村家园";
      model.startAdr = "曼宁国际";
      model.price = "" + i;
      model.walk = "南行685米";
      model.startTime = TimeUtils.convertMillisToString(getActivity(), System.currentTimeMillis(), true);
      model.endTime = TimeUtils.convertMillisToString(getActivity(), System.currentTimeMillis() + i * 10000, true);
      lists.add(model);
    }
    return lists;
  }

}
