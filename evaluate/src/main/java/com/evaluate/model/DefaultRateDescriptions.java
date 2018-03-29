package com.evaluate.model;

import android.support.annotation.StringRes;
import com.evaluate.R;
import java.util.ArrayList;
import java.util.List;

public class DefaultRateDescriptions {

  public static final List<RateDescription> LIST = new ArrayList<>(5);

  static {
    LIST.add(new RateDescriptionItem(1, R.string.oc_evaluate_star_description_1));
    LIST.add(new RateDescriptionItem(2, R.string.oc_evaluate_star_description_2));
    LIST.add(new RateDescriptionItem(3, R.string.oc_evaluate_star_description_3));
    LIST.add(new RateDescriptionItem(4, R.string.oc_evaluate_star_description_4));
    LIST.add(new RateDescriptionItem(5, R.string.oc_evaluate_star_description_5));
  }

  public static RateDescription getRateDescription(int star) {
    if (star < 0 || star > LIST.size()) {
      return null;
    }
    for (RateDescription rateDescription : LIST) {
      if (rateDescription.getRate() == star) {
        return rateDescription;
      }
    }
    return null;
  }

  public static List<RateDescription> getRateDescriptionList() {
    return LIST;
  }

  public static class RateDescriptionItem implements RateDescription {

    private int rate;
    private int res;

    public RateDescriptionItem(int rate, int res) {
      this.rate = rate;
      this.res = res;
    }

    @Override
    public int getRate() {
      return rate;
    }

    @Override
    public String getText() {
      return null;
    }

    @Override
    @StringRes
    public int getTextRes() {
      return res;
    }
  }
}
