package com.example.russianpath.data.seed

import android.util.Log
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.dao.SectionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import com.example.russianpath.data.local.entity.GradeEntity
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.local.entity.SectionEntity
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.data.local.entity.UserProgressEntity
import javax.inject.Inject
import javax.inject.Singleton

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

    private val loader: SeedLoader

) {

    suspend fun seedDatabase() {

        // Уже заполнена?
        val alreadySeeded = gradeDao.observeAll()

        var hasData = false

        alreadySeeded.collect { list ->
            hasData = list.isNotEmpty()
        }

        if (hasData) {
            Log.d("Seeder", "Database already seeded")
            return
        }

        Log.d("Seeder", "Start seeding...")

        seedGrades()
        seedSections()
        seedTopics()
        seedObjectives()
        seedMicroSkills()
        seedDictionary()
        seedLessons()
        seedQuestions()
        seedUser()

        Log.d("Seeder", "Seeding finished")
    }

    private suspend fun seedGrades() {
        val list =
            loader.loadList<GradeEntity>("grades.json")

        gradeDao.insertAll(list)
    }

    private suspend fun seedSections() {
        val list =
            loader.loadList<SectionEntity>("sections.json")

        sectionDao.insertAll(list)
    }

    private suspend fun seedTopics() {
        val list =
            loader.loadList<TopicEntity>("topics.json")

        topicDao.insertAll(list)
    }

    private suspend fun seedObjectives() {
        val list =
            loader.loadList<LearningObjectiveEntity>("objectives.json")

        objectiveDao.insertAll(list)
    }

    private suspend fun seedMicroSkills() {
        val list =
            loader.loadList<MicroSkillEntity>("micro_skills.json")

        microSkillDao.insertAll(list)
    }

    private suspend fun seedDictionary() {
        val list =
            loader.loadList<DictionaryWordEntity>("dictionary.json")

        dictionaryDao.insertAll(list)
    }

    private suspend fun seedLessons() {
        val list =
            loader.loadList<LessonEntity>("lessons.json")

        lessonDao.insertAll(list)
    }

    private suspend fun seedQuestions() {
        val list =
            loader.loadList<QuestionEntity>("questions.json")

        questionDao.insertAll(list)
    }

    private suspend fun seedUser() {

        userProgressDao.updateProgress(

            UserProgressEntity(
                id = 1,
                totalXp = 0,
                currentStreak = 0,
                longestStreak = 0,
                lastActiveDate = System.currentTimeMillis(),
                gemsBalance = 50,
                livesCount = 5,
                totalLessonsCompleted = 0,
                totalMistakesCount = 0
            )

        )
    }

}
