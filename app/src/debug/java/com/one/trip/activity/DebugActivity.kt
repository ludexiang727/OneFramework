package com.one.trip.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.one.framework.app.login.UserProfile
import com.one.framework.utils.PreferenceUtil
import com.one.map.model.Address
import com.one.map.model.LatLng
import com.one.trip.R
import com.one.trip.SplashActivity

class DebugActivity : Activity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)


        findViewById<Button>(R.id.debug_webiew_button).setOnClickListener {
            var url = findViewById<EditText>(R.id.debug_webiew_edit).text.toString()
//            Mocha.getInstance().startWebView(this@DebugActivity, url)
        }

//        findViewById<Button>(R.id.debug_sign_button).setOnClickListener {
//            val intent = Intent(this, SignDebugActivity::class.java)
//            startActivity(intent)
//        }

        findViewById<EditText>(R.id.debug_env_edit).setText(PreferenceUtil.instance(this).getString("debug_env"))
        findViewById<Button>(R.id.debug_env_save).setOnClickListener {
            var env = findViewById<EditText>(R.id.debug_env_edit).text.toString()
            PreferenceUtil.instance(this@DebugActivity).putString("debug_env", env)
            UserProfile.getInstance(this).logout()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Runtime.getRuntime().exit(0)
        }

        findViewById<Button>(R.id.test_nav_amap).setOnClickListener {
            var to = Address()
            var from = Address()

            to.mAdrLatLng = LatLng(39.9928900000, 116.3376600000)
            to.mAdrFullName = "五道口"

            from.mAdrLatLng = LatLng(39.9838900000, 116.3164900000)
            from.mAdrFullName = "中关村"
//            var nav = AMapNav(this@DebugActivity)
//            nav.doNav(from, to)
        }

        findViewById<Button>(R.id.test_nav_qq).setOnClickListener {

            var intent = Intent()
            //系统默认的action，用来打开默认的短信界面
            intent.setAction(Intent.ACTION_SENDTO);
            //需要发短息的号码,电话号码之间用“;”隔开
            intent.setData(Uri.parse("smsto:" + 10086 + ";" + 10002 + ";" + 10003));
            intent.putExtra("sms_body", "别紧张，这仅仅是是一个测试！OY");
            startActivity(intent);

//            GlobalUtils.toast(this, System.currentTimeMillis().toString());
//
//            var to = Address()
//            var from = Address()
//
//            to.mAdrLatLng = LatLng(39.9928900000, 116.3376600000)
//            to.mAdrFullName = "五道口"
//
//            from.mAdrLatLng = LatLng(39.9838900000, 116.3164900000)
//            from.mAdrFullName = "中关村"
//            var nav = QQNav(this@DebugActivity)
//            nav.doNav(from, to)
        }

        findViewById<Button>(R.id.test_nav_baidu).setOnClickListener {
            var to = Address()
            var from = Address()

            to.mAdrLatLng = LatLng(39.9928900000, 116.3376600000)
            to.mAdrFullName = "五道口"

            from.mAdrLatLng = LatLng(39.9838900000, 116.3164900000)
            from.mAdrFullName = "中关村"
//            var nav = BaiduNav(this@DebugActivity)
//            nav.doNav(from, to)
        }

    }


}
