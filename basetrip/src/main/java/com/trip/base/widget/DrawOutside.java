package com.trip.base.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;

/**
 * TODO(later) when you have alpha, the draw outside will not drawn
 */
public class DrawOutside {


  public static Drawable solidTransparentHack = new ColorDrawable(Color.WHITE) {

    @Override
    public void draw(Canvas canvas) {
      // do nothing
    }
  };

  public static void drawOutside(View view, Canvas canvas, NinePatchDrawable shadow, Rect padding,
      int alpha) {
    shadow.setAlpha(alpha);
    shadow.setBounds(0 - padding.left,
        0 - padding.top,
        view.getWidth() + padding.right,
        view.getHeight() + padding.bottom);
    shadow.draw(canvas);
  }


}
