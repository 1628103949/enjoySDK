# Add project specific ProGuard rules here.
  # By default, the flags in this file are appended to flags specified
  # in D:\ADT\sdk/tools/proguard/proguard-android.txt
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

 -ignorewarnings

# enjoy sdk framework proguard config

# enjoy framework

# support library
-dontwarn android.**
-keep class android.**{*;}



# common
-keepattributes Signature,*Annotation*
-keep public class com.enjoy.sdk.framework.** {
    public protected *;
}
-keep public interface com.enjoy.sdk.framework.** {
    public protected *;
}
-keepclassmembers class * extends com.enjoy.sdk.framework.** {
    public protected *;
}

# webview
-keepattributes *JavascriptInterface*

# xutils
-keepclassmembers @com.enjoy.sdk.framework.xutils.db.annotation.* class * {*;}
-keepclassmembers @com.enjoy.sdk.framework.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @com.enjoy.sdk.framework.xutils.view.annotation.Event <methods>;
}











