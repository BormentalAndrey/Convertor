package com.example.russianpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.russianpath.data.local.dao.*
import com.example.russianpath.data.local.entity.*

@Database(
    entities = [
        TopicEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        UserProgressEntity::class,
        LessonCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
}
