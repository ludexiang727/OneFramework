package com.one.trip.utils

import android.animation.StateListAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatButton
import android.view.View


fun putItCenteredDrawable(d: Drawable, iw: Int, ih: Int): Drawable {
  return FixedSizeCenteredDrawable(d, iw, ih)
}

fun scaleDrawable(d: Drawable, gravity: Int, scaleWidth: Float, scaleHeight: Float): Drawable {
  return ScaleDrawable(d, gravity, scaleWidth, scaleHeight)
}

fun layerDrawable(vararg d: Drawable): LayerDrawable {
  return LayerDrawable(d)
}


/**
 * unlike Android api, the rounded corners accepts only 4 value
 */
fun colorDrawableRounded(color: Int, rc: IntArray): Drawable {
  val radii = floatArrayOf(
      rc[0].toFloat(), rc[0].toFloat(),
      rc[1].toFloat(), rc[1].toFloat(),
      rc[2].toFloat(), rc[2].toFloat(),
      rc[3].toFloat(), rc[3].toFloat()
      )
  val shape = RoundRectShape(radii, null, null)
  val drawable = ShapeDrawable(shape)
  drawable.paint.color = color
  return drawable
}

fun colorDrawableRounded(color: Int, roundedCorner: Int): Drawable {
  val r = roundedCorner.toFloat()
  val radii = floatArrayOf(r, r, r, r, r, r, r, r)
  val shape = RoundRectShape(radii, null, null)
  val drawable = ShapeDrawable(shape)
  drawable.paint.color = color
  return drawable
}

fun rippleDrawableRect(color: Int?, maskColor: Int): Drawable {
  val rectDrawable = if (color == null) null else ColorDrawable(color)
  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    val maskDrawable = ColorDrawable(Color.WHITE)
    return RippleDrawable(
        ColorStateList(arrayOf(intArrayOf()),  intArrayOf(maskColor)),
        rectDrawable, maskDrawable)
  } else {
    val maskDrawable = StateListDrawable()
    maskDrawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(maskColor))
    val layerDrawable: Drawable = if (rectDrawable == null) maskDrawable else layerDrawable(rectDrawable, maskDrawable)
    return layerDrawable
  }
}

/**
 * unlike Android api, the rounded corners accepts only 4 value
 */
fun rippleDrawableRounded(color: Int?, maskColor: Int, roundedCorners: IntArray): Drawable {
  val rectDrawable = if (color == null) null else colorDrawableRounded(color, roundedCorners)
  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    val maskDrawable = colorDrawableRounded(Color.WHITE, roundedCorners)
    return RippleDrawable(
        ColorStateList(arrayOf(intArrayOf()),  intArrayOf(maskColor)),
        rectDrawable, maskDrawable)
  } else {
    val maskDrawable = StateListDrawable()
    maskDrawable.addState(intArrayOf(android.R.attr.state_pressed), colorDrawableRounded(maskColor, roundedCorners))
    val layerDrawable: Drawable = if (rectDrawable == null) maskDrawable else layerDrawable(rectDrawable, maskDrawable)
    return layerDrawable
  }
}

fun Drawable.colorDrawableColor() = (this as ColorDrawable).color

fun rippleDrawableRounded(color: Int?, maskColor: Int, roundedCorners: Int): Drawable {
  val rectDrawable = if (color == null) null else colorDrawableRounded(color, roundedCorners)
  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    val maskDrawable = colorDrawableRounded(Color.WHITE, roundedCorners)
    return RippleDrawable(
        ColorStateList(arrayOf(intArrayOf()),  intArrayOf(maskColor)),
        rectDrawable, maskDrawable)
  } else {
    val maskDrawable = StateListDrawable()
    maskDrawable.addState(intArrayOf(android.R.attr.state_pressed), colorDrawableRounded(maskColor, roundedCorners))
    val layerDrawable: Drawable = if (rectDrawable == null) maskDrawable else layerDrawable(rectDrawable, maskDrawable)
    return layerDrawable
  }
}

fun wrapToDisabledAlphaDrawable(drawable: Drawable, alpha: Float = 0.3F): StateListDrawable {
  val list = StateListDrawable()
  val alphaDrawable = drawable.constantState.newDrawable().mutate()
  alphaDrawable.alpha = (alpha * 255).toInt()
  list.addState(intArrayOf(android.R.attr.state_enabled), drawable)
  list.addState(intArrayOf(), alphaDrawable)
  return list
}

private var _defaultButtonStateListAnimator: StateListAnimator? = null


fun View.setDefaultButtonStateListAnimator() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    stateListAnimator = defaultButtonStateListAnimator(context as Activity);
  }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun defaultButtonStateListAnimator(activity: Activity): StateListAnimator {
  if (_defaultButtonStateListAnimator == null) {
    _defaultButtonStateListAnimator = AppCompatButton(activity).stateListAnimator
  }
  return _defaultButtonStateListAnimator!!.clone()
}
