# This file contains ProGuard rules for your application.

# --- Keep Attributes for Reflection --- #
# These attributes are essential for any library that uses reflection, including Retrofit and Gson.
-keepattributes Signature,InnerClasses,EnclosingMethod

# --- Keep Application Code --- #
# To ensure no part of your app's logic is incorrectly stripped, we will keep all of it.
# This is a broad rule, but it is the safest approach to solve this specific crash.
-keep class com.waldy.androidcurrencyexchange.** { *; }
-keep interface com.waldy.androidcurrencyexchange.** { *; }

# --- Kotlin & Coroutines --- #
# Keep Kotlin metadata, which is vital for reflection.
-keep class kotlin.Metadata { *; }
# Keep coroutines-related classes that are often accessed via reflection.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepclassmembers class kotlinx.coroutines.android.MainCoroutineDispatcher { public <init>(...); }

# --- Gson Serialization --- #
# Keep classes needed by Gson for serialization and deserialization.
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Retrofit & OkHttp Networking --- #
# Keep Retrofit, OkHttp, and Okio classes from being stripped.
-keep class retrofit2.** { *; }
-keepclassmembers interface retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
