package com.one.trip

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Handler
import android.os.Vibrator
import java.lang.ref.WeakReference

/**
 * Created by amglhit on 2017/10/20.
 */
class ShakeSensorListener(activity: Activity, range: Float, val callback: Callback) : SensorEventListener {
    private val MAX_COUNT = 5
    private val INTERVAL = 200

    private var last: Long = 0
    private var count = 0
    private var limit = 0.0

    private var enabled = true

    private var activityWeakReference: WeakReference<Activity>? = null

    init {
        activityWeakReference = WeakReference(activity)
        limit = Math.cbrt(range.toDouble()) * 8.2
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        val dX = event.values[0]
        val dY = event.values[1]
        val dZ = event.values[2]

        if (Math.sqrt((dX * dX + dY * dY + dZ * dZ).toDouble()) > limit && enabled) {
            val now = System.currentTimeMillis()
            if (now - last <= INTERVAL) {
                count++
            } else {
                count = 1
            }
            last = now

            if (count >= MAX_COUNT && activityWeakReference?.get() != null) {
                count = 0
                val vibrator = activityWeakReference?.get()?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                vibrator?.let {
                    enabled = false
                    it.vibrate(300)
                    Handler().postDelayed(
                            { callback.onShake() }, 300)
                    Handler().postDelayed({ enabled = true }, 1000)
                }
            }
        }
    }

    interface Callback {
        fun onShake()
    }
}