package com.trip.taxi;

import com.one.map.map.MarkerOption;
import com.one.map.model.Address;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/7.
 */

public interface ITaxiView {
  void moveMapToStartAddress(Address address);
  void addMarks(List<MarkerOption> options);
  void showFullForm();
}
