package com.example.russianpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.russianpath.data.local.converter.Converters
import com.example.russianpath.data.local.dao.*
import com.example.russianpath.data.local.entity.*

@Database(
    entities = [
        // Старые таблицы (оставляем без изменений)
        TopicEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        UserProgressEntity::class,
        LessonCompletionEntity::class,

        // Новые таблицы
        GradeEntity::class,
        SectionEntity::class,
        TopicEntityV2::class,
        LearningObjectiveEntity::class,
        MicroSkillEntity::class,
        DictionaryWordEntity::class
    ],
    version = 2,  // Было 1, стало 2
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Старые DAO (оставляем)
    abstract fun topicDao(): TopicDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao

    // Новые DAO
    abstract fun gradeDao(): GradeDao
    abstract fun sectionDao(): SectionDao
    abstract fun topicDaoV2(): TopicDaoV2
    abstract fun learningObjectiveDao(): LearningObjectiveDao
    abstract fun microSkillDao(): MicroSkillDao
    abstract fun dictionaryDao(): DictionaryDao
}
