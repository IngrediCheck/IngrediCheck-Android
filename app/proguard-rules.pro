# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.android.ingredicheck.ResponceModelClass.** { *; }

-keepnames class kotlin.** { *; }
-keepnames class io.** { *; }
-keepnames class kotlinx.** { *; }
-keepnames class korlibs.** { *; }
-keepnames class okhttp3.** { *; }
-keepnames class dev.** { *; }
-keepnames class javax.** { *; }
-keepnames class ir.** { *; }
-keepnames class androidx.** { *; }
-keepnames class android.** { *; }
-keepnames class java.** { *; }
-keepnames class kotlin.** { *; }
-keepnames class kotlinx.** { *; }
-keepnames class org.** { *; }
-keepnames class com.google.mlkit** { *; }