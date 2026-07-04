// app/src/main/java/com/example/russianpath/presentation/components/KnopaMood.kt

package com.example.russianpath.presentation.components

/**
 * Настроение маскота Кнопы.
 *
 * Кнопа — анимированный персонаж-помощник, который сопровождает пользователя
 * на протяжении всего обучения. Его настроение меняется в зависимости от контекста.
 *
 * Используется в KnopaImage для выбора подходящего изображения/анимации.
 */
enum class KnopaMood {
    /** Нейтральное состояние. */
    IDLE,

    /** Радостное состояние. */
    HAPPY,

    /** Восторженное состояние. */
    EXCITED,

    /** Грустное состояние. */
    SAD,

    /** Удивлённое состояние. */
    SURPRISED
}
