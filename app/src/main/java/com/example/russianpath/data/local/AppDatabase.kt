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
 * Архитектура:
 * - Grades → Sections → Topics → LearningObjectives → MicroSkills
 * - DictionaryWords — независимая таблица словаря
 * - Lessons → Questions — контент уроков
 * - LessonCompletions — история прохождений
 * - UserProgress — агрегированный прогресс пользователя
 *
 * Версия БД: 2 (миграция с version 1: добавлены новые поля, переименована lesson_completion → lesson_completions)
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

    abstract fun gradeDao(): GradeDao
    abstract fun sectionDao(): SectionDao
    abstract fun topicDao(): TopicDao
    abstract fun learningObjectiveDao(): LearningObjectiveDao
    abstract fun microSkillDao(): MicroSkillDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonCompletionDao(): LessonCompletionDao

    companion object {
        const val DATABASE_NAME = "russian_path.db"
    }
}
