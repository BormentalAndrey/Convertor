// app/src/test/java/com/example/russianpath/core/knowledge/SkillCodeTest.kt

package com.example.russianpath.core.knowledge

import org.junit.Assert.assertEquals
import org.junit.Test

class SkillCodeTest {

    @Test
    fun `all codes are unique`() {
        val codes = SkillCode.entries.map { it.code }
        assertEquals(
            "Duplicate SkillCode found",
            codes.size,
            codes.distinct().size
        )
    }

    @Test
    fun `codes are sorted`() {
        val codes = SkillCode.entries.map { it.code }
        assertEquals(
            "SkillCode codes must be sorted",
            codes.sorted(),
            codes
        )
    }

    @Test
    fun `fromCode finds all entries`() {
        SkillCode.entries.forEach { skill ->
            assertEquals(
                "fromCode(${skill.code}) failed for ${skill.name}",
                skill,
                SkillCode.fromCode(skill.code)
            )
        }
    }

    @Test
    fun `fromCode returns UNKNOWN for invalid code`() {
        assertEquals(
            "fromCode(9999) should return UNKNOWN",
            SkillCode.UNKNOWN,
            SkillCode.fromCode(9999)
        )
    }
}
