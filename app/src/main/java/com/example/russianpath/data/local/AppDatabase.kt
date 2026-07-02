// app/src/main/java/com/example/russianpath/data/local/AppDatabase.kt
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
        GradeEntity::class,
        SectionEntity::class,
        TopicEntity::class,
        LearningObjectiveEntity::class,
        MicroSkillEntity::class,
        DictionaryWordEntity::class
    ],
    version = 1,
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
}
