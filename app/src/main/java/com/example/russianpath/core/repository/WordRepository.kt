package com.example.russianpath.core.repository

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.knowledge.SkillCode

interface WordRepository {
    suspend fun findByNormalized(normalized: String): DictionaryWord?
    suspend fun findBySkill(skillCode: SkillCode): List<DictionaryWord>
}
