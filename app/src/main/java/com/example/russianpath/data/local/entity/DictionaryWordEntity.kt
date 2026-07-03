package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Словарное слово с полной лингвистической информацией.
 * Центральная таблица для словаря, интервального повторения и генерации упражнений.
 * Рассчитана на десятки тысяч слов.
 */
@Entity(
    tableName = "dictionary_words",
    indices = [
        Index(
            value = ["normalized"],
            name = "idx_dictionary_normalized"
        ),
        Index(
            value = ["grade_id"],
            name = "idx_dictionary_grade_id"
        ),
        Index(
            value = ["difficulty"],
            name = "idx_dictionary_difficulty"
        ),
        Index(
            value = ["part_of_speech"],
            name = "idx_dictionary_part_of_speech"
        ),
        Index(
            value = ["external_id"],
            name = "idx_dictionary_external_id",
            unique = true
        ),
        Index(
            value = ["is_active", "grade_id", "difficulty"],
            name = "idx_dictionary_active_grade_difficulty"
        ),
        Index(
            value = ["normalized", "grade_id"],
            name = "idx_dictionary_normalized_grade"
        )
    ]
)
data class DictionaryWordEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "word", defaultValue = "")
    val word: String,

    @ColumnInfo(name = "normalized", defaultValue = "")
    val normalized: String,

    @ColumnInfo(name = "transcription", defaultValue = "")
    val transcription: String = "",

    @ColumnInfo(name = "part_of_speech", defaultValue = "")
    val partOfSpeech: String = "",

    @ColumnInfo(name = "gender", defaultValue = "")
    val gender: String = "",

    @ColumnInfo(name = "number", defaultValue = "")
    val number: String = "",

    @ColumnInfo(name = "case_form", defaultValue = "")
    val caseForm: String = "",

    @ColumnInfo(name = "grade_id", defaultValue = "")
    val gradeId: String = "",

    @ColumnInfo(name = "difficulty", defaultValue = "1")
    val difficulty: Int,

    @ColumnInfo(name = "definition_short", defaultValue = "")
    val definitionShort: String = "",

    @ColumnInfo(name = "definition_full", defaultValue = "")
    val definitionFull: String = "",

    @ColumnInfo(name = "example_sentence", defaultValue = "")
    val exampleSentence: String = "",

    @ColumnInfo(name = "etymology", defaultValue = "")
    val etymology: String = "",

    @ColumnInfo(name = "morphemic_structure_json", defaultValue = "{}")
    val morphemicStructureJson: String = "{}",

    @ColumnInfo(name = "cognates_json", defaultValue = "[]")
    val cognatesJson: String = "[]",

    @ColumnInfo(name = "synonyms_json", defaultValue = "[]")
    val synonymsJson: String = "[]",

    @ColumnInfo(name = "antonyms_json", defaultValue = "[]")
    val antonymsJson: String = "[]",

    @ColumnInfo(name = "paronyms_json", defaultValue = "[]")
    val paronymsJson: String = "[]",

    @ColumnInfo(name = "orthoepic_note", defaultValue = "")
    val orthoepicNote: String = "",

    @ColumnInfo(name = "spelling_rule_id", defaultValue = "")
    val spellingRuleId: String = "",

    @ColumnInfo(name = "spelling_difficulty_marker", defaultValue = "")
    val spellingDifficultyMarker: String = "",

    @ColumnInfo(name = "frequency_rank", defaultValue = "0")
    val frequencyRank: Int = 0,

    @ColumnInfo(name = "tags_json", defaultValue = "[]")
    val tagsJson: String = "[]",

    @ColumnInfo(name = "audio_path", defaultValue = "")
    val audioPath: String = "",

    @ColumnInfo(name = "image_path", defaultValue = "")
    val imagePath: String = "",

    @ColumnInfo(name = "is_irregular", defaultValue = "0")
    val isIrregular: Boolean = false,

    @ColumnInfo(name = "is_exception", defaultValue = "0")
    val isException: Boolean = false,

    @ColumnInfo(name = "is_vocabulary_word", defaultValue = "0")
    val isVocabularyWord: Boolean = false,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "schema_version", defaultValue = "2")
    val schemaVersion: Int = 2,

    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L,

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    val updatedAt: Long = 0L,

    @ColumnInfo(name = "server_updated_at", defaultValue = "0")
    val serverUpdatedAt: Long = 0L
)
