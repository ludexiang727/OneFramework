package com.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.adapter.ListAdapter;
import com.one.base.BaseFragment;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.widget.PullListView;
import com.one.map.model.BestViewModel;
import com.one.model.ListModel;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.test.demo.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */
@ServiceProvider(value = Fragment.class, alias = "premium")
public class PremiumFragment extends BaseFragment {

  private PullScrollRelativeLayout mMoveParentLayout;
  private PullListView mListView;
  private AbsBaseAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.two_fragment_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mMoveParentLayout = (PullScrollRelativeLayout) view.findViewById(R.id.move_parent_layout);
    mListView = (PullListView) view.findViewById(android.R.id.list);

    mMoveParentLayout.setScrollView(mListView);
    mMoveParentLayout.setMoveListener(mListView);
    mAdapter = new ListAdapter(getActivity());
    mListView.setAdapter(mAdapter);

    mAdapter.setListData(testDemo());
  }

  @Override
  protected void boundsLatlng(BestViewModel bestView) {

  }

  //////////////// test demo
  private List<ListModel> testDemo() {
    List<ListModel> datas = new ArrayList<ListModel>();
    for (int i = 0; i< 15;i++) {
      ListModel model = new ListModel();
      model.title = "This is test " + i;
      datas.add(model);
    }
    return datas;
  }
}
