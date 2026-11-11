# TensorFlow Lite & ARCore Rules
-keep class org.tensorflow.lite.** { *; }
-keep class com.google.ar.core.** { *; }
-dontwarn org.tensorflow.lite.**
-dontwarn com.google.ar.core.**