#############################################
# ✅ General Keep Rules
#############################################

# Keep all ViewBinding/DataBinding classes
-keep class **.*Binding { *; }

# Keep your app classes
-keep class com.dev.ron.** { *; }

#############################################
# 🐍 Chaquopy / Python Rules
#############################################

# Keep all Chaquopy bridge classes
-keep class com.chaquo.** { *; }

# Keep native methods (used by Python JNI bridge too)
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve source for stack trace (optional but helpful)
-keepattributes SourceFile,LineNumberTable

#############################################
# 🌐 OkHttp & DNS-over-HTTPS Rules
#############################################

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn kotlin.Unit
-dontwarn kotlin.coroutines.**

-keepclassmembers class okhttp3.internal.publicsuffix.PublicSuffixDatabase {
    java.lang.String[] *;
}

#############################################
# ⚙️ Native / JNI + CMake Rules
#############################################

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# If you have specific native bridge classes
-keep class com.dev.ron.SekdJdhdJd {
    native <methods>;
}

#############################################
# 🔧 Misc Safe Defaults
#############################################

# Keep important class metadata
-keepattributes Signature, InnerClasses, EnclosingMethod

# Keep annotations
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault

# Silence safe libraries
-dontwarn androidx.**
-dontwarn org.jetbrains.annotations.**

# Generate mapping file
-printmapping build/proguard-mapping.txt