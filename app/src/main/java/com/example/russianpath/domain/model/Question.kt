// app/src/main/java/com/example/russianpath/domain/model/Question.kt

package com.example.russianpath.domain.model

/**
 * Тип вопроса в упражнении.
 *
 * Определяет механику взаимодействия пользователя с вопросом.
 */
enum class QuestionType {
    /** Выбор одного правильного ответа из нескольких вариантов. */
    SINGLE_CHOICE,

    /** Выбор нескольких правильных ответов. */
    MULTIPLE_CHOICE,

    /** Ввод текста с клавиатуры. */
    TEXT_INPUT,

    /** Составление слова/предложения из букв/слов (drag & drop). */
    WORD_DRAG,

    /** Расстановка слов/предложений в правильном порядке. */
    SEQUENCE_ORDER,

    /** Сопоставление пар (например, слово → определение). */
    MATCHING,

    /** Диктант: прослушать аудио и написать. */
    DICTATION,

    /** Вставить пропущенную букву/слово в тексте. */
    FILL_IN_BLANK,

    /** Выбрать правильное ударение в слове. */
    STRESS_SELECTION,

    /** Разбор слова по составу (морфемный разбор). */
    MORPHEMIC_ANALYSIS;

    companion object {
        /**
         * Безопасное преобразование строки в QuestionType.
         * Возвращает SINGLE_CHOICE для неизвестных типов.
         */
        fun fromString(value: String): QuestionType {
            return try {
                valueOf(value.uppercase())
            } catch (_: IllegalArgumentException) {
                SINGLE_CHOICE
            }
        }
    }
}

/**
 * Доменная модель вопроса упражнения.
 */
data class Question(
    val id: String,
    val lessonId: String,
    val primarySkillId: String = "",
    val questionType: QuestionType,
    val promptText: String,
    val promptAudioPath: String = "",
    val promptImagePath: String = "",
    val options: List<String> = emptyList(),
    val draggableWords: List<String> = emptyList(),
    val correctAnswer: String = "",
    val correctOrder: List<Int> = emptyList(),
    val acceptableAnswers: List<String> = emptyList(),
    val hintText: String = "",
    val explanationText: String = "",
    val audioPath: String = "",
    val ruleReference: String = "",
    val ruleReferenceId: String = "",
    val difficulty: Int = 1,
    val timeLimitSeconds: Int = 0,
    val points: Int = 10,
    val penaltyPoints: Int = 0,
    val maxAttempts: Int = 0,
    val isRequired: Boolean = true
) {

    fun checkAnswer(userAnswer: Any): Boolean {
        return when (questionType) {
            QuestionType.SINGLE_CHOICE -> {
                val answer = userAnswer as? String ?: return false
                answer == correctAnswer
            }
            QuestionType.MULTIPLE_CHOICE -> {
                val answers = userAnswer as? List<*> ?: return false
                val correctSet = correctAnswer.split(",").map { it.trim() }.toSet()
                val userSet = answers.filterIsInstance<String>().toSet()
                userSet == correctSet
            }
            QuestionType.TEXT_INPUT -> {
                val answer = (userAnswer as? String ?: return false).trim().lowercase()
                val correct = correctAnswer.trim().lowercase()
                val acceptable = acceptableAnswers.map { it.trim().lowercase() }
                answer == correct || answer in acceptable
            }
            QuestionType.SEQUENCE_ORDER,
            QuestionType.WORD_DRAG -> {
                val userOrder = userAnswer as? List<*> ?: return false
                val userInts = userOrder.filterIsInstance<Int>()
                userInts == correctOrder
            }
            QuestionType.MATCHING,
            QuestionType.DICTATION,
            QuestionType.FILL_IN_BLANK,
            QuestionType.STRESS_SELECTION,
            QuestionType.MORPHEMIC_ANALYSIS -> {
                checkComplexAnswer(userAnswer)
            }
        }
    }

    private fun checkComplexAnswer(userAnswer: Any): Boolean {
        val answer = userAnswer as? String ?: return false
        return answer.trim().lowercase() == correctAnswer.trim().lowercase()
    }

    fun isAnswerAcceptable(userAnswer: String): Boolean {
        val normalized = userAnswer.trim().lowercase()
        return acceptableAnswers.any { it.trim().lowercase() == normalized }
    }

    fun getFeedback(isCorrect: Boolean): String {
        return if (isCorrect) {
            "Правильно! $explanationText"
        } else {
            "Неправильно. ${hintText.ifBlank { "Попробуй ещё раз." }}"
        }
    }

    fun getQuestionTypeLabel(): String {
        return when (questionType) {
            QuestionType.SINGLE_CHOICE -> "Выбери ответ"
            QuestionType.MULTIPLE_CHOICE -> "Выбери несколько"
            QuestionType.TEXT_INPUT -> "Введи ответ"
            QuestionType.WORD_DRAG -> "Составь слово"
            QuestionType.SEQUENCE_ORDER -> "Расставь по порядку"
            QuestionType.MATCHING -> "Сопоставь пары"
            QuestionType.DICTATION -> "Диктант"
            QuestionType.FILL_IN_BLANK -> "Вставь пропуск"
            QuestionType.STRESS_SELECTION -> "Выбери ударение"
            QuestionType.MORPHEMIC_ANALYSIS -> "Разбор слова"
        }
    }
}
