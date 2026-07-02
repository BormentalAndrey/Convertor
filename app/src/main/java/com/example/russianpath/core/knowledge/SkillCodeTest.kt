// app/src/main/java/com/example/russianpath/core/knowledge/SkillCodeTest.kt
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
    fun `all keys are stable and unique`() {
        val keys = SkillCode.entries.map { it.key }
        assertEquals(
            "Duplicate keys found",
            keys.size,
            keys.distinct().size
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

    @Test(expected = IllegalArgumentException::class)
    fun `fromCode throws on unknown code`() {
        SkillCode.fromCode(9999)
    }
}
