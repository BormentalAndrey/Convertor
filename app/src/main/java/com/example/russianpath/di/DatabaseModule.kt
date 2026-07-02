package com.example.russianpath.di

import android.content.Context
import androidx.room.Room
import com.example.russianpath.data.local.AppDatabase
import com.example.russianpath.data.local.dao.*
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "russian_path.db"
        )
        .fallbackToDestructiveMigration() // Удалит старые данные при смене версии
        .build()
    }

    // Старые DAO
    @Provides
    fun provideTopicDao(database: AppDatabase): TopicDao = database.topicDao()

    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao = database.lessonDao()

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao = database.userProgressDao()

    // Новые DAO
    @Provides
    fun provideGradeDao(database: AppDatabase): GradeDao = database.gradeDao()

    @Provides
    fun provideSectionDao(database: AppDatabase): SectionDao = database.sectionDao()

    @Provides
    fun provideTopicDaoV2(database: AppDatabase): TopicDaoV2 = database.topicDaoV2()

    @Provides
    fun provideLearningObjectiveDao(database: AppDatabase): LearningObjectiveDao =
        database.learningObjectiveDao()

    @Provides
    fun provideMicroSkillDao(database: AppDatabase): MicroSkillDao = database.microSkillDao()

    @Provides
    fun provideDictionaryDao(database: AppDatabase): DictionaryDao = database.dictionaryDao()
}
