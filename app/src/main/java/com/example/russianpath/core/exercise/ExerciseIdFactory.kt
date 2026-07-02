package com.example.russianpath.core.exercise

/**
 * Фабрика идентификаторов упражнений.
 *
 * ## Формат (часть контракта v1.0.0)
 * ```
 * exercise:{skillCode.key}_{wordId.value}_d{difficulty.value}_s{seed}
 * ```
 *
 * ## Стабильность
 * Формат является частью публичного контракта.
 * Изменение формата требует мажорной версии контракта и миграции данных.
 *
 * ## Пример
 * ```
 * exercise:skill.2001_word_мама_d5_s17
 * ```
 */
object ExerciseIdFactory {

    private const val PREFIX = "exercise"

    fun create(fingerprint: ExerciseFingerprint): ExerciseId = ExerciseId(
        "$PREFIX:${fingerprint.skillCode.key}_${fingerprint.wordId.value}_d${fingerprint.difficulty.value}_s${fingerprint.seed}"
    )
}
