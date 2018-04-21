package com.one.adapter.impl;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.one.adapter.impl.ListAdapter.ListHolder;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.log.Logger;
import com.one.model.ListModel;
import com.test.demo.R;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class ListAdapter extends AbsBaseAdapter<ListModel, ListHolder> {


  public ListAdapter(Context context) {
    super(context);
  }

  @Override
  protected ListHolder createHolder() {
    return new ListHolder();
  }

  @Override
  protected void initView(View view, ListHolder holder) {
    holder.name = (TextView) view.findViewById(R.id.list_simple_name);
    holder.buy = (Button) view.findViewById(R.id.list_simple_buy);
    holder.buy.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Logger.e("ldx", "This is test");
      }
    });
  }

  @Override
  protected void bindData(ListModel bean, ListHolder holder, int position) {
    holder.name.setText(bean.title);
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.list_simple_layout, null);
  }

  public class ListHolder {
    TextView name;
    Button buy;
  }
}
