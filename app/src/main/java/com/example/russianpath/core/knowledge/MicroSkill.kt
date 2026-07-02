package com.example.russianpath.core.knowledge

data class MicroSkill(
    val id: MicroSkillId,
    val objectiveId: ObjectiveId,
    val skillCode: SkillCode,
    val name: String,
    val description: String
)
