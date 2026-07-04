package com.example.russianpath.di

import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.mapper.KnowledgeMapper
import com.example.russianpath.data.repository.KnowledgeRepositoryImpl
import com.example.russianpath.data.repository.LessonRepository
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt модуль для предоставления зависимостей слоя Repository.
 *
 * Архитектура зависимостей:
 * ```
 * DatabaseModule (DAO)
 *     ↓
 * RepositoryModule (Repository)
 *     ↓
 * ViewModel (Presentation)
 * ```
 *
 * Предоставляет:
 * - KnowledgeMapper — маппер Entity → Domain для графа знаний
 * - KnowledgeRepository — работа с целями обучения и микро-навыками
 * - LessonRepository — работа с уроками и вопросами
 * - TopicRepository — работа с темами
 * - UserRepository — работа с прогрессом пользователя
 *
 * Все зависимости имеют scope @Singleton:
 * - Репозитории stateless — безопасно переиспользовать один экземпляр
 * - KnowledgeMapper stateless — безопасно переиспользовать один экземпляр
 *
 * @see DatabaseModule
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // ========================================================================
    // Mapper
    // ========================================================================

    /**
     * Предоставляет KnowledgeMapper для преобразования Entity → Domain.
     *
     * Вынесен в отдельный объект для соблюдения Single Responsibility:
     * репозитории не должны знать о деталях маппинга.
     *
     * @return Единственный экземпляр KnowledgeMapper.
     */
    @Provides
    @Singleton
    fun provideKnowledgeMapper(): KnowledgeMapper = KnowledgeMapper()

    // ========================================================================
    // Repositories
    // ========================================================================

    /**
     * Предоставляет KnowledgeRepository для работы с графом знаний.
     *
     * Зависит от:
     * - LearningObjectiveDao — цели обучения
     * - MicroSkillDao — микро-навыки
     * - KnowledgeMapper — маппинг Entity → Domain
     *
     * @return Реализация KnowledgeRepository (KnowledgeRepositoryImpl).
     */
    @Provides
    @Singleton
    fun provideKnowledgeRepository(
        learningObjectiveDao: LearningObjectiveDao,
        microSkillDao: MicroSkillDao,
        knowledgeMapper: KnowledgeMapper
    ): KnowledgeRepository {
        return KnowledgeRepositoryImpl(
            learningObjectiveDao = learningObjectiveDao,
            microSkillDao = microSkillDao,
            knowledgeMapper = knowledgeMapper
        )
    }

    /**
     * Предоставляет LessonRepository для работы с уроками и вопросами.
     *
     * Зависит от:
     * - LessonDao — уроки
     * - QuestionDao — вопросы
     * - LessonCompletionDao — история прохождений
     *
     * @return Единственный экземпляр LessonRepository.
     */
    @Provides
    @Singleton
    fun provideLessonRepository(
        lessonDao: LessonDao,
        questionDao: QuestionDao,
        lessonCompletionDao: LessonCompletionDao
    ): LessonRepository {
        return LessonRepository(
            lessonDao = lessonDao,
            questionDao = questionDao,
            lessonCompletionDao = lessonCompletionDao
        )
    }

    /**
     * Предоставляет TopicRepository для работы с темами обучения.
     *
     * Зависит от:
     * - TopicDao — темы
     *
     * @return Единственный экземпляр TopicRepository.
     */
    @Provides
    @Singleton
    fun provideTopicRepository(
        topicDao: TopicDao
    ): TopicRepository {
        return TopicRepository(
            topicDao = topicDao
        )
    }

    /**
     * Предоставляет UserRepository для работы с прогрессом пользователя.
     *
     * Зависит от:
     * - UserProgressDao — общий прогресс (XP, уровень, жизни...)
     * - LessonCompletionDao — история прохождений уроков
     *
     * @return Единственный экземпляр UserRepository.
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        userProgressDao: UserProgressDao,
        lessonCompletionDao: LessonCompletionDao
    ): UserRepository {
        return UserRepository(
            userProgressDao = userProgressDao,
            lessonCompletionDao = lessonCompletionDao
        )
    }
}
