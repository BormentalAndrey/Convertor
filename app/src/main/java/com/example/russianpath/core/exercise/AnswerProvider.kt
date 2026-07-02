package com.example.russianpath.core.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.knowledge.SkillCode

interface AnswerProvider {
    fun getCorrectAnswer(skillCode: SkillCode, analysis: WordAnalysis): CorrectAnswer
}
