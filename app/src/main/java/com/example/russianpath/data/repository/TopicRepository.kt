package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepository @Inject constructor(
    private val topicDao: TopicDao
) {
    fun getTopicsByGrade(grade: Int): Flow<List<Topic>> {
        return topicDao.getTopicsByGrade(grade).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getAllTopics(): Flow<List<Topic>> {
        return topicDao.getAllTopics().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun unlockTopic(id: String) {
        topicDao.unlockTopic(id)
    }
}

private fun TopicEntity.toDomainModel(): Topic {
    return Topic(
        id = id,
        gradeLevel = gradeLevel,
        title = title,
        description = description,
        iconName = icon, // ИСПРАВЛЕНО: сопоставлено с полем icon в TopicEntity
        sortOrder = sortOrder,
        isUnlocked = isUnlocked
    )
}
