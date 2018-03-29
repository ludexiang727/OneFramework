package com.evaluate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbsRecyclerAdapter<T extends AbsViewBinder, V> extends
    RecyclerView.Adapter<T> {

  private List<V> mList;
  private LayoutInflater mInflater;

  public AbsRecyclerAdapter(Context context) {
    if (context == null) {
      return;
    }
    mList = new ArrayList<>();
    mInflater = LayoutInflater.from(context);
  }

  @Override
  public final T onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = createView(mInflater, parent, viewType);
    return createViewHolder(view);
  }

  protected abstract T createViewHolder(View view);

  /**
   * 如果是通过LayoutInflater创建的View,不要绑定到父View,RecyclerView会负责添加。
   */
  protected abstract View createView(LayoutInflater inflater, ViewGroup parent, int viewType);

  @Override
  public final void onBindViewHolder(T holder, int position) {
    V data = mList.get(position);
    holder.setData(data);
    holder.bind(data);
  }

  @Override
  public int getItemCount() {
    return mList.size();
  }

  /**
   * 列表末尾追加一个元素
   */
  public final void append(V item) {
    if (item == null) {
      return;
    }
    mList.add(item);
    notifyDataSetChanged();
  }

  /**
   * 在特定位置增加一个元素
   */
  public void append(V item, int position) {
    if (item == null) {
      return;
    }
    if (position < 0) {
      position = 0;
    } else if (position > mList.size()) {
      position = mList.size();
    }
    mList.add(position, item);
    notifyDataSetChanged();
  }

  /**
   * 追加一个集合
   */
  public final void append(Collection<V> items) {
    if (items == null || items.size() == 0) {
      return;
    }
    mList.addAll(items);
    notifyDataSetChanged();
  }

  /**
   * 清空集合
   */
  public final void clear() {
    if (mList.isEmpty()) {
      return;
    }
    mList.clear();
    notifyDataSetChanged();
  }

  /**
   * 删除一个元素
   */
  public final void remove(V item) {
    if (item == null) {
      return;
    }
    if (mList.contains(item)) {
      mList.remove(item);
      notifyDataSetChanged();
    }
  }

  /**
   * 删除一个集合
   */
  public final void remove(Collection<V> items) {
    if (items == null || items.size() == 0) {
      return;
    }
    if (mList.removeAll(items)) {
      notifyDataSetChanged();
    }
  }

  /**
   * 替换数据集合
   */
  public void update(Collection<V> items) {
    if (items == null || items.size() == 0) {
      return;
    }
    if (mList.size() > 0) {
      mList.clear();
    }
    mList.addAll(items);
    notifyDataSetChanged();
  }

  public List<V> getData() {
    return new ArrayList<>(mList);
  }

  public void refresh() {
    notifyDataSetChanged();
  }
}
