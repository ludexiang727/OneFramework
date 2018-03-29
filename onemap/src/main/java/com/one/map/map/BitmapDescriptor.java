package com.one.map.map;

import android.graphics.Bitmap;

/**
 * Created by mobike on 2017/11/29.
 */

public class BitmapDescriptor {
  public Bitmap getBitmap() {
    return bitmap;
  }
  
  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }
  
  private Bitmap bitmap;
}
