# app/proguard-rules.pro

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

# Сохранить конструкторы без аргументов (нужно для Gson десериализации)
-keepclassmembers class * {
    public <init>();
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
-keepclassmembers class com.example.russianpath.data.local.entity.** {
    <fields>;
    public <init>(...);
}

# ========================================================================
# Domain модели
# ========================================================================
-keep class com.example.russianpath.domain.model.** { *; }

# ========================================================================
# Seed классы (DatabaseSeeder, SeedLoader, ManifestLoader, ContentVersionManager)
# ========================================================================
-keep class com.example.russianpath.data.seed.** { *; }
-keep class com.example.russianpath.data.seed.model.** { *; }

# ========================================================================
# Kotlin Data Classes (компоненты для деструктуризации)
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

# ========================================================================
# Сохранить имена полей для Gson (LOWER_CASE_WITH_UNDERSCORES)
# ========================================================================
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========================================================================
# Сохранить все классы с аннотацией @Entity
# ========================================================================
-keep @androidx.room.Entity class * {
    <init>(...);
    <fields>;
}

# ========================================================================
# Сохранить репозитории
# ========================================================================
-keep class com.example.russianpath.data.repository.** { *; }

# ========================================================================
# Сохранить ViewModel
# ========================================================================
-keep class com.example.russianpath.presentation.screens.** { *; }

# ========================================================================
# Удалить логирование в релизе
# ========================================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
