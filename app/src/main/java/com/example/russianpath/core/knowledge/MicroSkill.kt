package com.example.russianpath.core.knowledge

/**
 * Доменная модель микро-навыка.
 *
 * Атомарная единица знания. Несколько микро-навыков образуют цель обучения.
 * Имеет иерархию через parentMicroSkillId.
 *
 * Пример: "Определение приставки в слове", "Выделение корня", "Проверка ударением".
 */
data class MicroSkill(
    /** Уникальный идентификатор микро-навыка. */
    val id: String,

    /** ID цели обучения, к которой относится навык. */
    val objectiveId: String,

    /** Код навыка из кодификатора ОГЭ/ЕГЭ. */
    val skillCodeId: Int,

    /** ID родительского микро-навыка (пустая строка — корневой). */
    val parentMicroSkillId: String = "",

    /** Название микро-навыка. */
    val name: String,

    /** Описание микро-навыка. */
    val description: String,

    /** Порядок сортировки внутри цели. */
    val sortOrder: Int,

    /** Уровень сложности (1–5). */
    val difficultyLevel: Int = 1,

    /** Категория ошибки (например, "phonetic", "morphemic", "syntactic"). */
    val errorCategory: String = "",

    /** Список типичных ошибочных паттернов. */
    val typicalMistakePatterns: List<String> = emptyList()
)
