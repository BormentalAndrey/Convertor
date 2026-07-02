package com.example.russianpath.di

import android.content.Context
import androidx.room.Room
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.core.repository.ProgressRepository
import com.example.russianpath.core.repository.WordRepository
import com.example.russianpath.data.local.AppDatabase
import com.example.russianpath.data.local.dao.*
import com.example.russianpath.data.repository.KnowledgeRepositoryImpl
import com.example.russianpath.data.repository.ProgressRepositoryImpl
import com.example.russianpath.data.repository.WordRepositoryImpl
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "russian_path.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // DAO
    @Provides fun provideGradeDao(db: AppDatabase): GradeDao = db.gradeDao()
    @Provides fun provideSectionDao(db: AppDatabase): SectionDao = db.sectionDao()
    @Provides fun provideTopicDao(db: AppDatabase): TopicDao = db.topicDao()
    @Provides fun provideLearningObjectiveDao(db: AppDatabase): LearningObjectiveDao = db.learningObjectiveDao()
    @Provides fun provideMicroSkillDao(db: AppDatabase): MicroSkillDao = db.microSkillDao()
    @Provides fun provideDictionaryDao(db: AppDatabase): DictionaryDao = db.dictionaryDao()

    // Repositories
    @Provides @Singleton
    fun provideWordRepository(impl: WordRepositoryImpl): WordRepository = impl

    @Provides @Singleton
    fun provideKnowledgeRepository(impl: KnowledgeRepositoryImpl): KnowledgeRepository = impl

    @Provides @Singleton
    fun provideProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository = impl
}
