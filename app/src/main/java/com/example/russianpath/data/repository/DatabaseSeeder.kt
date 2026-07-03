package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.GradeDao
import com.example.russianpath.data.local.dao.SectionDao
import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.GradeEntity
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
    private val userProgressDao: UserProgressDao
) {

    suspend fun seed() {

        // Уже инициализировано
        if (gradeDao.getById("grade5") != null) {
            return
        }

        // ---------- КЛАССЫ ----------

        gradeDao.insertAll(
            listOf(
                GradeEntity(
                    id = "grade5",
                    name = "5 класс",
                    sortOrder = 5
                )
            )
        )

        // ---------- РАЗДЕЛЫ ----------

        sectionDao.insertAll(
            listOf(
                SectionEntity(
                    id = "phonetics",
                    gradeId = "grade5",
                    name = "Фонетика",
                    sortOrder = 1
                ),
                SectionEntity(
                    id = "spelling",
                    gradeId = "grade5",
                    name = "Орфография",
                    sortOrder = 2
                ),
                SectionEntity(
                    id = "speech",
                    gradeId = "grade5",
                    name = "Развитие речи",
                    sortOrder = 3
                )
            )
        )

        // ---------- ТЕМЫ ----------

        topicDao.insertAll(
            listOf(
                TopicEntity(
                    id = "topic_letters",
                    sectionId = "phonetics",
                    gradeLevel = 5,
                    title = "Гласные буквы",
                    description = "Изучаем гласные буквы русского языка",
                    iconName = "letters",
                    sortOrder = 1,
                    isUnlocked = true
                ),

                TopicEntity(
                    id = "topic_sounds",
                    sectionId = "phonetics",
                    gradeLevel = 5,
                    title = "Звуки речи",
                    description = "Учимся различать звуки",
                    iconName = "sound",
                    sortOrder = 2,
                    isUnlocked = false
                ),

                TopicEntity(
                    id = "topic_stress",
                    sectionId = "phonetics",
                    gradeLevel = 5,
                    title = "Ударение",
                    description = "Правильная постановка ударения",
                    iconName = "stress",
                    sortOrder = 3,
                    isUnlocked = false
                ),

                TopicEntity(
                    id = "topic_root",
                    sectionId = "spelling",
                    gradeLevel = 5,
                    title = "Корень слова",
                    description = "Учимся находить корень",
                    iconName = "root",
                    sortOrder = 4,
                    isUnlocked = false
                ),

                TopicEntity(
                    id = "topic_sentence",
                    sectionId = "speech",
                    gradeLevel = 5,
                    title = "Предложения",
                    description = "Строим предложения",
                    iconName = "sentence",
                    sortOrder = 5,
                    isUnlocked = false
                )
            )
        )

        // ---------- ПРОФИЛЬ ИГРОКА ----------

        userProgressDao.updateProgress(
            UserProgressEntity(
                id = 1,
                totalXp = 0,
                currentStreak = 0,
                longestStreak = 0,
                lastActiveDate = System.currentTimeMillis(),
                gemsBalance = 100,
                livesCount = 5,
                totalLessonsCompleted = 0,
                totalMistakesCount = 0
            )
        )
    }
}
