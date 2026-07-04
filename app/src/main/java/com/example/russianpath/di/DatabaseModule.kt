// app/src/main/java/com/example/russianpath/di/DatabaseModule.kt

package com.example.russianpath.di

import android.content.Context
import androidx.room.Room
import com.example.russianpath.data.local.AppDatabase
import com.example.russianpath.data.local.AppDatabaseMigrations
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGradeDao(db: AppDatabase): GradeDao = db.gradeDao()

    @Provides
    @Singleton
    fun provideSectionDao(db: AppDatabase): SectionDao = db.sectionDao()

    @Provides
    @Singleton
    fun provideTopicDao(db: AppDatabase): TopicDao = db.topicDao()

    @Provides
    @Singleton
    fun provideLearningObjectiveDao(db: AppDatabase): LearningObjectiveDao =
        db.learningObjectiveDao()

    @Provides
    @Singleton
    fun provideMicroSkillDao(db: AppDatabase): MicroSkillDao = db.microSkillDao()

    @Provides
    @Singleton
    fun provideRuleDao(db: AppDatabase): RuleDao = db.ruleDao()

    @Provides
    @Singleton
    fun provideDictionaryDao(db: AppDatabase): DictionaryDao = db.dictionaryDao()

    @Provides
    @Singleton
    fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()

    @Provides
    @Singleton
    fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()

    @Provides
    @Singleton
    fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    @Singleton
    fun provideLessonCompletionDao(db: AppDatabase): LessonCompletionDao =
        db.lessonCompletionDao()
}
