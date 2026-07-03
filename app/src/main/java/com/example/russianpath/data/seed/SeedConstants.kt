package com.example.russianpath.data.seed

/**
 * Константы путей к seed-файлам в assets/seed/.
 *
 * Все пути используют единый корень ROOT для лёгкой смены директории.
 * Используется SeedLoader, ManifestLoader и DatabaseSeeder.
 */
object SeedConstants {

    /** Корневая директория seed-файлов в assets. */
    const val ROOT = "seed"

    /** Путь к манифесту контента. */
    const val MANIFEST = "$ROOT/manifest.json"

    /** Путь к файлу классов обучения. */
    const val GRADES = "$ROOT/grades.json"

    /** Путь к файлу разделов. */
    const val SECTIONS = "$ROOT/sections.json"

    /** Путь к файлу тем. */
    const val TOPICS = "$ROOT/topics.json"

    /** Путь к файлу целей обучения. */
    const val OBJECTIVES = "$ROOT/objectives.json"

    /** Путь к файлу микро-навыков. */
    const val MICRO_SKILLS = "$ROOT/micro_skills.json"

    /** Путь к файлу словарных слов. */
    const val DICTIONARY = "$ROOT/dictionary.json"

    /** Путь к файлу уроков. */
    const val LESSONS = "$ROOT/lessons.json"

    /** Путь к файлу вопросов. */
    const val QUESTIONS = "$ROOT/questions.json"

    /** Путь к файлу правил орфографии. */
    const val RULES = "$ROOT/rules.json"

    /** Путь к файлу слов-исключений. */
    const val EXCEPTIONS = "$ROOT/exceptions.json"

    /** Путь к файлу завершений уроков (для тестового сидирования). */
    const val LESSON_COMPLETIONS = "$ROOT/lesson_completions.json"

    /** Путь к файлу прогресса пользователя (для тестового сидирования). */
    const val USER_PROGRESS = "$ROOT/user_progress.json"
}
