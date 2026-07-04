// app/src/main/java/com/example/russianpath/core/exercise/ExerciseFingerprint.kt

package com.example.russianpath.core.exercise

import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.dictionary.WordId
import com.example.russianpath.core.knowledge.SkillCode

data class ExerciseFingerprint(
    val skillCode: SkillCode,
    val wordId: WordId,
    val difficulty: Difficulty,
    val seed: Int
)
