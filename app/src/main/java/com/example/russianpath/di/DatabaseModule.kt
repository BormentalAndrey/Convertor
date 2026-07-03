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
        .fallbackToDestructiveMigration()
        .build()
    }

    // Новые DAO
    @Provides
    fun provideGradeDao(db: AppDatabase): GradeDao = db.gradeDao()

    @Provides
    fun provideSectionDao(db: AppDatabase): SectionDao = db.sectionDao()

    @Provides
    fun provideTopicDao(db: AppDatabase): TopicDao = db.topicDao()

    @Provides
    fun provideLearningObjectiveDao(db: AppDatabase): LearningObjectiveDao =
        db.learningObjectiveDao()

    @Provides
    fun provideMicroSkillDao(db: AppDatabase): MicroSkillDao = db.microSkillDao()

    @Provides
    fun provideDictionaryDao(db: AppDatabase): DictionaryDao = db.dictionaryDao()

    // Старые DAO
    @Provides
    fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()

    @Provides
    fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()

    @Provides
    fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
}
