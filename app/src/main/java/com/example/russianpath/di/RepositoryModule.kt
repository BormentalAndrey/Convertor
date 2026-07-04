// app/src/main/java/com/example/russianpath/di/RepositoryModule.kt

package com.example.russianpath.di

import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.RuleDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.mapper.KnowledgeMapper
import com.example.russianpath.data.repository.KnowledgeRepositoryImpl
import com.example.russianpath.data.repository.LessonRepository
import com.example.russianpath.data.repository.RuleRepository
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideKnowledgeMapper(): KnowledgeMapper = KnowledgeMapper()

    @Provides
    @Singleton
    fun provideKnowledgeRepository(
        learningObjectiveDao: LearningObjectiveDao,
        microSkillDao: MicroSkillDao,
        knowledgeMapper: KnowledgeMapper
    ): KnowledgeRepository {
        return KnowledgeRepositoryImpl(learningObjectiveDao, microSkillDao, knowledgeMapper)
    }

    @Provides
    @Singleton
    fun provideLessonRepository(
        lessonDao: LessonDao,
        questionDao: QuestionDao,
        lessonCompletionDao: LessonCompletionDao
    ): LessonRepository {
        return LessonRepository(lessonDao, questionDao, lessonCompletionDao)
    }

    @Provides
    @Singleton
    fun provideRuleRepository(
        ruleDao: RuleDao
    ): RuleRepository {
        return RuleRepository(ruleDao)
    }

    @Provides
    @Singleton
    fun provideTopicRepository(
        topicDao: TopicDao
    ): TopicRepository {
        return TopicRepository(topicDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userProgressDao: UserProgressDao,
        lessonCompletionDao: LessonCompletionDao
    ): UserRepository {
        return UserRepository(userProgressDao, lessonCompletionDao)
    }
}
