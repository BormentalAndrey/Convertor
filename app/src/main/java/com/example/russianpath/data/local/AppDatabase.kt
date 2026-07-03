package com.example.russianpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.russianpath.data.local.converter.SkillCodeConverter
import com.example.russianpath.data.local.converter.StringListConverter
import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.SectionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.entity.GradeEntity
import com.example.russianpath.data.local.entity.SectionEntity
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.data.local.entity.LessonCompletionEntity

/**
 * Основная база данных приложения "Русский Путь".
 *
 * Архитектура сущностей (иерархия контента):
 * ```
 * GradeEntity (классы: 1-11, ОГЭ, ЕГЭ)
 *   └─ SectionEntity (разделы: Фонетика, Морфология...)
 *        └─ TopicEntity (темы: Правописание приставок...)
 *             └─ LearningObjectiveEntity (цели обучения)
 *                  └─ MicroSkillEntity (микро-навыки)
 *
 * DictionaryWordEntity — независимый словарь
 * LessonEntity → QuestionEntity — контент уроков
 * LessonCompletionEntity — история всех попыток
 * UserProgressEntity — агрегированный прогресс (синглтон)
 * ```
 *
 * Версия БД: 2
 * Миграция 1→2: AppDatabaseMigrations.MIGRATION_1_2
 *   - Добавлены поля синхронизации во все таблицы
 *   - Добавлены поля для образовательной аналитики
 *   - Переименована lesson_completion → lesson_completions
 *   - Переименована topics_v2 → topics
 */
@Database(
    entities = [
        GradeEntity::class,
        SectionEntity::class,
        TopicEntity::class,
        LearningObjectiveEntity::class,
        MicroSkillEntity::class,
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

    /** DAO для классов обучения (1-11, ОГЭ, ЕГЭ). */
    abstract fun gradeDao(): GradeDao

    /** DAO для разделов внутри класса. */
    abstract fun sectionDao(): SectionDao

    /** DAO для тем внутри раздела. */
    abstract fun topicDao(): TopicDao

    /** DAO для целей обучения внутри темы. */
    abstract fun learningObjectiveDao(): LearningObjectiveDao

    /** DAO для микро-навыков. */
    abstract fun microSkillDao(): MicroSkillDao

    /** DAO для словарных слов. */
    abstract fun dictionaryDao(): DictionaryDao

    /** DAO для уроков. */
    abstract fun lessonDao(): LessonDao

    /** DAO для вопросов упражнений. */
    abstract fun questionDao(): QuestionDao

    /** DAO для прогресса пользователя. */
    abstract fun userProgressDao(): UserProgressDao

    /** DAO для истории завершения уроков. */
    abstract fun lessonCompletionDao(): LessonCompletionDao

    companion object {
        /** Имя файла базы данных. */
        const val DATABASE_NAME = "russian_path.db"
    }
}
