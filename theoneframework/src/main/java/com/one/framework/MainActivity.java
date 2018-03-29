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

    mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.one_map_fragment);
    mTopbarFragment = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.one_top_bar_fragment);
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
    TabItem tab1 = new TabItem();
    tab1.tab = "快车";
    tab1.position = 0;
    tab1.tabBiz = "flash";
    tab1.isRedPoint = false;
    tab1.isSelected = true;
    TabItem tab2 = new TabItem();
    tab2.tab = "专车";
    tab2.position = 1;
    tab2.tabBiz = "premium";
    tab2.isRedPoint = true;
    tab2.isSelected = false;

    TabItem tab3 = new TabItem();
    tab3.tab = "出租车";
    tab3.position = 2;
    tab3.tabBiz = "taxi";
    tab3.isRedPoint = false;
    tab3.isSelected = false;
    items.add(tab1);
    items.add(tab2);
    items.add(tab3);
    return items;
  }
}
