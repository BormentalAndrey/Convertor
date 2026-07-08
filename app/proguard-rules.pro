# Сохранить все классы, используемые Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Сохранить все классы Entity (Room)
-keep class com.example.russianpath.data.local.entity.** { *; }

# Сохранить все классы Domain
-keep class com.example.russianpath.domain.model.** { *; }

# Сохранить все классы Seed
-keep class com.example.russianpath.data.seed.model.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Не обфусцировать Kotlin data classes
-keepclassmembers class * {
    public <init>(...);
    public *** component1();
    public *** component2();
    public *** component3();
    public *** copy(...);
    public *** toString();
}
