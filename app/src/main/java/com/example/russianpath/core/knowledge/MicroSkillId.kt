package com.example.russianpath.core.knowledge

/**
 * Идентификатор микронавыка.
 * Формат: "ms_" + skillCode.key
 * Пример: MicroSkillId("ms_count_syllables")
 */
@JvmInline
value class MicroSkillId(val value: String)
