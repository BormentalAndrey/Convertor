package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.ChoiceAnswer
import com.example.russianpath.core.exercise.ChoiceUserAnswer
import com.example.russianpath.core.exercise.Exercise
import com.example.russianpath.core.exercise.TextAnswer
import com.example.russianpath.core.exercise.TextUserAnswer
import com.example.russianpath.core.progress.AnswerEvaluator
import com.example.russianpath.core.progress.AnswerResult
import com.example.russianpath.core.progress.UserAnswer
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerEvaluatorImpl @Inject constructor() : AnswerEvaluator {

    override fun evaluate(exercise: Exercise, answer: UserAnswer): AnswerResult {
        val isCorrect = when (exercise.correctAnswer) {
            is TextAnswer -> {
                val userText = (answer as? TextUserAnswer)?.text ?: ""
                userText.equals(exercise.correctAnswer.value, ignoreCase = true)
            }
            is ChoiceAnswer -> {
                val userIndex = (answer as? ChoiceUserAnswer)?.index ?: -1
                userIndex == exercise.correctAnswer.index
            }
        }

        return AnswerResult(
            exerciseId = exercise.id,
            skillCode = exercise.fingerprint.skillCode,
            userAnswer = answer,
            isCorrect = isCorrect,
            timeSpentMs = 0L,
            timestamp = Instant.now()
        )
    }
}
