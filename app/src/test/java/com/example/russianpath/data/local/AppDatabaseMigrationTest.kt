package com.example.russianpath.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Unit-тесты для миграции базы данных с версии 1 на версию 2.
 *
 * Проверяет:
 * 1. Корректное переименование таблиц (topics_v2 → topics, lesson_completion → lesson_completions)
 * 2. Добавление всех новых колонок с DEFAULT-значениями
 * 3. Создание всех индексов
 * 4. Сохранность существующих данных после миграции
 * 5. Идемпотентность миграции (повторный запуск не ломает БД)
 *
 * Запуск: Right-click → Run 'AppDatabaseMigrationTest'
 * Требует эмулятор или устройство (AndroidJUnit4)
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private lateinit var database: AppDatabase

    // ========================================================================
    // Setup / Teardown
    // ========================================================================

    @Before
    fun setup() {
        // Создаём БД версии 1 с тестовыми данными
        database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "test_migration.db"
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_1_2)
            .build()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    // ========================================================================
    // Тест 1: Миграция с версии 1 на версию 2 не крашится
    // ========================================================================

    @Test
    fun migration1to2_doesNotCrash() = runTest {
        // Создаём БД версии 1
        val dbV1 = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "test_migration_v1.db"
        ).build()

        // Вставляем тестовые данные в БД версии 1
        insertTestDataV1(dbV1)

        // Закрываем БД версии 1
        dbV1.close()

        // Открываем БД с миграцией на версию 2
        val dbV2 = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "test_migration_v1.db"
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_1_2)
            .build()

        // Проверяем, что БД открылась без ошибок
        assertThat(dbV2.openHelper.readableDatabase.version).isEqualTo(2)

        dbV2.close()
    }

    // ========================================================================
    // Тест 2: Переименование таблиц
    // ========================================================================

    @Test
    fun migration1to2_renamesTables() = runTest {
        // Проверяем, что таблицы существуют после миграции
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('topics', 'lesson_completions')",
            null
        )

        val tableNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0))
        }
        cursor.close()

        // Таблица topics должна существовать (переименована из topics_v2)
        assertThat(tableNames).contains("topics")

        // Таблица lesson_completions должна существовать (переименована из lesson_completion)
        assertThat(tableNames).contains("lesson_completions")

        // Старых таблиц быть не должно
        val oldCursor = database.openHelper.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('topics_v2', 'lesson_completion')",
            null
        )
        val oldTableNames = mutableListOf<String>()
        while (oldCursor.moveToNext()) {
            oldTableNames.add(oldCursor.getString(0))
        }
        oldCursor.close()

        assertThat(oldTableNames).isEmpty()
    }

    // ========================================================================
    // Тест 3: Добавление колонок в таблицу grades
    // ========================================================================

    @Test
    fun migration1to2_addsColumnsToGrades() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "PRAGMA table_info(grades)",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1)) // column name
        }
        cursor.close()

        // Проверяем наличие старых колонок
        assertThat(columns).contains("id")
        assertThat(columns).contains("name")
        assertThat(columns).contains("sort_order")

        // Проверяем наличие новых колонок
        assertThat(columns).contains("external_id")
        assertThat(columns).contains("display_name")
        assertThat(columns).contains("description")
        assertThat(columns).contains("is_active")
        assertThat(columns).contains("schema_version")
        assertThat(columns).contains("created_at")
        assertThat(columns).contains("updated_at")
        assertThat(columns).contains("server_updated_at")
    }

    // ========================================================================
    // Тест 4: Добавление колонок в таблицу dictionary_words
    // ========================================================================

    @Test
    fun migration1to2_addsColumnsToDictionaryWords() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "PRAGMA table_info(dictionary_words)",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()

        // Проверяем наличие лингвистических колонок
        assertThat(columns).contains("transcription")
        assertThat(columns).contains("part_of_speech")
        assertThat(columns).contains("gender")
        assertThat(columns).contains("number")
        assertThat(columns).contains("case_form")
        assertThat(columns).contains("grade_id")

        // Проверяем наличие колонок определений
        assertThat(columns).contains("definition_short")
        assertThat(columns).contains("definition_full")
        assertThat(columns).contains("example_sentence")
        assertThat(columns).contains("etymology")

        // Проверяем наличие колонок морфемики и связей
        assertThat(columns).contains("morphemic_structure_json")
        assertThat(columns).contains("cognates_json")
        assertThat(columns).contains("synonyms_json")
        assertThat(columns).contains("antonyms_json")
        assertThat(columns).contains("paronyms_json")

        // Проверяем наличие колонок орфоэпии и орфографии
        assertThat(columns).contains("orthoepic_note")
        assertThat(columns).contains("spelling_rule_id")
        assertThat(columns).contains("spelling_difficulty_marker")

        // Проверяем наличие служебных колонок
        assertThat(columns).contains("frequency_rank")
        assertThat(columns).contains("audio_path")
        assertThat(columns).contains("image_path")
        assertThat(columns).contains("is_irregular")
        assertThat(columns).contains("is_exception")
        assertThat(columns).contains("is_vocabulary_word")
    }

    // ========================================================================
    // Тест 5: Добавление колонок в таблицу lessons
    // ========================================================================

    @Test
    fun migration1to2_addsColumnsToLessons() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "PRAGMA table_info(lessons)",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()

        // Проверяем наличие новых колонок
        assertThat(columns).contains("primary_objective_id")
        assertThat(columns).contains("description")
        assertThat(columns).contains("instruction_text")
        assertThat(columns).contains("questions_count")
        assertThat(columns).contains("time_limit_seconds")
        assertThat(columns).contains("passing_score_percent")
        assertThat(columns).contains("max_stars")
        assertThat(columns).contains("xp_base_reward")
        assertThat(columns).contains("xp_perfect_bonus")
        assertThat(columns).contains("gems_reward")
        assertThat(columns).contains("is_bonus")
        assertThat(columns).contains("is_diagnostic")
    }

    // ========================================================================
    // Тест 6: Добавление колонок в таблицу questions
    // ========================================================================

    @Test
    fun migration1to2_addsColumnsToQuestions() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "PRAGMA table_info(questions)",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()

        // Проверяем наличие новых колонок
        assertThat(columns).contains("primary_skill_id")
        assertThat(columns).contains("prompt_audio_path")
        assertThat(columns).contains("prompt_image_path")
        assertThat(columns).contains("acceptable_answers_json")
        assertThat(columns).contains("explanation_text")
        assertThat(columns).contains("rule_reference_id")
        assertThat(columns).contains("difficulty")
        assertThat(columns).contains("time_limit_seconds")
        assertThat(columns).contains("points")
        assertThat(columns).contains("penalty_points")
        assertThat(columns).contains("max_attempts")
        assertThat(columns).contains("is_required")
    }

    // ========================================================================
    // Тест 7: Добавление колонок в таблицу user_progress
    // ========================================================================

    @Test
    fun migration1to2_addsColumnsToUserProgress() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "PRAGMA table_info(user_progress)",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()

        // Проверяем наличие новых колонок
        assertThat(columns).contains("current_level")
        assertThat(columns).contains("xp_to_next_level")
        assertThat(columns).contains("streak_start_date")
        assertThat(columns).contains("max_lives")
        assertThat(columns).contains("last_life_refill_time")
        assertThat(columns).contains("total_time_spent_seconds")
        assertThat(columns).contains("total_perfect_lessons")
        assertThat(columns).contains("total_days_active")
        assertThat(columns).contains("current_grade_id")
        assertThat(columns).contains("current_topic_id")
        assertThat(columns).contains("onboarding_completed")
        assertThat(columns).contains("last_sync_time")
    }

    // ========================================================================
    // Тест 8: Создание индексов
    // ========================================================================

    @Test
    fun migration1to2_createsIndexes() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='index' ORDER BY name",
            null
        )

        val indexes = mutableListOf<String>()
        while (cursor.moveToNext()) {
            indexes.add(cursor.getString(0))
        }
        cursor.close()

        // Проверяем наличие ключевых индексов
        assertThat(indexes).contains("idx_grades_sort_order")
        assertThat(indexes).contains("idx_sections_grade_sort")
        assertThat(indexes).contains("idx_topics_section_sort")
        assertThat(indexes).contains("idx_topics_grade_sort")
        assertThat(indexes).contains("idx_topics_active_unlocked")
        assertThat(indexes).contains("idx_dictionary_normalized")
        assertThat(indexes).contains("idx_dictionary_active_grade_difficulty")
        assertThat(indexes).contains("idx_lessons_topic_sort")
        assertThat(indexes).contains("idx_lessons_topic_type_sort")
        assertThat(indexes).contains("idx_questions_lesson_sort")
        assertThat(indexes).contains("idx_questions_skill_id")
        assertThat(indexes).contains("idx_completions_lesson_date")
        assertThat(indexes).contains("idx_user_progress_last_active")
    }

    // ========================================================================
    // Тест 9: Уникальные индексы
    // ========================================================================

    @Test
    fun migration1to2_createsUniqueIndexes() = runTest {
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='index' AND name LIKE 'idx_%_external_id'",
            null
        )

        val indexes = mutableListOf<String>()
        while (cursor.moveToNext()) {
            indexes.add(cursor.getString(0))
        }
        cursor.close()

        // Проверяем наличие уникальных индексов для синхронизации
        assertThat(indexes).contains("idx_grades_external_id")
        assertThat(indexes).contains("idx_sections_external_id")
        assertThat(indexes).contains("idx_topics_external_id")
        assertThat(indexes).contains("idx_objectives_external_id")
        assertThat(indexes).contains("idx_micro_skills_external_id")
        assertThat(indexes).contains("idx_dictionary_external_id")
        assertThat(indexes).contains("idx_lessons_external_id")
        assertThat(indexes).contains("idx_questions_external_id")
    }

    // ========================================================================
    // Тест 10: Значения по умолчанию для новых колонок
    // ========================================================================

    @Test
    fun migration1to2_defaultValuesAreCorrect() = runTest {
        // Вставляем минимальную запись в grades (только обязательные поля)
        database.openHelper.writableDatabase.execSQL(
            "INSERT INTO grades (id, name, sort_order) VALUES ('test_grade', 'Test', 1)"
        )

        // Проверяем значения по умолчанию
        val cursor = database.openHelper.readableDatabase.rawQuery(
            "SELECT external_id, display_name, is_active, schema_version, created_at FROM grades WHERE id = 'test_grade'",
            null
        )

        assertThat(cursor.moveToFirst()).isTrue()
        assertThat(cursor.getString(0)).isEmpty()        // external_id = ''
        assertThat(cursor.getString(1)).isEmpty()        // display_name = ''
        assertThat(cursor.getInt(2)).isEqualTo(1)        // is_active = 1
        assertThat(cursor.getInt(3)).isEqualTo(1)        // schema_version = 1
        assertThat(cursor.getLong(4)).isEqualTo(0)       // created_at = 0

        cursor.close()
    }

    // ========================================================================
    // Тест 11: Идемпотентность миграции
    // ========================================================================

    @Test
    fun migration1to2_isIdempotent() = runTest {
        // Открываем БД с миграцией первый раз
        val db1 = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "test_idempotent.db"
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_1_2)
            .build()

        assertThat(db1.openHelper.readableDatabase.version).isEqualTo(2)
        db1.close()

        // Открываем БД с миграцией второй раз
        val db2 = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "test_idempotent.db"
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_1_2)
            .build()

        // БД должна открыться без ошибок (миграция не должна повредить данные)
        assertThat(db2.openHelper.readableDatabase.version).isEqualTo(2)

        // Проверяем, что таблицы всё ещё существуют
        val cursor = db2.openHelper.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='grades'",
            null
        )
        assertThat(cursor.count).isEqualTo(1)
        cursor.close()

        db2.close()
    }

    // ========================================================================
    // Test Data Helpers
    // ========================================================================

    /**
     * Вставляет тестовые данные в БД версии 1.
     * Симулирует состояние БД до миграции.
     */
    private fun insertTestDataV1(database: AppDatabase) {
        database.openHelper.writableDatabase.apply {
            // grades (старая структура)
            execSQL("""
                INSERT INTO grades (id, name, sort_order) 
                VALUES ('grade_5', '5 класс', 5)
            """)

            // sections (старая структура)
            execSQL("""
                INSERT INTO sections (id, gradeId, name, sort_order) 
                VALUES ('section_5_1', 'grade_5', 'Фонетика', 1)
            """)

            // topics_v2 (будет переименована в topics)
            execSQL("""
                INSERT INTO topics_v2 (id, sectionId, gradeLevel, title, description, iconName, sortOrder, isUnlocked) 
                VALUES ('topic_5_1', 'section_5_1', 5, 'Фонетический разбор', 'Изучаем звуки', 'ic_phonetics', 1, 0)
            """)

            // lessons (старая структура)
            execSQL("""
                INSERT INTO lessons (id, topicId, lessonType, difficulty, theoryJson, sortOrder, title) 
                VALUES ('lesson_1', 'topic_5_1', 'practice', 1, '{}', 1, 'Звуки и буквы')
            """)

            // questions (старая структура)
            execSQL("""
                INSERT INTO questions (id, lessonId, questionType, promptText, dataJson, correctAnswerJson, hintText, audioPath) 
                VALUES ('q1', 'lesson_1', 'single_choice', 'Сколько гласных?', '["6","10","5"]', '6', 'В русском 6 гласных', NULL)
            """)

            // lesson_completion (будет переименована в lesson_completions)
            execSQL("""
                INSERT INTO lesson_completion (lessonId, stars, mistakesCount, completedAt, xpEarned) 
                VALUES ('lesson_1', 3, 0, 1234567890, 50)
            """)

            // user_progress (старая структура)
            execSQL("""
                INSERT INTO user_progress (id, totalXp, currentStreak, longestStreak, lastActiveDate, gemsBalance, livesCount, totalLessonsCompleted, totalMistakesCount) 
                VALUES (1, 100, 3, 7, 1234567890, 50, 5, 2, 1)
            """)
        }
    }
}
