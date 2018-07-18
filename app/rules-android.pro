-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontoptimize
-dontshrink
-allowaccessmodification
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes JavascriptInterface
-keepattributes LineNumberTable
-keepattributes Signature
-keepattributes SourceFile

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.google.**{*;}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    void set*(***);
    *** get*();
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class **.R$* {
    public static <fields>;
}

-keep @android.support.annotation.Keep class *
-keep @android.support.annotation.Keep interface *

-keepclassmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclassmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclassmembers interface * {
    @android.support.annotation.Keep <methods>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String,int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Android Support
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# Android
-dontwarn com.android.**
-dontwarn android.app.**
-keep class com.android.** { *; }
-keep interface com.android.** { *; }

# native
-keepclasseswithmembernames class * {
    native <methods>;
}
