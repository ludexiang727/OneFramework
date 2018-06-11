package com.trip.base.adapter;

import static com.one.framework.db.DBTables.AddressTable.END;
import static com.one.framework.db.DBTables.AddressTable.SEARCH_HISTORY;
import static com.one.framework.db.DBTables.AddressTable.START;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.map.model.Address;
import com.trip.base.R;
import com.trip.base.adapter.AddressAdapter.AddressHolder;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class AddressAdapter extends AbsBaseAdapter<Address, AddressHolder> {

  private int iconType;

  public AddressAdapter(Context context) {
    super(context);
  }

  @Override
  protected AddressHolder createHolder() {
    return new AddressHolder();
  }

  public void setType(int type) {
    iconType = type;
  }

  @Override
  protected void initView(View view, AddressHolder holder) {
    holder.addressIcon = (ImageView) view.findViewById(R.id.address_item_icon);
    holder.displayName = (TextView) view.findViewById(R.id.address_item_display_name);
    holder.detailAddress = (TextView) view.findViewById(R.id.address_item_adr_detail);
    holder.recommend = (TextView) view.findViewById(R.id.address_recommend_info);
  }

  @Override
  protected void bindData(Address model, AddressHolder holder, int position) {
    if (model.type != -1) {
      holder.addressIcon.setImageResource(R.drawable.base_address_search_item_result);
      if (model.type == SEARCH_HISTORY) {
        holder.recommend.setVisibility(View.VISIBLE);
        holder.recommend.setText(R.string.address_search_history);
      } else {
        holder.recommend.setVisibility(View.GONE);
      }
    } else {
      if (iconType == START) {
        holder.addressIcon.setImageResource(R.drawable.base_address_item_origin);
      } else if (iconType == END) {
        holder.addressIcon.setImageResource(R.drawable.base_address_item_terminal);
      }
      if (model.distance != -1 && model.distance < 30) { // tencent 返回distance应该是米
        holder.recommend.setVisibility(View.VISIBLE);
      } else {
        holder.recommend.setVisibility(View.GONE);
      }
      holder.recommend.setText(R.string.address_recommend);
    }

    holder.displayName.setText(model.mAdrDisplayName);
    holder.detailAddress.setText(model.mAdrFullName);
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.address_sub_item_layout, null);
  }

  final class AddressHolder {
    ImageView addressIcon;
    TextView displayName;
    TextView detailAddress;
    TextView recommend;
  }
}
