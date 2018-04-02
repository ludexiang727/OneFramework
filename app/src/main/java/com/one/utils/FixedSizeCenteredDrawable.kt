package com.one.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.support.annotation.IntRange

/**
 *
 * put it centered! and no clipping what so ever!
 *
 * this is a very badly named class, when needed, make it general and give it a proper name
 */
class FixedSizeCenteredDrawable(val base: Drawable, val iw: Int, val ih: Int) : Drawable() {

  override fun draw(canvas: Canvas) {
    val rw = base.intrinsicWidth
    val rh = base.intrinsicHeight
    val left = -(rw - iw) / 2
    val top = -(rh - ih) / 2
    canvas.save()
    canvas.translate(left.toFloat(), top.toFloat())
    canvas.clipRect(Rect(0, 0, rw, rh), Region.Op.REPLACE)
    base.setBounds(0, 0, base.intrinsicWidth, base.intrinsicHeight)
    base.draw(canvas)
    canvas.restore()
  }

  override fun setAlpha(@IntRange(from = 0, to = 255) i: Int)  { base.alpha = i }
  override fun setColorFilter(colorFilter: ColorFilter?) { base.colorFilter = colorFilter }
  override fun getOpacity(): Int = base.opacity
  override fun getIntrinsicWidth(): Int = iw
  override fun getIntrinsicHeight(): Int = ih
}
