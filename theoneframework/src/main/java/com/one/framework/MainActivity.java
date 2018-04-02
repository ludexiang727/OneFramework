package com.one.framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.one.framework.app.map.IMapFragment;
import com.one.framework.app.map.MapFragment;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.page.impl.TopBarFragment;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.manager.ActivityDelegateManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/26.
 */

public class MainActivity extends FragmentActivity implements ITabItemListener {

  private ActivityDelegateManager mDelegateManager;
  private IMapFragment mMapFragment;
  private ITopbarFragment mTopbarFragment;
  private INavigator mNavigator;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.one_main_activity);

    addNavigator();

    mMapFragment = (MapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.one_map_fragment);
    mTopbarFragment = (TopBarFragment) getSupportFragmentManager()
        .findFragmentById(R.id.one_top_bar_fragment);
    mTopbarFragment.setTabItemListener(this);

    mDelegateManager = new ActivityDelegateManager(this);
    mDelegateManager.notifyOnCreate();

    mTopbarFragment.setTabItems(testTabItems());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mDelegateManager.notifyOnStart();
  }


  @Override
  protected void onResume() {
    super.onResume();
    mDelegateManager.notifyOnResume();
  }

  @Override
  public void onItemClick(TabItem item) {
    String filter = constructUriString(item);
    Uri uri = Uri.parse(filter);
    Intent intent = new Intent();
    intent.setData(uri);

    mNavigator.fillPage(mNavigator.getFragment(this, intent), R.id.content_view_container);
  }

  private String constructUriString(TabItem tab) {
    String uriString = "OneFramework://" + tab.tabBiz + "/entrance";
    return uriString;
  }

  @Override
  protected void onPause() {
    super.onPause();
    mDelegateManager.notifyOnPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mDelegateManager.notifyOnStop();
  }

  public void addNavigator() {
    mNavigator = new Navigator(getSupportFragmentManager());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mDelegateManager.notifyOnDestroy();
  }

  /////////////////////// test //////
  private List<TabItem> testTabItems() {
    List<TabItem> items = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      TabItem tab1 = new TabItem();
      if (i == 0) {
        tab1.tab = "站点巴士";
        tab1.position = i;
        tab1.tabBiz = "calendar";
        tab1.isRedPoint = false;
        tab1.isSelected = i == 0 ? true : false;
      } else {
        tab1.tab = "快车" + i;
        tab1.position = i;
        tab1.tabBiz = "flash";
        tab1.isRedPoint = false;
        tab1.isSelected = i == 0 ? true : false;
      }
      items.add(tab1);
    }
    return items;
  }
}
