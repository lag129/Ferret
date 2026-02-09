
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keep class net.lag129.ferret.api.entity.** { *; }
-keep class net.lag129.ferret.compose.StatusCardData { *; }

-keep class io.ktor.client.engine.** { *; }
-keep class io.ktor.client.engine.cio.** { *; }

-keep class io.ktor.client.HttpClientEngineContainer { *; }
-keep class io.ktor.client.engine.cio.CIOEngineContainer { *; }
-keep class * implements io.ktor.client.HttpClientEngineContainer { *; }
-keepnames class io.ktor.** implements java.io.Serializable

-keep class org.koin.core.** { *; }
-keep class org.koin.androidx.viewmodel.** { *; }

-assumenosideeffects class io.github.aakira.napier.Napier { *; }
-dontwarn com.google.crypto.tink.**
-keep class com.cybozu.datastore.crypto.** { *; }
-keep class com.mohamedrejeb.ksoup.** { *; }
-keep class * implements androidx.navigation3.runtime.NavKey { *; }
