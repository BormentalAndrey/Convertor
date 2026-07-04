// app/src/main/java/com/example/russianpath/core/exercise/ExerciseIdFactory.kt

package com.example.russianpath.core.exercise

object ExerciseIdFactory {

    private const val PREFIX = "exercise"

    fun create(fingerprint: ExerciseFingerprint): ExerciseId = ExerciseId(
        "$PREFIX:${fingerprint.skillCode.code}_${fingerprint.wordId.value}_d${fingerprint.difficulty.value}_s${fingerprint.seed}"
    )
}
