# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/stephen/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

###################################################
-dontwarn org.**
-keep class org.** { *; }
-keep interface org.** { *; }

# Sun Misc
-dontwarn sun.misc.**
-keep class sun.misc.** { *; }
-keep interface sun.misc.** { *; }

-dontwarn com.alibaba.**
-keep class com.alibaba.** { *; }
-keep interface com.alibaba.** { *; }

-dontwarn com.alipay.**
-keep class com.alipay.** { *; }
-keep interface com.alipay.** { *; }

-dontwarn com.tencent.**
-keep class com.tencent.** { *; }
-keep interface com.tencent.** { *; }

-dontwarn com.qq.**
-keep class com.qq.** { *; }
-keep interface com.qq.** { *; }

-dontwarn com.sina.**
-keep class com.sina.** { *; }
-keep interface com.sina.** { *; }

-dontwarn com.xiaomi.**
-keep class com.xiaomi.** { *; }
-keep interface com.xiaomi.** { *; }

-dontwarn com.tendcloud.**
-keep class com.tendcloud.** { *; }
-keep interface com.tendcloud.** { *; }

-dontwarn cn.sharesdk.**
-keep class cn.sharesdk.** { *; }
-keep interface cn.sharesdk.** { *; }

-dontwarn navsns.**
-keep class navsns.** { *; }
-keep interface navsns.** { *; }

-dontwarn ct.**
-keep class ct.** { *; }
-keep interface ct.** { *; }

-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep interface com.igexin.** { *; }

-dontwarn com.iflytek.**
-keep class com.iflytek.** { *; }
-keep interface com.iflytek.** { *; }

-dontwarn com.bumptech.**
-keep class com.bumptech.** { *; }
-keep class * implements com.bumptech.glide.module.GlideModule
-keep interface com.bumptech.** { *; }

-dontwarn com.lidroid.**
-keep class com.lidroid.** { *; }
-keep interface com.lidroid.** { *; }

-dontwarn com.third.**
-keep class com.third.** { *; }
-keep interface com.third.** { *; }

-dontwarn com.mob.**
-keep class com.mob.** { *; }
-keep interface com.mob.** { *; }

-dontwarn com.tunasashimi.**
-keep class com.tunasashimi.** { *; }
-keep interface com.tunasashimi.** { *; }

-dontwarn com.ut.**
-keep class com.ut.** { *; }
-keep interface com.ut.** { *; }

-dontwarn org.greenrobot.**
-keep class org.greenrobot.** { *; }
-keep interface org.greenrobot.** { *; }

-dontwarn pl.droidsonroids.**
-keep class pl.droidsonroids.** { *; }
-keep interface pl.droidsonroids.** { *; }

-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }

-dontwarn com.ta.**
-keep class com.ta.** { *; }
-keep interface com.ta.** { *; }

-dontwarn com.turbomanage.**
-keep class com.turbomanage.** { *; }
-keep interface com.turbomanage.** { *; }

-dontwarn rttradio.**
-keep class rttradio.** { *; }
-keep interface rttradio.** { *; }

-dontwarn in.srain.**
-keep class in.srain.** { *; }
-keep interface in.srain.** { *; }

-keep class **$Properties

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

-dontwarn okio.**
-keep class okio.** { *; }
-keep interface okio.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

######################### 招行键盘 ###############################
#-keepclasseswithmembers class cmb.pb.util.CMBKeyboardFunc {
#  public <init>(android.app.Activity);
#  public boolean HandleUrlCall(android.webkit.WebView, java.lang.String);
#  public void callKeyBoardActivity();
#}

######################## 百度地图 ###############################
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

###################### 腾讯地图 ###########################
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}


-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

######################### 高德地图 #########################
-dontwarn com.amap.api.maps.**
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}
-keep class com.amap.api.mapcore.** {*;}

-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep class com.amap.api.services.**{*;}
