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

# enjoy sdk proguard config
#--------------- start -------------

# enjoy sdk
-keepclassmembers class com.enjoy.sdk.core.own.fw.TNUserActivity$WebInterface{
   public *;
}
-keepclassmembers class com.enjoy.sdk.core.sdk.common.NoticeDialog$WebInterface{
   public *;
}
-keepattributes *JavascriptInterface*

-keep class com.enjoy.sdk.core.api.EnjoyConstants{*;}
-keep class com.enjoy.sdk.core.api.EnjoyConstants$**{*;}

-keep class com.enjoy.sdk.core.api.EnjoyApplication{*;}
-keep class com.enjoy.sdk.core.api.EnjoySdkApi{*;}
-keep class com.enjoy.sdk.core.api.EnjoySdkApp{*;}
-keep class com.enjoy.sdk.core.api.**{*;}
-keep class com.enjoy.sdk.core.sdk.ads.**{*;}
-keep class com.enjoy.sdk.core.sdk.SDKData{*;}
-keep class com.enjoy.sdk.core.sdk.config.**{*;}
-keep class com.enjoy.sdk.core.platform.**{*;}
-keep class com.enjoy.sdk.core.sdk.SDKApplication{*;}
-keep class com.enjoy.sdk.core.http.params.MPayQueryParam{*;}
-keep class com.enjoy.sdk.core.http.params.ReferParam{*;}
-keep class com.enjoy.sdk.core.http.EnjoyResponse{*;}
-keepclassmembers @com.enjoy.sdk.framework.xutils.db.annotation.* class * {*;}
-keepclassmembers @com.enjoy.sdk.framework.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @com.enjoy.sdk.framework.xutils.view.annotation.Event <methods>;
}

-keep class com.bun.miitmdid.core.** {*;}

#--------------- End ---------------











