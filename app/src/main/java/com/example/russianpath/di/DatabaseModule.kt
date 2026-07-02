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
    @Provides fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()
    @Provides fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()
    @Provides fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()

    // Analyzer
    @Provides @Singleton fun provideVowelDetector(): VowelDetector = VowelDetector()
    @Provides @Singleton fun provideSyllableSplitter(vowelDetector: VowelDetector): SyllableSplitter = SyllableSplitter(vowelDetector)
    @Provides @Singleton fun provideLetterAnalyzer(vowelDetector: VowelDetector): LetterAnalyzer = LetterAnalyzer(vowelDetector)
    @Provides @Singleton fun provideSyllableAnalyzer(vowelDetector: VowelDetector, splitter: SyllableSplitter): SyllableAnalyzer = SyllableAnalyzer(vowelDetector, splitter)
    @Provides @Singleton fun provideRussianAnalyzer(letterAnalyzer: LetterAnalyzer, syllableAnalyzer: SyllableAnalyzer): RussianAnalyzer = RussianAnalyzerImpl(letterAnalyzer, syllableAnalyzer)

    // Exercise
    @Provides @Singleton fun provideTemplateEngine(): TemplateEngine = TemplateEngineImpl()
    @Provides @Singleton fun provideDistractorGenerator(): DistractorGenerator = DistractorGeneratorImpl()
    @Provides @Singleton fun provideExerciseBuilder(templateEngine: TemplateEngine, distractorGenerator: DistractorGenerator): ExerciseBuilder = ExerciseBuilderImpl(templateEngine, distractorGenerator)
    @Provides @Singleton fun provideExerciseRequestFactory(): ExerciseRequestFactory = ExerciseRequestFactoryImpl()
    @Provides @Singleton fun provideAnswerEvaluator(): AnswerEvaluator = AnswerEvaluatorImpl()

    // Repositories
    @Provides @Singleton fun provideWordRepository(impl: WordRepositoryImpl): WordRepository = impl
    @Provides @Singleton fun provideKnowledgeRepository(impl: KnowledgeRepositoryImpl): KnowledgeRepository = impl
    @Provides @Singleton fun provideProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository = impl
}
