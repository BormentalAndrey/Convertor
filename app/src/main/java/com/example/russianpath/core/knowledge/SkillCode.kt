// app/src/main/java/com/example/russianpath/core/knowledge/SkillCode.kt

package com.example.russianpath.core.knowledge

/**
 * Код навыка из кодификатора ОГЭ/ЕГЭ по русскому языку.
 *
 * Каждое проверяемое умение имеет уникальный числовой код.
 * Используется для:
 * - Привязки вопросов к проверяемым навыкам
 * - Построения графа пререквизитов
 * - Аналитики ошибок по кодам
 * - Генерации упражнений
 */
enum class SkillCode(val code: Int) {

    /** Неизвестный / не задан. */
    UNKNOWN(0),

    /** Опознавать языковые единицы, проводить различные виды их анализа. */
    IDENTIFY_LANGUAGE_UNITS(11),

    /** Проводить фонетический анализ слова. */
    PHONETIC_ANALYSIS(21),

    /** Проводить морфемный анализ слова. */
    MORPHEMIC_ANALYSIS(22),

    /** Проводить морфологический анализ слова. */
    MORPHOLOGICAL_ANALYSIS(23),

    /** Проводить синтаксический анализ словосочетания и предложения. */
    SYNTACTIC_ANALYSIS(24),

    /** Проводить орфографический анализ слова. */
    ORTHOGRAPHIC_ANALYSIS(31),

    /** Проводить пунктуационный анализ предложения. */
    PUNCTUATION_ANALYSIS(32),

    /** Соблюдать орфографические нормы в письменной речи. */
    ORTHOGRAPHY_NORMS(41),

    /** Соблюдать пунктуационные нормы в письменной речи. */
    PUNCTUATION_NORMS(42),

    /** Соблюдать грамматические нормы (морфологические и синтаксические). */
    GRAMMAR_NORMS(43),

    /** Соблюдать речевые нормы (лексические, стилистические). */
    SPEECH_NORMS(44),

    /** Определять стили речи (разговорный, научный, официально-деловой и др.). */
    SPEECH_STYLES(51),

    /** Определять типы речи (повествование, описание, рассуждение). */
    SPEECH_TYPES(52),

    /** Анализировать текст: тема, идея, проблема, авторская позиция. */
    TEXT_ANALYSIS(61),

    /** Создавать текст (сочинение, изложение). */
    TEXT_CREATION(62),

    /** Редактировать текст (исправлять ошибки, улучшать стиль). */
    TEXT_EDITING(63),

    /** Работать с информацией: поиск, извлечение, интерпретация. */
    INFORMATION_PROCESSING(71),

    /** Владеть лексическими средствами языка (синонимы, антонимы, фразеологизмы). */
    LEXICAL_MEANS(81),

    /** Владеть выразительными средствами языка (метафоры, эпитеты, сравнения). */
    EXPRESSIVE_MEANS(82);

    companion object {

        /**
         * Безопасное преобразование Int в SkillCode.
         * Если код не найден — возвращает UNKNOWN.
         */
        fun fromCode(code: Int): SkillCode {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }

        /**
         * Возвращает все коды навыков для указанного уровня образования.
         *
         * @param gradeId ID класса ("5"-"11", "oge", "ege").
         * @return Список кодов навыков, проверяемых на этом уровне.
         */
        fun forGrade(gradeId: String): List<SkillCode> {
            return when (gradeId) {
                "5", "6" -> listOf(
                    IDENTIFY_LANGUAGE_UNITS,
                    PHONETIC_ANALYSIS,
                    MORPHEMIC_ANALYSIS,
                    ORTHOGRAPHIC_ANALYSIS,
                    PUNCTUATION_ANALYSIS,
                    ORTHOGRAPHY_NORMS,
                    PUNCTUATION_NORMS,
                    SPEECH_STYLES,
                    SPEECH_TYPES,
                    LEXICAL_MEANS
                )
                "7", "8" -> listOf(
                    IDENTIFY_LANGUAGE_UNITS,
                    PHONETIC_ANALYSIS,
                    MORPHEMIC_ANALYSIS,
                    MORPHOLOGICAL_ANALYSIS,
                    SYNTACTIC_ANALYSIS,
                    ORTHOGRAPHIC_ANALYSIS,
                    PUNCTUATION_ANALYSIS,
                    ORTHOGRAPHY_NORMS,
                    PUNCTUATION_NORMS,
                    GRAMMAR_NORMS,
                    SPEECH_STYLES,
                    SPEECH_TYPES,
                    TEXT_ANALYSIS,
                    LEXICAL_MEANS,
                    EXPRESSIVE_MEANS
                )
                "9", "oge" -> listOf(
                    IDENTIFY_LANGUAGE_UNITS,
                    PHONETIC_ANALYSIS,
                    MORPHEMIC_ANALYSIS,
                    MORPHOLOGICAL_ANALYSIS,
                    SYNTACTIC_ANALYSIS,
                    ORTHOGRAPHIC_ANALYSIS,
                    PUNCTUATION_ANALYSIS,
                    ORTHOGRAPHY_NORMS,
                    PUNCTUATION_NORMS,
                    GRAMMAR_NORMS,
                    SPEECH_NORMS,
                    SPEECH_STYLES,
                    SPEECH_TYPES,
                    TEXT_ANALYSIS,
                    TEXT_CREATION,
                    TEXT_EDITING,
                    INFORMATION_PROCESSING,
                    LEXICAL_MEANS,
                    EXPRESSIVE_MEANS
                )
                "10", "11", "ege" -> entries.filter { it != UNKNOWN }
                else -> entries.filter { it != UNKNOWN }
            }
        }
    }
}
