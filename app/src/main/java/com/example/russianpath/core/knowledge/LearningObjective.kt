package com.example.russianpath.core.knowledge

/**
 * Доменная модель цели обучения.
 *
 * Представляет конкретную образовательную цель внутри темы.
 * Связана с кодификатором навыков через skillCodeId.
 *
 * Пример: "Научиться различать приставки ПРЕ- и ПРИ- по значению"
 */
data class LearningObjective(
    /** Уникальный идентификатор цели. */
    val id: String,

    /** ID темы, к которой относится цель. */
    val topicId: String,

    /** Код навыка из кодификатора ОГЭ/ЕГЭ (0 — не привязан). */
    val skillCodeId: Int,

    /** Название цели. */
    val name: String,

    /** Описание цели. */
    val description: String,

    /** Порядок сортировки внутри темы. */
    val sortOrder: Int,

    /** Список ID целей, которые нужно достичь перед этой. */
    val prerequisiteObjectiveIds: List<String> = emptyList(),

    /** Уровень по таксономии Блума (1=знание, 2=понимание, 3=применение, 4=анализ, 5=синтез, 6=оценка). */
    val bloomTaxonomyLevel: Int = 1,

    /** Порог освоения в процентах (по умолчанию 80%). */
    val masteryThresholdPercent: Int = 80,

    /** Является ли цель обязательной (или опциональной). */
    val isRequired: Boolean = true
)
