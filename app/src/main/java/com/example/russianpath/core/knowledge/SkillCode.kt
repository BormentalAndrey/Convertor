package com.example.russianpath.core.knowledge

enum class SkillCode(val code: Int) {

    // 1xxx: Графика
    FIND_FIRST_LETTER(1001),
    FIND_LAST_LETTER(1002),
    COUNT_LETTERS(1003),
    RECOGNIZE_SOFT_SIGN(1101),
    RECOGNIZE_HARD_SIGN(1102),

    // 2xxx: Фонетика
    COUNT_SYLLABLES(2001),
    DIVIDE_TO_SYLLABLES(2002),
    FIND_STRESSED_SYLLABLE(2101),
    COUNT_VOWELS(2201),
    COUNT_CONSONANTS(2202),

    // 3xxx: Орфография
    SPELLING_ZHI_SHI(3001),
    SPELLING_CHA_SCHA(3002),
    SPELLING_CHU_SCHU(3003),

    // 4xxx: Морфемика
    FIND_ROOT(4001),
    FIND_PREFIX(4101),
    FIND_SUFFIX(4201),
    FIND_ENDING(4301);

    val key: String = "skill.$code"

    companion object {
        private val byCode: Map<Int, SkillCode> = entries.associateBy { it.code }

        fun fromCode(code: Int): SkillCode =
            byCode[code] ?: throw IllegalArgumentException(
                "Unknown SkillCode: $code. Database or application version mismatch."
            )
    }
}
