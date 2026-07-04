// app/src/main/java/com/example/russianpath/data/local/converter/SkillCodeConverter.kt

package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.knowledge.SkillCode

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
        return SkillCode.fromCode(code)
    }

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
 * Уровень сложности.
 *
 * Используется для классификации уроков, вопросов и словарных слов.
 * Значение автоматически ограничивается диапазоном 1–5.
 */
enum class Difficulty(val value: Int) {
    /** Начальный уровень. */
    BEGINNER(1),

    /** Базовый уровень. */
    BASIC(2),

    /** Средний уровень. */
    INTERMEDIATE(3),

    /** Продвинутый уровень. */
    ADVANCED(4),

    /** Экспертный уровень. */
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
