package com.evaluate.loading;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

interface PBDelegate {
  void draw(Canvas canvas, Paint paint);

  void drawSuccess(Canvas canvas, Paint paint, Bitmap successIcon);

  void start();

  void stop();

  void progressiveStop(CircularProgressDrawable.OnEndListener listener);

  public void changeToSuccess(Bitmap successIcon);

  public void changeToLoading();
}
