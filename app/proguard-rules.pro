
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keep class net.lag129.ferret.model.** { *; }

-assumenosideeffects class io.github.aakira.napier.Napier {
    public *** v(...);
    public *** d(...);
    public *** i(...);
    public *** w(...);
    public *** e(...);
    public *** wtf(...);
}
-dontwarn com.google.crypto.tink.**
-keep class * implements androidx.navigation3.runtime.NavKey { *; }
