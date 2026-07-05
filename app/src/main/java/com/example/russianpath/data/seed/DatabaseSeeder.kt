// app/src/main/java/com/example/russianpath/data/seed/DatabaseSeeder.kt

package com.example.russianpath.data.seed

import android.util.Log
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.RuleDao
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
import com.example.russianpath.data.local.entity.RuleEntity
import com.example.russianpath.data.local.entity.SectionEntity
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.data.seed.model.SeedManifest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val gradeDao: GradeDao,
    private val sectionDao: SectionDao,
    private val topicDao: TopicDao,
    private val objectiveDao: LearningObjectiveDao,
    private val microSkillDao: MicroSkillDao,
    private val ruleDao: RuleDao,
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
            Log.d(TAG, "Content is up to date. Schema: $savedSchemaVersion, Content: $savedContentVersion")
            return
        }

        if (needsSchemaMigration) {
            Log.d(TAG, "Schema migration required: $savedSchemaVersion → ${manifest.schemaVersion}")
            performFullSeed(manifest)
        } else {
            Log.d(TAG, "Content update required: $savedContentVersion → ${manifest.contentVersion}")
            performIncrementalUpdate(manifest)
        }

        versionManager.update(manifest.schemaVersion, manifest.contentVersion)
        Log.d(TAG, "Seed completed. Schema: ${manifest.schemaVersion}, Content: ${manifest.contentVersion}")
    }

    private suspend fun performFullSeed(manifest: SeedManifest) {
        Log.d(TAG, "Performing full seed...")
        val enabledModules = manifest.getEnabledModules()
        validateRequiredFiles()

        seedGrades()
        seedSections()
        seedTopics()
        seedObjectives()
        seedMicroSkills()
        seedRules()
        seedDictionary()
        seedLessons()
        seedQuestions()
        seedUserProgress()

        if (enabledModules.any { it.id == "lesson_completions" }) {
            seedLessonCompletions()
        }

        Log.d(TAG, "Full seed finished successfully")
    }

    private suspend fun performIncrementalUpdate(manifest: SeedManifest) {
        Log.d(TAG, "Performing incremental update...")
        for (module in manifest.getEnabledModules()) {
            val savedVersion = versionManager.getModuleVersion(module.id)
            if (savedVersion < manifest.contentVersion) {
                Log.d(TAG, "Updating module: ${module.id} (v$savedVersion → v${manifest.contentVersion})")
                seedModuleByName(module.id, module.archive)
                versionManager.saveModuleVersion(module.id, manifest.contentVersion)
            }
        }
        Log.d(TAG, "Incremental update finished")
    }

    private suspend fun seedModuleByName(moduleId: String, fileName: String) {
        when (moduleId) {
            "grades" -> seedGrades()
            "sections" -> seedSections()
            "topics" -> seedTopics()
            "objectives" -> seedObjectives()
            "micro_skills" -> seedMicroSkills()
            "rules" -> seedRules()
            "dictionary" -> seedDictionary()
            "lessons" -> seedLessons()
            "questions" -> seedQuestions()
            "lesson_completions" -> seedLessonCompletions()
            else -> Log.w(TAG, "Unknown module: $moduleId")
        }
    }

    private fun validateRequiredFiles() {
        val requiredFiles = listOf(SeedConstants.GRADES, SeedConstants.SECTIONS, SeedConstants.TOPICS, SeedConstants.LESSONS, SeedConstants.QUESTIONS)
        for (file in requiredFiles) {
            if (!loader.fileExists(file.removePrefix("seed/"))) {
                throw ContentLoadException("Required seed file not found: $file.")
            }
        }
    }

    private suspend fun seedGrades() {
        if (gradeDao.count() > 0) { Log.d(TAG, "Grades already seeded, skipping"); return }
        val list = loader.loadList<GradeEntity>(SeedConstants.GRADES)
        val valid = list.filter { it.id.isNotBlank() }
        if (valid.isNotEmpty()) { gradeDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} grades") }
        else Log.w(TAG, "No valid grades found")
    }

    private suspend fun seedSections() {
        if (sectionDao.countByGrade("5") > 0) { Log.d(TAG, "Sections already seeded, skipping"); return }
        val list = loader.loadList<SectionEntity>(SeedConstants.SECTIONS)
        val valid = list.filter { it.id.isNotBlank() && it.gradeId.isNotBlank() }
        if (valid.isNotEmpty()) { sectionDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} sections") }
        else Log.w(TAG, "No valid sections found")
    }

    private suspend fun seedTopics() {
        if (topicDao.countByGrade("5") > 0) { Log.d(TAG, "Topics already seeded, skipping"); return }
        val list = loader.loadList<TopicEntity>(SeedConstants.TOPICS)
        val valid = list.filter { it.id.isNotBlank() && it.sectionId.isNotBlank() }
        if (valid.isNotEmpty()) { topicDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} topics") }
        else Log.w(TAG, "No valid topics found")
    }

    private suspend fun seedObjectives() {
        val list = loader.loadList<LearningObjectiveEntity>(SeedConstants.OBJECTIVES)
        Log.d(TAG, "Loaded ${list.size} objectives from JSON")
        val valid = list.filter { it.id.isNotBlank() && it.topicId.isNotBlank() }
        Log.d(TAG, "Valid objectives after filter: ${valid.size}")
        if (valid.isNotEmpty()) { objectiveDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} objectives") }
        else Log.d(TAG, "No objectives found, skipping (optional)")
    }

    private suspend fun seedMicroSkills() {
        val list = loader.loadList<MicroSkillEntity>(SeedConstants.MICRO_SKILLS)
        Log.d(TAG, "Loaded ${list.size} micro_skills from JSON")
        val valid = list.filter { it.id.isNotBlank() && it.objectiveId.isNotBlank() }
        Log.d(TAG, "Valid micro_skills after filter: ${valid.size}")
        if (valid.isNotEmpty()) {
            try {
                microSkillDao.insertAll(valid)
                Log.d(TAG, "Seeded ${valid.size} micro_skills")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to seed micro_skills: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "No valid micro_skills found, skipping")
        }
    }

    private suspend fun seedRules() {
        if (ruleDao.countByTopic("topic_5_4") > 0) { Log.d(TAG, "Rules already seeded, skipping"); return }
        val list = loader.loadList<RuleEntity>(SeedConstants.RULES)
        val valid = list.filter { it.id.isNotBlank() }
        if (valid.isNotEmpty()) { ruleDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} rules") }
        else Log.d(TAG, "No rules found, skipping (optional)")
    }

    private suspend fun seedDictionary() {
        if (dictionaryDao.count() > 0) { Log.d(TAG, "Dictionary already seeded, skipping"); return }
        val list = loader.loadList<DictionaryWordEntity>(SeedConstants.DICTIONARY)
        val valid = list.filter { it.id.isNotBlank() && it.normalized.isNotBlank() }
        if (valid.isNotEmpty()) { dictionaryDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} dictionary words") }
        else Log.d(TAG, "No dictionary words found, skipping (optional)")
    }

    private suspend fun seedLessons() {
        if (lessonDao.countByTopic("topic_5_1") > 0) { Log.d(TAG, "Lessons already seeded, skipping"); return }
        val list = loader.loadList<LessonEntity>(SeedConstants.LESSONS)
        val preparedList = list.map { lesson ->
            lesson.copy(
                primaryObjectiveId = if (lesson.primaryObjectiveId.isNullOrBlank()) null else lesson.primaryObjectiveId
            )
        }
        val valid = preparedList.filter { it.id.isNotBlank() && it.topicId.isNotBlank() }
        if (valid.isNotEmpty()) { lessonDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} lessons") }
        else Log.w(TAG, "No valid lessons found")
    }

    private suspend fun seedQuestions() {
        if (questionDao.countByLesson("lesson_5_1_1") > 0) { Log.d(TAG, "Questions already seeded, skipping"); return }
        val list = loader.loadList<QuestionEntity>(SeedConstants.QUESTIONS)
        val preparedList = list.map { question ->
            question.copy(
                primarySkillId = if (question.primarySkillId.isNullOrBlank()) null else question.primarySkillId,
                questionType = question.questionType.ifBlank { "single_choice" }
            )
        }
        val valid = preparedList.filter { it.id.isNotBlank() && it.lessonId.isNotBlank() }
        if (valid.isNotEmpty()) { questionDao.insertAll(valid); Log.d(TAG, "Seeded ${valid.size} questions") }
        else Log.w(TAG, "No valid questions found")
    }

    private suspend fun seedUserProgress() {
        val existing = userProgressDao.getUserProgress()
        if (existing != null) { Log.d(TAG, "UserProgress already exists, skipping"); return }
        val now = System.currentTimeMillis()
        userProgressDao.upsertProgress(
            UserProgressEntity(
                id = 1, totalXp = 0, currentLevel = 1, xpToNextLevel = 100,
                currentStreak = 0, longestStreak = 0, lastActiveDate = now, streakStartDate = 0,
                gemsBalance = 50, livesCount = 5, maxLives = 5, lastLifeRefillTime = now,
                totalLessonsCompleted = 0, totalMistakesCount = 0, totalTimeSpentSeconds = 0,
                totalPerfectLessons = 0, totalDaysActive = 0,
                currentGradeId = "", currentTopicId = "", onboardingCompleted = false,
                lastSyncTime = 0, schemaVersion = 1, createdAt = now, updatedAt = now
            )
        )
        Log.d(TAG, "UserProgress initialized")
    }

    private suspend fun seedLessonCompletions() {
        if (lessonCompletionDao.getTotalStars() > 0) { Log.d(TAG, "LessonCompletions already seeded, skipping"); return }
        val list = loader.loadList<LessonCompletionEntity>(SeedConstants.LESSON_COMPLETIONS)
        val valid = list.filter { it.id.isNotBlank() && it.lessonId.isNotBlank() }
        if (valid.isNotEmpty()) { lessonCompletionDao.saveCompletions(valid); Log.d(TAG, "Seeded ${valid.size} lesson completions") }
        else Log.d(TAG, "No lesson completions found, skipping (optional)")
    }

    suspend fun forceReseed() {
        Log.w(TAG, "Force reseeding all content...")
        performFullSeed(SeedManifest(schemaVersion = Int.MAX_VALUE, contentVersion = Int.MAX_VALUE, generated = "forced", modules = emptyList()))
    }
}
