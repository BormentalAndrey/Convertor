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
 *
 * Представляет один вопрос в уроке. Поддерживает все типы вопросов
 * через полиморфные поля (options, draggableWords, correctOrder и т.д.).
 *
 * Конкретный тип вопроса определяет, какие поля используются:
 * - SINGLE_CHOICE → options, correctAnswer
 * - TEXT_INPUT → correctAnswer, acceptableAnswers
 * - WORD_DRAG → draggableWords, correctOrder
 * - SEQUENCE_ORDER → options, correctOrder
 * - MATCHING → pairs (лежит в dataJson на Entity-уровне)
 */
data class Question(
    /** Уникальный идентификатор вопроса. */
    val id: String,

    /** ID урока, к которому относится вопрос. */
    val lessonId: String,

    /** ID микро-навыка, проверяемого вопросом. Для аналитики ошибок. */
    val primarySkillId: String = "",

    /** Тип вопроса (определяет UI и логику проверки). */
    val questionType: QuestionType,

    /** Текст задания/вопроса. */
    val promptText: String,

    /** Путь к аудиофайлу условия (для диктантов). */
    val promptAudioPath: String = "",

    /** Путь к изображению условия. */
    val promptImagePath: String = "",

    /** Варианты ответов для SINGLE_CHOICE, MULTIPLE_CHOICE. */
    val options: List<String> = emptyList(),

    /** Слова для перетаскивания в WORD_DRAG. */
    val draggableWords: List<String> = emptyList(),

    /** Правильный ответ (для SINGLE_CHOICE, TEXT_INPUT — строка). */
    val correctAnswer: String = "",

    /** Правильный порядок индексов (для SEQUENCE_ORDER). */
    val correctOrder: List<Int> = emptyList(),

    /** Допустимые варианты ответа (для TEXT_INPUT — синонимы, регистр). */
    val acceptableAnswers: List<String> = emptyList(),

    /** Текст подсказки. */
    val hintText: String = "",

    /** Текст объяснения правильного ответа. */
    val explanationText: String = "",

    /** Путь к аудиофайлу (произношение слова). */
    val audioPath: String = "",

    /** Ссылка на правило орфографии. */
    val ruleReference: String = "",

    /** ID правила в таблице правил. */
    val ruleReferenceId: String = "",

    /** Уровень сложности вопроса (1–5). */
    val difficulty: Int = 1,

    /** Ограничение по времени в секундах (0 — без ограничения). */
    val timeLimitSeconds: Int = 0,

    /** Баллы за правильный ответ. */
    val points: Int = 10,

    /** Штрафные баллы за неправильный ответ. */
    val penaltyPoints: Int = 0,

    /** Максимальное количество попыток (0 — неограниченно). */
    val maxAttempts: Int = 0,

    /** Является ли вопрос обязательным. */
    val isRequired: Boolean = true
) {

    /**
     * Проверяет ответ пользователя.
     *
     * @param userAnswer Ответ пользователя (строка для SINGLE_CHOICE/TEXT_INPUT,
     *                   список строк для MULTIPLE_CHOICE,
     *                   список индексов для SEQUENCE_ORDER/WORD_DRAG).
     * @return true если ответ правильный.
     */
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
                // Для сложных типов проверка делегируется специализированному чекеру
                checkComplexAnswer(userAnswer)
            }
        }
    }

    /**
     * Проверка ответа для сложных типов вопросов.
     * Может быть переопределена или расширена при добавлении новых типов.
     */
    private fun checkComplexAnswer(userAnswer: Any): Boolean {
        // Базовая реализация — сравнение строк
        val answer = userAnswer as? String ?: return false
        return answer.trim().lowercase() == correctAnswer.trim().lowercase()
    }

    /**
     * Проверяет, является ли ответ пользователя допустимым
     * (не обязательно правильным, но входящим в список acceptable).
     */
    fun isAnswerAcceptable(userAnswer: String): Boolean {
        val normalized = userAnswer.trim().lowercase()
        return acceptableAnswers.any { it.trim().lowercase() == normalized }
    }

    /**
     * Возвращает текст обратной связи в зависимости от правильности ответа.
     */
    fun getFeedback(isCorrect: Boolean): String {
        return if (isCorrect) {
            "Правильно! $explanationText"
        } else {
            "Неправильно. ${hintText.ifBlank { "Попробуй ещё раз." }}"
        }
    }

    /**
     * Возвращает текстовое представление типа вопроса для UI.
     */
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
