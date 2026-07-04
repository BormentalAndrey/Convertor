// app/src/main/java/com/example/russianpath/domain/model/Rule.kt

package com.example.russianpath.domain.model

/**
 * Доменная модель правила орфографии/пунктуации/грамматики.
 */
data class Rule(
    val id: String,
    val topicId: String,
    val gradeId: String,
    val title: String,
    val shortDescription: String,
    val fullDescription: String,
    val ruleText: String,
    val examples: List<String>,
    val counterexamples: List<String>,
    val exceptions: List<String>,
    val ruleCategory: String,
    val difficultyLevel: Int,
    val sortOrder: Int,
    val iconName: String,
    val relatedRuleIds: List<String>,
    val mnemonicText: String,
    val videoUrl: String,
    val imagePath: String
)
