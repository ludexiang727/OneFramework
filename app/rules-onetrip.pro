-optimizationpasses 5
# shrink 压缩
-dontshrink # 不压缩
# Optimize 优化 分析和优化字节码
-dontoptimize # 不优化
# Preveirfy java平台上对处理后的代码进行预检
-dontpreverify # 不做预检
-dontskipnonpubliclibraryclasses
-verbose
#google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keep class com.one.framework.app.login.VerificationCodeView$OnCodeFinishListener { *; }

-keep class com.one.framework.app.widget.base.IListItemView$IClickCallback { *; }

-keep class com.one.framework.app.widget.base.IItemClickListener { *; }

-keep class com.one.framework.app.widget.base.IMovePublishListener { *; }

-keep class com.one.framework.app.widget.base.IPullView { *; }

-keepnames class com.one.map.map.CircleOption

-keepnames class com.one.map.model.MapStatusOperation$**

-keep class com.one.map.location.LocationProvider {
  *;
}
