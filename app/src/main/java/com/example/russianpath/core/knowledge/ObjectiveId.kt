package com.example.russianpath.core.knowledge

/**
 * Value Object для идентификатора цели обучения.
 *
 * Оборачивает строковый ID в типобезопасную обёртку.
 * Предотвращает перепутывание ID разных сущностей.
 */
@JvmInline
value class ObjectiveId(val value: String) {

    init {
        require(value.isNotBlank()) {
            "ObjectiveId cannot be blank"
        }
    }

    override fun toString(): String = value
}
