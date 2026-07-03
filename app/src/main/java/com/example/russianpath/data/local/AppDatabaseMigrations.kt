package com.example.russianpath.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Миграции базы данных приложения "Русский Путь".
 *
 * Каждая миграция должна быть протестирована на копии production-данных
 * перед релизом. Используйте `exportSchema = true` в @Database для генерации
 * JSON-схем, по которым можно валидировать миграции.
 *
 * История версий:
 * - Version 1: Начальная схема (topics_v2, lesson_completion)
 * - Version 2: Полная переработка схемы
 *   - Переименование таблиц: topics_v2 → topics, lesson_completion → lesson_completions
 *   - Добавление полей синхронизации во все таблицы
 *   - Добавление полей для образовательной аналитики
 *   - Создание составных индексов для ускорения запросов
 */
object AppDatabaseMigrations {

    /**
     * Миграция с версии 1 на версию 2.
     *
     * Выполняет:
     * 1. Переименование устаревших таблиц
     * 2. Добавление новых колонок (все NOT NULL с DEFAULT)
     * 3. Создание всех индексов
     *
     * Использует try/catch для идемпотентности:
     * если колонка уже существует, ALTER TABLE ADD COLUMN выбросит исключение,
     * которое мы безопасно игнорируем.
     */
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // ============================================================
            // grades
            // ============================================================
            migrateGrades(database)

            // ============================================================
            // sections
            // ============================================================
            migrateSections(database)

            // ============================================================
            // topics (бывшая topics_v2)
            // ============================================================
            migrateTopics(database)

            // ============================================================
            // learning_objectives
            // ============================================================
            migrateLearningObjectives(database)

            // ============================================================
            // micro_skills
            // ============================================================
            migrateMicroSkills(database)

            // ============================================================
            // dictionary_words
            // ============================================================
            migrateDictionaryWords(database)

            // ============================================================
            // lessons
            // ============================================================
            migrateLessons(database)

            // ============================================================
            // questions
            // ============================================================
            migrateQuestions(database)

            // ============================================================
            // lesson_completion → lesson_completions
            // ============================================================
            migrateLessonCompletions(database)

