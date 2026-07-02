package com.example.russianpath.di

import android.content.Context
import androidx.room.Room
import com.example.russianpath.core.analysis.RussianAnalyzer
import com.example.russianpath.core.exercise.*
import com.example.russianpath.core.progress.AnswerEvaluator
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.core.repository.ProgressRepository
import com.example.russianpath.core.repository.WordRepository
import com.example.russianpath.data.analyzer.*
import com.example.russianpath.data.exercise.*
import com.example.russianpath.data.local.AppDatabase
import com.example.russianpath.data.local.dao.*
import com.example.russianpath.data.local.mapper.DictionaryWordMapper
import com.example.russianpath.data.local.mapper.KnowledgeMapper
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
        return Room.databaseBuilder(context, AppDatabase::class.java, "russian_path.db")
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

    // Mappers
    @Provides @Singleton fun provideDictionaryWordMapper(): DictionaryWordMapper = DictionaryWordMapper()
    @Provides @Singleton fun provideKnowledgeMapper(): KnowledgeMapper = KnowledgeMapper()

    // Analyzer
    @Provides @Singleton fun provideVowelDetector(): VowelDetector = VowelDetector()
    @Provides @Singleton fun provideSyllableSplitter(vd: VowelDetector): SyllableSplitter = SyllableSplitter(vd)
    @Provides @Singleton fun provideLetterAnalyzer(vd: VowelDetector): LetterAnalyzer = LetterAnalyzer(vd)
    @Provides @Singleton fun provideSyllableAnalyzer(sp: SyllableSplitter): SyllableAnalyzer = SyllableAnalyzer(sp)
    @Provides @Singleton fun provideRussianAnalyzer(la: LetterAnalyzer, sa: SyllableAnalyzer): RussianAnalyzer = RussianAnalyzerImpl(la, sa)

    // Exercise
    @Provides @Singleton fun provideTemplateEngine(): TemplateEngine = TemplateEngineImpl()
    @Provides @Singleton fun provideDistractorGenerator(): DistractorGenerator = DistractorGeneratorImpl()
    @Provides @Singleton fun provideExerciseBuilder(te: TemplateEngine, dg: DistractorGenerator): ExerciseBuilder = ExerciseBuilderImpl(te, dg)
    @Provides @Singleton fun provideExerciseRequestFactory(): ExerciseRequestFactory = ExerciseRequestFactoryImpl()
    @Provides @Singleton fun provideAnswerEvaluator(): AnswerEvaluator = AnswerEvaluatorImpl()

    // Repositories
    @Provides @Singleton fun provideWordRepository(impl: WordRepositoryImpl): WordRepository = impl
    @Provides @Singleton fun provideKnowledgeRepository(impl: KnowledgeRepositoryImpl): KnowledgeRepository = impl
    @Provides @Singleton fun provideProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository = impl
}
