package com.example.russianpath.domain.usecase

import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    operator fun invoke(grade: Int): Flow<List<Topic>> {
        return topicRepository.getTopicsByGrade(grade)
    }
}
