// app/src/main/java/com/example/russianpath/data/local/AppDatabase.kt

package com.example.russianpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.russianpath.data.local.converter.SkillCodeConverter
import com.example.russianpath.data.local.converter.StringListConverter
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.RuleDao
import com.example.russianpath.data.local.dao.SectionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import com.example.russianpath.data.local.entity.GradeEntity
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.local.entity.RuleEntity
import com.example.russianpath.data.local.entity.SectionEntity
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.data.local.entity.UserProgressEntity

/**
 * Основная база данных приложения "Русский Путь".
 *
 * Архитектура сущностей (иерархия контента):
 * ```
 * GradeEntity (классы: 1-11, ОГЭ, ЕГЭ)
 *   └─ SectionEntity (разделы: Фонетика, Морфология...)
 *        └─ TopicEntity (темы: Правописание приставок...)
 *             ├─ LearningObjectiveEntity (цели обучения)
 *             │    └─ MicroSkillEntity (микро-навыки)
 *             └─ RuleEntity (правила орфографии/грамматики)
 *
 * DictionaryWordEntity — независимый словарь
 * LessonEntity → QuestionEntity — контент уроков
 * LessonCompletionEntity — история всех попыток
 * UserProgressEntity — агрегированный прогресс (синглтон)
 * ```
 *
 * Версия БД: 2
 * Миграция 1→2: AppDatabaseMigrations.MIGRATION_1_2
 */
@Database(
    entities = [
        GradeEntity::class,
        SectionEntity::class,
        TopicEntity::class,
        LearningObjectiveEntity::class,
        MicroSkillEntity::class,
        RuleEntity::class,
        DictionaryWordEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        UserProgressEntity::class,
        LessonCompletionEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    SkillCodeConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gradeDao(): GradeDao
    abstract fun sectionDao(): SectionDao
    abstract fun topicDao(): TopicDao
    abstract fun learningObjectiveDao(): LearningObjectiveDao
    abstract fun microSkillDao(): MicroSkillDao
    abstract fun ruleDao(): RuleDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonCompletionDao(): LessonCompletionDao

    companion object {
        const val DATABASE_NAME = "russian_path.db"
    }
}
