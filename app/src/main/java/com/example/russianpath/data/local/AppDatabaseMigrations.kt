package com.example.russianpath.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Миграции базы данных приложения "Русский Путь".
 * Каждая миграция должна быть протестирована на копии production-данных.
 */
object AppDatabaseMigrations {

    /**
     * Миграция с версии 1 на версию 2.
     *
     * Изменения:
     * - Переименование lesson_completion → lesson_completions
     * - Добавление новых колонок во все таблицы
     * - Создание недостающих индексов
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // ============================================================
            // grades
            // ============================================================
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN display_name TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN description TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                ALTER TABLE grades ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_grades_sort_order ON grades(sort_order)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_grades_external_id ON grades(external_id)
            """)

            // ============================================================
            // sections
            // ============================================================
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN display_name TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN description TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN icon_name TEXT NOT NULL DEFAULT ''
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN color_hex TEXT NOT NULL DEFAULT '#FF6200EE'
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                ALTER TABLE sections ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_sections_grade_sort ON sections(grade_id, sort_order)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_sections_external_id ON sections(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_sections_grade_id ON sections(grade_id)
            """)

            // ============================================================
            // topics (бывшая topics_v2 — обрабатываем оба случая)
            // ============================================================
            // Пытаемся переименовать topics_v2 → topics, если она существует
            try {
                database.execSQL("ALTER TABLE topics_v2 RENAME TO topics")
            } catch (_: Exception) {
                // Таблицы topics_v2 может не быть, если это чистая установка v2
            }
            // Если таблица topics уже существует (v1), добавляем колонки
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN grade_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN difficulty_level INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN estimated_minutes INTEGER NOT NULL DEFAULT 15
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN prerequisite_topic_ids_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN content_hash TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE topics ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_topics_section_sort ON topics(section_id, sort_order)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_topics_grade_sort ON topics(grade_id, sort_order)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_topics_external_id ON topics(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_topics_section_id ON topics(section_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_topics_grade_id ON topics(grade_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_topics_active_unlocked ON topics(is_active, is_unlocked)
            """)

            // ============================================================
            // learning_objectives
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN skill_code_id INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN prerequisite_objective_ids_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN bloom_taxonomy_level INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN mastery_threshold_percent INTEGER NOT NULL DEFAULT 80
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN is_required INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE learning_objectives ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_objectives_topic_sort ON learning_objectives(topic_id, sort_order)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_objectives_external_id ON learning_objectives(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_objectives_topic_id ON learning_objectives(topic_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_objectives_skill_code ON learning_objectives(skill_code_id)
            """)

            // ============================================================
            // micro_skills
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN parent_micro_skill_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN difficulty_level INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN error_category TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN typical_mistake_pattern_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE micro_skills ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_micro_skills_objective_sort ON micro_skills(objective_id, sort_order)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_micro_skills_skill_code ON micro_skills(skill_code_id)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_micro_skills_external_id ON micro_skills(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_micro_skills_objective_id ON micro_skills(objective_id)
            """)

            // ============================================================
            // dictionary_words
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN transcription TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN part_of_speech TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN gender TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN number TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN case_form TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN grade_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN definition_short TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN definition_full TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN example_sentence TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN etymology TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN morphemic_structure_json TEXT NOT NULL DEFAULT '{}'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN cognates_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN synonyms_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN antonyms_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN paronyms_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN orthoepic_note TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN spelling_rule_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN spelling_difficulty_marker TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN frequency_rank INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN audio_path TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN image_path TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN is_irregular INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN is_exception INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN is_vocabulary_word INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 2
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE dictionary_words ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_dictionary_part_of_speech ON dictionary_words(part_of_speech)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_dictionary_external_id ON dictionary_words(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_dictionary_active_grade_difficulty ON dictionary_words(is_active, grade_id, difficulty)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_dictionary_normalized_grade ON dictionary_words(normalized, grade_id)
            """)

            // ============================================================
            // lessons
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN primary_objective_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN description TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN instruction_text TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN questions_count INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN time_limit_seconds INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN passing_score_percent INTEGER NOT NULL DEFAULT 70
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN max_stars INTEGER NOT NULL DEFAULT 3
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN xp_base_reward INTEGER NOT NULL DEFAULT 50
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN xp_perfect_bonus INTEGER NOT NULL DEFAULT 25
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN gems_reward INTEGER NOT NULL DEFAULT 5
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN is_bonus INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN is_diagnostic INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lessons ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_lessons_topic_sort ON lessons(topic_id, sort_order)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_lessons_topic_type_sort ON lessons(topic_id, lesson_type, sort_order)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_lessons_external_id ON lessons(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_lessons_topic_id ON lessons(topic_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_lessons_objective_id ON lessons(primary_objective_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_lessons_active ON lessons(is_active)
            """)

            // ============================================================
            // questions
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN external_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN primary_skill_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN prompt_audio_path TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN prompt_image_path TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN acceptable_answers_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN explanation_text TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN rule_reference_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN difficulty INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN time_limit_seconds INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN points INTEGER NOT NULL DEFAULT 10
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN penalty_points INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN max_attempts INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN is_required INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE questions ADD COLUMN server_updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_lesson_sort ON questions(lesson_id, sort_order)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_lesson_type ON questions(lesson_id, question_type)
            """)
            database.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_questions_external_id ON questions(external_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_lesson_id ON questions(lesson_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_skill_id ON questions(primary_skill_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_type ON questions(question_type)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions(difficulty)
            """)

            // ============================================================
            // lesson_completion → lesson_completions
            // ============================================================
            // Пытаемся переименовать старую таблицу
            try {
                database.execSQL("ALTER TABLE lesson_completion RENAME TO lesson_completions")
            } catch (_: Exception) {
                // Таблицы lesson_completion может не быть
            }
            // Если таблица lesson_completions уже существует (v2), добавляем колонки
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN topic_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN score_percent INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN correct_answers INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN total_questions INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN mistakes_json TEXT NOT NULL DEFAULT '[]'
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN time_spent_seconds INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN gems_earned INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN attempt_number INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN is_passed INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN device_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE lesson_completions ADD COLUMN synced_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_completions_lesson_date ON lesson_completions(lesson_id, completed_at)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_completions_date ON lesson_completions(completed_at)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_completions_lesson_id ON lesson_completions(lesson_id)
            """)
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_completions_topic_date ON lesson_completions(topic_id, completed_at)
            """)

            // ============================================================
            // user_progress
            // ============================================================
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN current_level INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN xp_to_next_level INTEGER NOT NULL DEFAULT 100
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN streak_start_date INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN max_lives INTEGER NOT NULL DEFAULT 5
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN last_life_refill_time INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN total_time_spent_seconds INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN total_perfect_lessons INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN total_days_active INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN current_grade_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN current_topic_id TEXT NOT NULL DEFAULT ''
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN onboarding_completed INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN last_sync_time INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN schema_version INTEGER NOT NULL DEFAULT 1
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            try {
                database.execSQL("""
                    ALTER TABLE user_progress ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0
                """)
            } catch (_: Exception) { /* колонка уже существует */ }
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_user_progress_last_active ON user_progress(last_active_date)
            """)
        }
    }
}