            // ============================================================
            // user_progress
            // ============================================================
            migrateUserProgress(database)
        }
    }

    // ========================================================================
    // Приватные методы миграции для каждой таблицы
    // ========================================================================

    private fun migrateGrades(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "grades", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "grades", "display_name", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "grades", "description", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "grades", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "grades", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "grades", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "grades", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "grades", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(database, "idx_grades_sort_order", "grades", "sort_order")
        createUniqueIndexSafely(database, "idx_grades_external_id", "grades", "external_id")
    }

    private fun migrateSections(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "sections", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "sections", "display_name", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "sections", "description", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "sections", "icon_name", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "sections", "color_hex", "TEXT NOT NULL DEFAULT '#FF6200EE'")
        addColumnSafely(database, "sections", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "sections", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "sections", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "sections", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "sections", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(database, "idx_sections_grade_sort", "sections", "grade_id, sort_order")
        createUniqueIndexSafely(database, "idx_sections_external_id", "sections", "external_id")
        createIndexSafely(database, "idx_sections_grade_id", "sections", "grade_id")
    }

    private fun migrateTopics(database: SupportSQLiteDatabase) {
        // Пытаемся переименовать topics_v2 → topics
        renameTableSafely(database, "topics_v2", "topics")

        addColumnSafely(database, "topics", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "topics", "grade_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "topics", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "topics", "difficulty_level", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "topics", "estimated_minutes", "INTEGER NOT NULL DEFAULT 15")
        addColumnSafely(
            database, "topics",
            "prerequisite_topic_ids_json", "TEXT NOT NULL DEFAULT '[]'"
        )
        addColumnSafely(database, "topics", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "topics", "content_hash", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "topics", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "topics", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "topics", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(database, "idx_topics_section_sort", "topics", "section_id, sort_order")
        createIndexSafely(database, "idx_topics_grade_sort", "topics", "grade_id, sort_order")
        createUniqueIndexSafely(database, "idx_topics_external_id", "topics", "external_id")
        createIndexSafely(database, "idx_topics_section_id", "topics", "section_id")
        createIndexSafely(database, "idx_topics_grade_id", "topics", "grade_id")
        createIndexSafely(database, "idx_topics_active_unlocked", "topics", "is_active, is_unlocked")
    }

    private fun migrateLearningObjectives(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "learning_objectives", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "learning_objectives", "skill_code_id", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "learning_objectives",
            "prerequisite_objective_ids_json", "TEXT NOT NULL DEFAULT '[]'"
        )
        addColumnSafely(
            database, "learning_objectives",
            "bloom_taxonomy_level", "INTEGER NOT NULL DEFAULT 1"
        )
        addColumnSafely(
            database, "learning_objectives",
            "mastery_threshold_percent", "INTEGER NOT NULL DEFAULT 80"
        )
        addColumnSafely(database, "learning_objectives", "is_required", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "learning_objectives", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "learning_objectives", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "learning_objectives", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "learning_objectives", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "learning_objectives",
            "server_updated_at", "INTEGER NOT NULL DEFAULT 0"
        )

        createIndexSafely(
            database, "idx_objectives_topic_sort",
            "learning_objectives", "topic_id, sort_order"
        )
        createUniqueIndexSafely(
            database, "idx_objectives_external_id",
            "learning_objectives", "external_id"
        )
        createIndexSafely(
            database, "idx_objectives_topic_id",
            "learning_objectives", "topic_id"
        )
        createIndexSafely(
            database, "idx_objectives_skill_code",
            "learning_objectives", "skill_code_id"
        )
    }

    private fun migrateMicroSkills(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "micro_skills", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "micro_skills",
            "parent_micro_skill_id", "TEXT NOT NULL DEFAULT ''"
        )
        addColumnSafely(database, "micro_skills", "difficulty_level", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "micro_skills", "error_category", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "micro_skills",
            "typical_mistake_pattern_json", "TEXT NOT NULL DEFAULT '[]'"
        )
        addColumnSafely(database, "micro_skills", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "micro_skills", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "micro_skills", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "micro_skills", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "micro_skills", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(
            database, "idx_micro_skills_objective_sort",
            "micro_skills", "objective_id, sort_order"
        )
        createIndexSafely(
            database, "idx_micro_skills_skill_code",
            "micro_skills", "skill_code_id"
        )
        createUniqueIndexSafely(
            database, "idx_micro_skills_external_id",
            "micro_skills", "external_id"
        )
        createIndexSafely(
            database, "idx_micro_skills_objective_id",
            "micro_skills", "objective_id"
        )
    }

    private fun migrateDictionaryWords(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "dictionary_words", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "transcription", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "part_of_speech", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "gender", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "number", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "case_form", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "grade_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "definition_short", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "definition_full", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "example_sentence", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "etymology", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "dictionary_words",
            "morphemic_structure_json", "TEXT NOT NULL DEFAULT '{}'"
        )
        addColumnSafely(database, "dictionary_words", "cognates_json", "TEXT NOT NULL DEFAULT '[]'")
        addColumnSafely(database, "dictionary_words", "synonyms_json", "TEXT NOT NULL DEFAULT '[]'")
        addColumnSafely(database, "dictionary_words", "antonyms_json", "TEXT NOT NULL DEFAULT '[]'")
        addColumnSafely(database, "dictionary_words", "paronyms_json", "TEXT NOT NULL DEFAULT '[]'")
        addColumnSafely(database, "dictionary_words", "orthoepic_note", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "spelling_rule_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "dictionary_words",
            "spelling_difficulty_marker", "TEXT NOT NULL DEFAULT ''"
        )
        addColumnSafely(database, "dictionary_words", "frequency_rank", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "dictionary_words", "audio_path", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "image_path", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "dictionary_words", "is_irregular", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "dictionary_words", "is_exception", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "dictionary_words",
            "is_vocabulary_word", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(database, "dictionary_words", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "dictionary_words", "schema_version", "INTEGER NOT NULL DEFAULT 2")
        addColumnSafely(database, "dictionary_words", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "dictionary_words", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "dictionary_words",
            "server_updated_at", "INTEGER NOT NULL DEFAULT 0"
        )

        createIndexSafely(
            database, "idx_dictionary_part_of_speech",
            "dictionary_words", "part_of_speech"
        )
        createUniqueIndexSafely(
            database, "idx_dictionary_external_id",
            "dictionary_words", "external_id"
        )
        createIndexSafely(
            database, "idx_dictionary_active_grade_difficulty",
            "dictionary_words", "is_active, grade_id, difficulty"
        )
        createIndexSafely(
            database, "idx_dictionary_normalized_grade",
            "dictionary_words", "normalized, grade_id"
        )
    }

    private fun migrateLessons(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "lessons", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lessons", "primary_objective_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lessons", "description", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lessons", "instruction_text", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lessons", "questions_count", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lessons", "time_limit_seconds", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "lessons",
            "passing_score_percent", "INTEGER NOT NULL DEFAULT 70"
        )
        addColumnSafely(database, "lessons", "max_stars", "INTEGER NOT NULL DEFAULT 3")
        addColumnSafely(database, "lessons", "xp_base_reward", "INTEGER NOT NULL DEFAULT 50")
        addColumnSafely(database, "lessons", "xp_perfect_bonus", "INTEGER NOT NULL DEFAULT 25")
        addColumnSafely(database, "lessons", "gems_reward", "INTEGER NOT NULL DEFAULT 5")
        addColumnSafely(database, "lessons", "is_bonus", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lessons", "is_diagnostic", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lessons", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "lessons", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "lessons", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lessons", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lessons", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(database, "idx_lessons_topic_sort", "lessons", "topic_id, sort_order")
        createIndexSafely(
            database, "idx_lessons_topic_type_sort",
            "lessons", "topic_id, lesson_type, sort_order"
        )
        createUniqueIndexSafely(database, "idx_lessons_external_id", "lessons", "external_id")
        createIndexSafely(database, "idx_lessons_topic_id", "lessons", "topic_id")
        createIndexSafely(
            database, "idx_lessons_objective_id",
            "lessons", "primary_objective_id"
        )
        createIndexSafely(database, "idx_lessons_active", "lessons", "is_active")
    }

    private fun migrateQuestions(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "questions", "external_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "questions", "primary_skill_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "questions", "prompt_audio_path", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "questions", "prompt_image_path", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "questions",
            "acceptable_answers_json", "TEXT NOT NULL DEFAULT '[]'"
        )
        addColumnSafely(database, "questions", "explanation_text", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "questions", "rule_reference_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "questions", "difficulty", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "questions", "time_limit_seconds", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "questions", "points", "INTEGER NOT NULL DEFAULT 10")
        addColumnSafely(database, "questions", "penalty_points", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "questions", "max_attempts", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "questions", "is_required", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "questions", "is_active", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "questions", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "questions", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "questions", "updated_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "questions", "server_updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(
            database, "idx_questions_lesson_sort",
            "questions", "lesson_id, sort_order"
        )
        createIndexSafely(
            database, "idx_questions_lesson_type",
            "questions", "lesson_id, question_type"
        )
        createUniqueIndexSafely(database, "idx_questions_external_id", "questions", "external_id")
        createIndexSafely(database, "idx_questions_lesson_id", "questions", "lesson_id")
        createIndexSafely(database, "idx_questions_skill_id", "questions", "primary_skill_id")
        createIndexSafely(database, "idx_questions_type", "questions", "question_type")
        createIndexSafely(database, "idx_questions_difficulty", "questions", "difficulty")
    }

    private fun migrateLessonCompletions(database: SupportSQLiteDatabase) {
        // Переименовываем старую таблицу
        renameTableSafely(database, "lesson_completion", "lesson_completions")

        addColumnSafely(database, "lesson_completions", "id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lesson_completions", "topic_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "lesson_completions", "score_percent", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lesson_completions", "correct_answers", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lesson_completions", "total_questions", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(
            database, "lesson_completions",
            "mistakes_json", "TEXT NOT NULL DEFAULT '[]'"
        )
        addColumnSafely(
            database, "lesson_completions",
            "time_spent_seconds", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(database, "lesson_completions", "gems_earned", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lesson_completions", "attempt_number", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "lesson_completions", "is_passed", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "lesson_completions", "device_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "lesson_completions",
            "schema_version", "INTEGER NOT NULL DEFAULT 1"
        )
        addColumnSafely(database, "lesson_completions", "synced_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(
            database, "idx_completions_lesson_date",
            "lesson_completions", "lesson_id, completed_at"
        )
        createIndexSafely(
            database, "idx_completions_date",
            "lesson_completions", "completed_at"
        )
        createIndexSafely(
            database, "idx_completions_lesson_id",
            "lesson_completions", "lesson_id"
        )
        createIndexSafely(
            database, "idx_completions_topic_date",
            "lesson_completions", "topic_id, completed_at"
        )
    }

    private fun migrateUserProgress(database: SupportSQLiteDatabase) {
        addColumnSafely(database, "user_progress", "current_level", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "user_progress", "xp_to_next_level", "INTEGER NOT NULL DEFAULT 100")
        addColumnSafely(database, "user_progress", "streak_start_date", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "user_progress", "max_lives", "INTEGER NOT NULL DEFAULT 5")
        addColumnSafely(
            database, "user_progress",
            "last_life_refill_time", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(
            database, "user_progress",
            "total_time_spent_seconds", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(
            database, "user_progress",
            "total_perfect_lessons", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(database, "user_progress", "total_days_active", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "user_progress", "current_grade_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(database, "user_progress", "current_topic_id", "TEXT NOT NULL DEFAULT ''")
        addColumnSafely(
            database, "user_progress",
            "onboarding_completed", "INTEGER NOT NULL DEFAULT 0"
        )
        addColumnSafely(database, "user_progress", "last_sync_time", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "user_progress", "schema_version", "INTEGER NOT NULL DEFAULT 1")
        addColumnSafely(database, "user_progress", "created_at", "INTEGER NOT NULL DEFAULT 0")
        addColumnSafely(database, "user_progress", "updated_at", "INTEGER NOT NULL DEFAULT 0")

        createIndexSafely(
            database, "idx_user_progress_last_active",
            "user_progress", "last_active_date"
        )
    }

    // ========================================================================
    // Утилиты миграции
    // ========================================================================

    /**
     * Безопасно добавляет колонку к таблице.
     * Если колонка уже существует — исключение игнорируется.
     */
    private fun addColumnSafely(
        database: SupportSQLiteDatabase,
        table: String,
        column: String,
        definition: String
    ) {
        try {
            database.execSQL("ALTER TABLE $table ADD COLUMN $column $definition")
        } catch (_: Exception) {
            // Колонка уже существует — это нормально при повторной миграции
        }
    }

    /**
     * Безопасно создаёт индекс.
     * Использует IF NOT EXISTS для идемпотентности.
     */
    private fun createIndexSafely(
        database: SupportSQLiteDatabase,
        indexName: String,
        table: String,
        columns: String
    ) {
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS $indexName ON $table($columns)"
        )
    }

    /**
     * Безопасно создаёт уникальный индекс.
     */
    private fun createUniqueIndexSafely(
        database: SupportSQLiteDatabase,
        indexName: String,
        table: String,
        columns: String
    ) {
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS $indexName ON $table($columns)"
        )
    }

    /**
     * Безопасно переименовывает таблицу.
     * Если таблица не существует или уже переименована — исключение игнорируется.
     */
    private fun renameTableSafely(
        database: SupportSQLiteDatabase,
        oldName: String,
        newName: String
    ) {
        try {
            database.execSQL("ALTER TABLE $oldName RENAME TO $newName")
        } catch (_: Exception) {
            // Таблица не существует или уже переименована
        }
    }
}
