package com.example.russianpath.core.knowledge

/**
 * Идентификатор учебной цели.
 * Формат: "obj_" + описание
 * Пример: ObjectiveId("obj_count_syllables")
 */
@JvmInline
value class ObjectiveId(val value: String)
