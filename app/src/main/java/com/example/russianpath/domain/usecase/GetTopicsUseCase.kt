// app/src/main/java/com/example/russianpath/domain/usecase/GetTopicsUseCase.kt

package com.example.russianpath.domain.usecase

import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    operator fun invoke(gradeId: String): Flow<List<Topic>> {
        return topicRepository.observeTopicsByGrade(gradeId)
    }
}
