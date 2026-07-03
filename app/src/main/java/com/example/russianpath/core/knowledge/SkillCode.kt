package com.example.russianpath.core.knowledge

enum class SkillCode(val code: Int, val key: String) {
    FIND_FIRST_LETTER(1001, "find_first_letter"),
    FIND_LAST_LETTER(1002, "find_last_letter"),
    COUNT_LETTERS(1003, "count_letters"),
    RECOGNIZE_SOFT_SIGN(1004, "recognize_soft_sign"),
    RECOGNIZE_HARD_SIGN(1005, "recognize_hard_sign"),

    COUNT_SYLLABLES(2001, "count_syllables"),
    DIVIDE_TO_SYLLABLES(2002, "divide_to_syllables"),
    FIND_STRESSED_SYLLABLE(2003, "find_stressed_syllable"),
    COUNT_VOWELS(2004, "count_vowels"),
    COUNT_CONSONANTS(2005, "count_consonants"),

    SPELLING_ZHI_SHI(3001, "spelling_zhi_shi"),
    SPELLING_CHA_SCHA(3002, "spelling_cha_scha"),
    SPELLING_CHU_SCHU(3003, "spelling_chu_schu"),

    FIND_ROOT(4001, "find_root"),
    FIND_PREFIX(4002, "find_prefix"),
    FIND_SUFFIX(4003, "find_suffix"),
    FIND_ENDING(4004, "find_ending");

    companion object {
        fun fromCode(code: Int): SkillCode {
            return entries.first { it.code == code }
        }
    }
}
