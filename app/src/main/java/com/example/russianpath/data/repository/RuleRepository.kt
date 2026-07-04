// app/src/main/java/com/example/russianpath/data/repository/RuleRepository.kt

package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.RuleDao
import com.example.russianpath.data.local.entity.RuleEntity
import com.example.russianpath.domain.model.Rule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleRepository @Inject constructor(
    private val ruleDao: RuleDao
) {

    private val gson = Gson()

    fun observeRulesByTopic(topicId: String): Flow<List<Rule>> {
        return ruleDao.observeByTopic(topicId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeRulesByGrade(gradeId: String): Flow<List<Rule>> {
        return ruleDao.observeByGrade(gradeId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeRulesByCategoryAndGrade(category: String, gradeId: String): Flow<List<Rule>> {
        return ruleDao.observeByCategoryAndGrade(category, gradeId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getRuleById(id: String): Rule? {
        return withContext(Dispatchers.IO) {
            ruleDao.getById(id)?.toDomainModel()
        }
    }

    suspend fun searchRules(query: String, limit: Int = 20): List<Rule> {
        return withContext(Dispatchers.IO) {
            ruleDao.search(query, limit).map { it.toDomainModel() }
        }
    }

    private fun RuleEntity.toDomainModel(): Rule {
        return Rule(
            id = id,
            topicId = topicId,
            gradeId = gradeId,
            title = title,
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            ruleText = ruleText,
            examples = parseStringList(examplesJson),
            counterexamples = parseStringList(counterexamplesJson),
            exceptions = parseStringList(exceptionsJson),
            ruleCategory = ruleCategory,
            difficultyLevel = difficultyLevel,
            sortOrder = sortOrder,
            iconName = iconName,
            relatedRuleIds = parseStringList(relatedRuleIdsJson),
            mnemonicText = mnemonicText,
            videoUrl = videoUrl,
            imagePath = imagePath
        )
    }

    private fun parseStringList(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
