package com.example.russianpath.core.knowledge

data class SkillDescriptor(
    val code: SkillCode,
    val titleKey: String,
    val descriptionKey: String,
    val knowledgeArea: KnowledgeArea
)
