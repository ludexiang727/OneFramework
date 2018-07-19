package com.one.trip

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.TextUtils
import com.one.framework.app.base.OneApplication
import com.one.framework.net.Api
import com.one.framework.utils.PreferenceUtil
import com.one.framework.utils.ToastUtils
import com.one.trip.activity.DebugActivity
import com.trip.taxi.utils.H5Page

/**
 * Created by amglhit on 2017/10/20.
 */
class DebugApplication : OneApplication() {

//    override fun initInMainProcess() {
//        initializeStetho(this)
//        initDebugMode()
//        super.initInMainProcess()
//    }

    override fun onCreate() {
        super.onCreate()
        ToastUtils.toast(this, "欢迎使出租车乘客端！！\n摇一摇可切环境")
        initDebugMode()
        initNetworkManager()
    }

    fun initNetworkManager() {
        var env = PreferenceUtil.instance(this).getString("debug_env")
        if (TextUtils.isEmpty(env)) {

        } else {
//            taxi must be fix mo/

            val apiUrl: String = "https://" + env.trim().toLowerCase() + "-app.mobike.com"
            H5Page.initEnv(env)
            Api.apiUrl(apiUrl)
        }

    }

    private fun initDebugMode() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            var listener: SensorEventListener? = null
            override fun onActivityPaused(activity: Activity?) {
                if (activity == null || activity is DebugActivity || listener == null) {
                    return
                }
                val sensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
                sensorManager.unregisterListener(listener)
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity == null || activity is DebugActivity)
                    return
                val sensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
                val accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                accelerator?.let {
                    listener = ShakeSensorListener(activity, accelerator.maximumRange / 2, shakeCallback)
                    sensorManager.registerListener(listener, accelerator, SensorManager.SENSOR_DELAY_UI)
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, p1: Bundle?) {
            }
        })
    }

    private val shakeCallback = object : ShakeSensorListener.Callback {
        override fun onShake() {
            var intent = Intent(this@DebugApplication, DebugActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }


    private fun initializeStetho(context: Context) {
//        Stetho.initializeWithDefaults(context)
    }
}