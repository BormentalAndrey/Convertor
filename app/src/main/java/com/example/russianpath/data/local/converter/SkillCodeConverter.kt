package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter

/**
 * TypeConverter для Room.
 *
 * Преобразует:
 * - Int ↔ SkillCode (код навыка из кодификатора)
 * - Int ↔ Difficulty (уровень сложности)
 *
 * Все преобразования безопасны: при неизвестном значении возвращается значение по умолчанию.
 */
class SkillCodeConverter {

    // ========================================================================
    // SkillCode ↔ Int
    // ========================================================================

    /**
     * Преобразует SkillCode в Int (код навыка).
     * Используется при сохранении в БД.
     */
    @TypeConverter
    fun fromSkillCode(skillCode: SkillCode): Int = skillCode.code

    /**
     * Преобразует Int в SkillCode.
     * Если код не найден — возвращает SkillCode.UNKNOWN.
     * Используется при чтении из БД.
     */
    @TypeConverter
    fun toSkillCode(code: Int): SkillCode {
        return SkillCode.entries.firstOrNull { it.code == code } ?: SkillCode.UNKNOWN
    }

    // ========================================================================
    // Difficulty ↔ Int
    // ========================================================================

    /**
     * Преобразует Difficulty в Int.
     */
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): Int = difficulty.value

    /**
     * Преобразует Int в Difficulty.
     * Значение автоматически ограничивается диапазоном 1–5.
     */
    @TypeConverter
    fun toDifficulty(value: Int): Difficulty = Difficulty.fromValue(value)
}

/**
 * Код навыка из кодификатора (ОГЭ/ЕГЭ).
 *
 * Каждое проверяемое умение имеет уникальный числовой код.
 * Например: 1.1 — "Опознавать языковые единицы", 2.3 — "Проводить фонетический анализ".
 */
enum class SkillCode(val code: Int) {
    /** Неизвестный / не задан. */
    UNKNOWN(0),

    /** Опознавать языковые единицы. */
    IDENTIFY_LANGUAGE_UNITS(11),

    /** Проводить фонетический анализ слова. */
    PHONETIC_ANALYSIS(21),

    /** Проводить морфемный анализ слова. */
    MORPHEMIC_ANALYSIS(22),

    /** Проводить морфологический анализ слова. */
    MORPHOLOGICAL_ANALYSIS(23),

    /** Проводить синтаксический анализ. */
    SYNTACTIC_ANALYSIS(24),

    /** Проводить орфографический анализ. */
    ORTHOGRAPHIC_ANALYSIS(31),

    /** Проводить пунктуационный анализ. */
    PUNCTUATION_ANALYSIS(32),

    /** Соблюдать орфографические нормы. */
    ORTHOGRAPHY_NORMS(41),

    /** Соблюдать пунктуационные нормы. */
    PUNCTUATION_NORMS(42),

    /** Соблюдать грамматические нормы. */
    GRAMMAR_NORMS(43),

    /** Соблюдать речевые нормы. */
    SPEECH_NORMS(44),

    /** Определять стили речи. */
    SPEECH_STYLES(51),

    /** Определять типы речи. */
    SPEECH_TYPES(52),

    /** Анализировать текст. */
    TEXT_ANALYSIS(61),

    /** Создавать текст. */
    TEXT_CREATION(62),

    /** Редактировать текст. */
    TEXT_EDITING(63),

    /** Работать с информацией. */
    INFORMATION_PROCESSING(71),

    /** Владеть лексическими средствами. */
    LEXICAL_MEANS(81),

    /** Владеть выразительными средствами. */
    EXPRESSIVE_MEANS(82)
}

/**
 * Уровень сложности.
 *
 * Используется для классификации уроков, вопросов и словарных слов.
 * Значение автоматически ограничивается диапазоном 1–5.
 */
enum class Difficulty(val value: Int) {
    /** Начальный уровень (1 класс, начинающие). */
    BEGINNER(1),

    /** Базовый уровень (2–4 класс). */
    BASIC(2),

    /** Средний уровень (5–7 класс). */
    INTERMEDIATE(3),

    /** Продвинутый уровень (8–9 класс, ОГЭ). */
    ADVANCED(4),

    /** Экспертный уровень (10–11 класс, ЕГЭ). */
    EXPERT(5);

    companion object {
        /**
         * Безопасное создание Difficulty из Int.
         * Значение ограничивается диапазоном 1–5.
         */
        fun fromValue(value: Int): Difficulty {
            return entries.firstOrNull { it.value == value.coerceIn(1, 5) } ?: BEGINNER
        }
    }
}
