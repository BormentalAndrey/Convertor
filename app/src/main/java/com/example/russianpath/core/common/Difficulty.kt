package com.example.russianpath.core.common

@JvmInline
value class Difficulty(val value: Int) {
    companion object {
        val EASY = Difficulty(1)
        val MEDIUM = Difficulty(5)
        val HARD = Difficulty(10)
    }

    init {
        require(value in 1..10) {
            "Difficulty must be in range 1..10, got $value"
        }
    }
}
