package com.example.russianpath.data.seed

import android.util.Log
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.SectionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import com.example.russianpath.data.local.entity.GradeEntity
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.local.entity.SectionEntity
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.data.seed.model.SeedManifest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Оркестратор сидирования базы данных начальным контентом.
 *
 * Порядок сидирования строго определён иерархией зависимостей:
 * Grades → Sections → Topics → Objectives → MicroSkills
 * Затем независимые: Dictionary, Lessons, Questions
 * В конце — пользовательские данные: UserProgress.
 *
 * Сидирование идемпотентно: повторный запуск не дублирует данные
 * (проверяется через ContentVersionManager и количество записей в таблицах).
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val gradeDao: GradeDao,
    private val sectionDao: SectionDao,
    private val topicDao: TopicDao,
    private val objectiveDao: LearningObjectiveDao,
    private val microSkillDao: MicroSkillDao,
    private val dictionaryDao: DictionaryDao,
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val userProgressDao: UserProgressDao,
    private val lessonCompletionDao: LessonCompletionDao,
    private val loader: SeedLoader,
    private val manifestLoader: ManifestLoader,
    private val versionManager: ContentVersionManager
) {

    companion object {
        private const val TAG = "DatabaseSeeder"
    }

    /**
     * Главный метод сидирования.
     *
     * Алгоритм:
     * 1. Загружает манифест.
     * 2. Проверяет, требуется ли сидирование (схема или контент изменились).
     * 3. Если требуется — выполняет сидирование в правильном порядке.
     * 4. Сохраняет новые версии в ContentVersionManager.
     *
     * Безопасен для многократного вызова: повторное сидирование пропускается.
     */
    suspend fun seedDatabase() {
        Log.d(TAG, "Starting seed process...")

        val manifest = try {
            manifestLoader.load()
        } catch (e: ContentLoadException) {
            Log.w(TAG, "Manifest not found or invalid, skipping seed: ${e.message}")
            return
        }

        val savedSchemaVersion = versionManager.getSchemaVersion()
        val savedContentVersion = versionManager.getContentVersion()

        val needsSchemaMigration = manifest.requiresSchemaMigration(savedSchemaVersion)
        val needsContentUpdate = manifest.requiresContentUpdate(savedContentVersion)

        if (!needsSchemaMigration && !needsContentUpdate) {
            Log.d(
                TAG,
                "Content is up to date. Schema: $savedSchemaVersion, Content: $savedContentVersion"
            )
            return
        }

        if (needsSchemaMigration) {
            Log.d(
                TAG,
                "Schema migration required: $savedSchemaVersion → ${manifest.schemaVersion}"
            )
            performFullSeed(manifest)
        } else {
            Log.d(
                TAG,
                "Content update required: $savedContentVersion → ${manifest.contentVersion}"
            )
            performIncrementalUpdate(manifest)
        }

        versionManager.update(manifest.schemaVersion, manifest.contentVersion)
        Log.d(
            TAG,
            "Seed completed. Schema: ${manifest.schemaVersion}, Content: ${manifest.contentVersion}"
        )
    }

    /**
     * Полное сидирование всех таблиц.
     * Выполняется при первом запуске или изменении схемы.
     */
    private suspend fun performFullSeed(manifest: SeedManifest) {
        Log.d(TAG, "Performing full seed...")

        val enabledModules = manifest.getEnabledModules()

        // Проверка наличия минимально необходимых файлов
        validateRequiredFiles()

        seedGrades()
        seedSections()
        seedTopics()
        seedObjectives()
        seedMicroSkills()
        seedDictionary()
        seedLessons()
        seedQuestions()
        seedUserProgress()

        // Опциональные модули
        if (enabledModules.any { it.id == "lesson_completions" }) {
            seedLessonCompletions()
        }

        Log.d(TAG, "Full seed finished successfully")
    }

    /**
     * Инкрементальное обновление контента.
     * Загружает только модули, версия которых изменилась.
     */
    private suspend fun performIncrementalUpdate(manifest: SeedManifest) {
        Log.d(TAG, "Performing incremental update...")

        for (module in manifest.getEnabledModules()) {
            val savedVersion = versionManager.getModuleVersion(module.id)
            val moduleFile = module.archive

            if (savedVersion < manifest.contentVersion) {
                Log.d(TAG, "Updating module: ${module.id} (v$savedVersion → v${manifest.contentVersion})")
                seedModuleByName(module.id, moduleFile)
                versionManager.saveModuleVersion(module.id, manifest.contentVersion)
            }
        }

        Log.d(TAG, "Incremental update finished")
    }

    /**
     * Сидирует конкретный модуль по его идентификатору.
     */
    private suspend fun seedModuleByName(moduleId: String, fileName: String) {
        when (moduleId) {
            "grades" -> seedGrades()
            "sections" -> seedSections()
            "topics" -> seedTopics()
            "objectives" -> seedObjectives()
            "micro_skills" -> seedMicroSkills()
            "dictionary" -> seedDictionary()
            "lessons" -> seedLessons()
            "questions" -> seedQuestions()
            "lesson_completions" -> seedLessonCompletions()
            else -> Log.w(TAG, "Unknown module: $moduleId")
        }
    }

    /**
     * Проверяет наличие обязательных seed-файлов в assets.
     * Выбрасывает ContentLoadException, если какой-то файл отсутствует.
     */
    private fun validateRequiredFiles() {
        val requiredFiles = listOf(
            SeedConstants.GRADES,
            SeedConstants.SECTIONS,
            SeedConstants.TOPICS,
            SeedConstants.LESSONS,
            SeedConstants.QUESTIONS
        )

        for (file in requiredFiles) {
            if (!loader.fileExists(file.removePrefix("seed/"))) {
                throw ContentLoadException(
                    "Required seed file not found: $file. " +
                            "All required files must be present in assets/seed/ for initial setup."
                )
            }
        }
    }

    // ========================================================================
    // Методы сидирования отдельных таблиц
    // ========================================================================

    private suspend fun seedGrades() {
        if (gradeDao.count() > 0) {
            Log.d(TAG, "Grades already seeded, skipping")
            return
        }
        val list = loader.loadList<GradeEntity>(SeedConstants.GRADES)
        if (list.isNotEmpty()) {
            gradeDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} grades")
        } else {
            Log.w(TAG, "No grades found in seed file")
        }
    }

    private suspend fun seedSections() {
        if (sectionDao.countByGrade(listOf("1", "5", "11").first()) > 0) {
            Log.d(TAG, "Sections already seeded, skipping")
            return
        }
        val list = loader.loadList<SectionEntity>(SeedConstants.SECTIONS)
        if (list.isNotEmpty()) {
            sectionDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} sections")
        } else {
            Log.w(TAG, "No sections found in seed file")
        }
    }

    private suspend fun seedTopics() {
        if (topicDao.countByGrade("5") > 0) {
            Log.d(TAG, "Topics already seeded, skipping")
            return
        }
        val list = loader.loadList<TopicEntity>(SeedConstants.TOPICS)
        if (list.isNotEmpty()) {
            topicDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} topics")
        } else {
            Log.w(TAG, "No topics found in seed file")
        }
    }

    private suspend fun seedObjectives() {
        if (objectiveDao.countByTopic(listOf("topic_5_1").first()) > 0) {
            Log.d(TAG, "Objectives already seeded, skipping")
            return
        }
        val list = loader.loadList<LearningObjectiveEntity>(SeedConstants.OBJECTIVES)
        if (list.isNotEmpty()) {
            objectiveDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} objectives")
        } else {
            Log.d(TAG, "No objectives file found, skipping (optional)")
        }
    }

    private suspend fun seedMicroSkills() {
        if (microSkillDao.countByObjective(listOf("obj_5_1_1").first()) > 0) {
            Log.d(TAG, "MicroSkills already seeded, skipping")
            return
        }
        val list = loader.loadList<MicroSkillEntity>(SeedConstants.MICRO_SKILLS)
        if (list.isNotEmpty()) {
            microSkillDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} micro_skills")
        } else {
            Log.d(TAG, "No micro_skills file found, skipping (optional)")
        }
    }

    private suspend fun seedDictionary() {
        if (dictionaryDao.count() > 0) {
            Log.d(TAG, "Dictionary already seeded, skipping")
            return
        }
        val list = loader.loadList<DictionaryWordEntity>(SeedConstants.DICTIONARY)
        if (list.isNotEmpty()) {
            dictionaryDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} dictionary words")
        } else {
            Log.d(TAG, "No dictionary file found, skipping (optional)")
        }
    }

    private suspend fun seedLessons() {
        if (lessonDao.countByTopic("topic_5_1") > 0) {
            Log.d(TAG, "Lessons already seeded, skipping")
            return
        }
        val list = loader.loadList<LessonEntity>(SeedConstants.LESSONS)
        if (list.isNotEmpty()) {
            lessonDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} lessons")
        } else {
            Log.w(TAG, "No lessons found in seed file")
        }
    }

    private suspend fun seedQuestions() {
        if (questionDao.countByLesson(listOf("lesson_5_1_1").first()) > 0) {
            Log.d(TAG, "Questions already seeded, skipping")
            return
        }
        val list = loader.loadList<QuestionEntity>(SeedConstants.QUESTIONS)
        if (list.isNotEmpty()) {
            questionDao.insertAll(list)
            Log.d(TAG, "Seeded ${list.size} questions")
        } else {
            Log.w(TAG, "No questions found in seed file")
        }
    }

    private suspend fun seedUserProgress() {
        val existing = userProgressDao.getUserProgress()
        if (existing != null) {
            Log.d(TAG, "UserProgress already exists, skipping")
            return
        }
        val now = System.currentTimeMillis()
        userProgressDao.upsertProgress(
            UserProgressEntity(
                id = 1,
                totalXp = 0,
                currentLevel = 1,
                xpToNextLevel = 100,
                currentStreak = 0,
                longestStreak = 0,
                lastActiveDate = now,
                streakStartDate = 0,
                gemsBalance = 50,
                livesCount = 5,
                maxLives = 5,
                lastLifeRefillTime = now,
                totalLessonsCompleted = 0,
                totalMistakesCount = 0,
                totalTimeSpentSeconds = 0,
                totalPerfectLessons = 0,
                totalDaysActive = 0,
                currentGradeId = "",
                currentTopicId = "",
                onboardingCompleted = false,
                lastSyncTime = 0,
                schemaVersion = 1,
                createdAt = now,
                updatedAt = now
            )
        )
        Log.d(TAG, "UserProgress initialized")
    }

    private suspend fun seedLessonCompletions() {
        if (lessonCompletionDao.getTotalStars() > 0) {
            Log.d(TAG, "LessonCompletions already seeded, skipping")
            return
        }
        val list = loader.loadList<LessonCompletionEntity>(SeedConstants.LESSON_COMPLETIONS)
        if (list.isNotEmpty()) {
            lessonCompletionDao.saveCompletions(list)
            Log.d(TAG, "Seeded ${list.size} lesson completions")
        } else {
            Log.d(TAG, "No lesson completions file found, skipping (optional)")
        }
    }

    /**
     * Принудительное пересидирование (для тестов и отладки).
     * Не проверяет версии — просто перезаписывает данные.
     */
    suspend fun forceReseed() {
        Log.w(TAG, "Force reseeding all content...")
        performFullSeed(
            SeedManifest(
                schemaVersion = Int.MAX_VALUE,
                contentVersion = Int.MAX_VALUE,
                generated = "forced",
                modules = emptyList()
            )
        )
    }
}
