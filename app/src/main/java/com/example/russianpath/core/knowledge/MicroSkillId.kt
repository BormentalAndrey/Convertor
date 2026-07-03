package com.example.russianpath.core.knowledge

/**
 * Value Object для идентификатора микро-навыка.
 *
 * Оборачивает строковый ID в типобезопасную обёртку.
 */
@JvmInline
value class MicroSkillId(val value: String) {

    init {
        require(value.isNotBlank()) {
            "MicroSkillId cannot be blank"
        }
    }

    override fun toString(): String = value
}
