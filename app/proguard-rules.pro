# ========================================================================
# Gson
# ========================================================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Сохранить все поля с аннотацией @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========================================================================
# Room Database
# ========================================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.paging.**

# ========================================================================
# Entity классы (НЕ ОБФУСЦИРОВАТЬ!)
# ========================================================================
-keep class com.example.russianpath.data.local.entity.** { *; }
-keep class com.example.russianpath.domain.model.** { *; }

# ========================================================================
# Kotlin Data Classes
# ========================================================================
-keepclassmembers class * {
    public <init>(...);
    public *** component1();
    public *** component2();
    public *** component3();
    public *** component4();
    public *** component5();
    public *** component6();
    public *** component7();
    public *** component8();
    public *** component9();
    public *** component10();
    public *** component11();
    public *** component12();
    public *** component13();
    public *** component14();
    public *** component15();
    public *** component16();
    public *** component17();
    public *** component18();
    public *** component19();
    public *** component20();
    public *** copy(...);
    public *** toString();
}

# ========================================================================
# Coroutines
# ========================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ========================================================================
# Hilt / Dagger
# ========================================================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.hilt.**

# ========================================================================
# SQLite
# ========================================================================
-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }

# ========================================================================
# AndroidX
# ========================================================================
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
