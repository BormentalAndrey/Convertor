package com.example.russianpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.russianpath.data.local.converter.SkillCodeConverter
import com.example.russianpath.data.local.converter.StringListConverter
import com.example.russianpath.data.local.dao.*
import com.example.russianpath.data.local.entity.*

@Database(
    entities = [
        // Новая архитектура
        GradeEntity::class,
        SectionEntity::class,
        TopicEntity::class,
        LearningObjectiveEntity::class,
        MicroSkillEntity::class,
        DictionaryWordEntity::class,

        // Старая архитектура (нужна для LessonViewModel и UserRepository)
        LessonEntity::class,
        QuestionEntity::class,
        UserProgressEntity::class,
        LessonCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    SkillCodeConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    // Новые DAO
    abstract fun gradeDao(): GradeDao
    abstract fun sectionDao(): SectionDao
    abstract fun topicDao(): TopicDao
    abstract fun learningObjectiveDao(): LearningObjectiveDao
    abstract fun microSkillDao(): MicroSkillDao
    abstract fun dictionaryDao(): DictionaryDao

    // Старые DAO (нужны для LessonViewModel и UserRepository)
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
}
